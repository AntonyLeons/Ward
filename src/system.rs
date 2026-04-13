use crate::models::*;
use sysinfo::{System, Disks};
use std::sync::Mutex;
use std::time::Instant;

/// SystemMonitor wraps sysinfo to provide real-time hardware metrics.
pub struct SystemMonitor {
    sys: Mutex<System>,
    disks: Mutex<Disks>,
    last_update: Mutex<Instant>,
}

impl SystemMonitor {
    /// Initialize a new SystemMonitor instance
    pub fn new() -> Self {
        let mut sys = System::new_all();
        sys.refresh_all();
        let disks = Disks::new_with_refreshed_list();
        Self {
            sys: Mutex::new(sys),
            disks: Mutex::new(disks),
            last_update: Mutex::new(Instant::now()),
        }
    }

    /// Refresh hardware metrics if they haven't been updated recently.
    fn refresh_if_needed(&self) {
        let mut last_update = self.last_update.lock().unwrap();
        if last_update.elapsed().as_secs() >= 1 {
            let mut sys = self.sys.lock().unwrap();
            let mut disks = self.disks.lock().unwrap();
            sys.refresh_all();
            disks.refresh(true);
            *last_update = Instant::now();
        }
    }

    fn get_converted_capacity(bytes: u64) -> String {
        let bits = bytes as f64 * 8.0;
        if (bits / 1.049e6) > 999.0 {
            if (bits / 1.074e9) > 999.0 {
                format!("{:.1} TiB", bits / 1.1e12)
            } else {
                format!("{} GiB", (bits / 1.074e9).round())
            }
        } else {
            format!("{} MiB", (bits / 1.049e6).round())
        }
    }

    /// Try to determine the physical hardware model of the main storage drive
    fn get_hardware_storage_model() -> String {
        #[cfg(target_os = "windows")]
        {
            if let Ok(output) = std::process::Command::new("wmic")
                .args(&["diskdrive", "get", "model"])
                .output()
            {
                let stdout = String::from_utf8_lossy(&output.stdout);
                let mut lines = stdout.lines().map(|l| l.trim()).filter(|l| !l.is_empty());
                lines.next(); // Skip "Model" header
                if let Some(model) = lines.next() {
                    return model.to_string();
                }
            }
        }
        
        #[cfg(target_os = "linux")]
        {
            if let Ok(entries) = std::fs::read_dir("/sys/block") {
                for entry in entries.flatten() {
                    let name = entry.file_name();
                    let name_str = name.to_string_lossy();
                    if name_str.starts_with("loop") || name_str.starts_with("ram") || name_str.starts_with("fd") || name_str.starts_with("sr") {
                        continue;
                    }
                    let model_path = entry.path().join("device/model");
                    if let Ok(model) = std::fs::read_to_string(model_path) {
                        let trimmed = model.trim().to_string();
                        if !trimmed.is_empty() {
                            return trimmed;
                        }
                    }
                }
            }
        }
        
        #[cfg(target_os = "macos")]
        {
            if let Ok(output) = std::process::Command::new("system_profiler")
                .args(&["SPStorageDataType"])
                .output()
            {
                let stdout = String::from_utf8_lossy(&output.stdout);
                for line in stdout.lines() {
                    let trimmed = line.trim();
                    if trimmed.starts_with("Device Name:") || trimmed.starts_with("Media Name:") {
                        let parts: Vec<&str> = trimmed.split(':').collect();
                        if parts.len() == 2 {
                            let model = parts[1].trim().to_string();
                            if !model.is_empty() && model != "APPLE SSD" {
                                return model;
                            }
                        }
                    }
                }
            }
        }
        
        "Undefined".to_string()
    }

    /// Get static hardware information.
    pub fn get_info(&self) -> InfoDto {
        self.refresh_if_needed();
        let sys = self.sys.lock().unwrap();
        let disks = self.disks.lock().unwrap();

        let cpu = sys.cpus().first().unwrap();
        let cpu_brand = cpu.brand().split('@').next().unwrap_or("Unknown").trim().to_string();
        let cpu_name = if cpu_brand.is_empty() { cpu.name().to_string() } else { cpu_brand };
        
        let core_count = sys.cpus().len();
        let core_count_str = format!("{} {}", core_count, if core_count > 1 { "Cores" } else { "Core" });
        
        let cpu_freq = format!("{:.1} GHz", cpu.frequency() as f64 / 1000.0);
        let cpu_bit_depth = if cfg!(target_pointer_width = "64") { "64-bit" } else { "32-bit" }.to_string();

        let processor = ProcessorDto {
            name: cpu_name,
            core_count: core_count_str,
            clock_speed: cpu_freq,
            bit_depth: cpu_bit_depth.clone(),
        };

        let os_name = System::name().unwrap_or_else(|| "Unknown".to_string());
        let os_version = System::os_version().unwrap_or_else(|| "".to_string());
        let operating_system = format!("{} {}", os_name, os_version);

        let total_ram_bytes = sys.total_memory();
        let total_ram_formatted = format!("{} RAM", Self::get_converted_capacity(total_ram_bytes));
        
        let proc_count = sys.processes().len();
        let proc_count_str = format!("{} {}", proc_count, if proc_count > 1 { "Procs" } else { "Proc" });

        let machine = MachineDto {
            operating_system,
            total_ram: total_ram_formatted,
            ram_type_or_os_bit_depth: cpu_bit_depth.clone(), // sysinfo doesn't easily provide RAM DDR generation, fallback to bit-depth like Java version
            proc_count: proc_count_str,
        };

        let mut total_storage_bytes = 0;
        for disk in disks.list() {
            total_storage_bytes += disk.total_space();
        }

        let mut main_storage = Self::get_hardware_storage_model();
        if main_storage == "Undefined" {
            for disk in disks.list() {
                let name = disk.name().to_string_lossy().to_string();
                if !name.is_empty() {
                    main_storage = name;
                    break;
                }
            }
            if main_storage == "Undefined" {
                main_storage = "Disk".to_string();
            }
        }
        
        let storage_total_formatted = format!("{} Total", Self::get_converted_capacity(total_storage_bytes));
        let disk_count = disks.list().len();
        let disk_count_str = format!("{} {}", disk_count, if disk_count > 1 { "Disks" } else { "Disk" });

        // Sysinfo changed Windows swap behavior recently: it now returns ONLY the swap/paging file size 
        // directly in `sys.total_swap()`! My manual subtraction of physical RAM broke it and caused it to show 0.
        let swap_bytes = sys.total_swap();
        
        let swap_amount = format!("{} Swap", Self::get_converted_capacity(swap_bytes));

        let storage = StorageDto {
            main_storage,
            total: storage_total_formatted,
            disk_count: disk_count_str,
            swap_amount,
        };

        InfoDto { processor, machine, storage }
    }

    /// Get current dynamic hardware usage (CPU, RAM, Storage).
    pub fn get_usage(&self) -> UsageDto {
        let mut sys = self.sys.lock().unwrap();
        let mut disks = self.disks.lock().unwrap();
        let mut last_update = self.last_update.lock().unwrap();

        // Refresh system to get latest metrics
        sys.refresh_all();
        disks.refresh(true);
        *last_update = Instant::now();

        // Wait 1 second and refresh CPU specifically for accurate delta and to throttle frontend updates
        std::thread::sleep(std::time::Duration::from_millis(1000));
        sys.refresh_cpu_usage();

        let cpu_usage: f32 = sys.global_cpu_usage();
        
        let total_ram = sys.total_memory();
        let used_ram = sys.used_memory();
        let ram_usage = if total_ram > 0 {
            ((used_ram as f64 / total_ram as f64) * 100.0) as i32
        } else {
            0
        };

        let mut total_storage = 0;
        let mut used_storage = 0;
        for disk in disks.list() {
            total_storage += disk.total_space();
            used_storage += disk.total_space() - disk.available_space();
        }
        
        let storage_usage = if total_storage > 0 {
            ((used_storage as f64 / total_storage as f64) * 100.0) as i32
        } else {
            0
        };

        UsageDto {
            processor: cpu_usage as i32,
            ram: ram_usage,
            storage: storage_usage,
        }
    }

    /// Get system uptime.
    pub fn get_uptime(&self) -> UptimeDto {
        let uptime_secs = System::uptime();
        
        let days = uptime_secs / 86400;
        let hours = (uptime_secs % 86400) / 3600;
        let minutes = (uptime_secs % 3600) / 60;
        let seconds = uptime_secs % 60;

        UptimeDto {
            days: format!("{:02}", days),
            hours: format!("{:02}", hours),
            minutes: format!("{:02}", minutes),
            seconds: format!("{:02}", seconds),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_system_monitor() {
        let monitor = SystemMonitor::new();
        
        let info = monitor.get_info();
        assert!(!info.processor.name.is_empty());
        assert!(!info.machine.operating_system.is_empty());
        
        let usage = monitor.get_usage();
        assert!(usage.processor >= 0 && usage.processor <= 100);
        assert!(usage.ram >= 0 && usage.ram <= 100);
        assert!(usage.storage >= 0 && usage.storage <= 100);

        let uptime = monitor.get_uptime();
        assert!(!uptime.days.is_empty());
        assert!(!uptime.hours.is_empty());
        assert!(!uptime.minutes.is_empty());
        assert!(!uptime.seconds.is_empty());
    }
}
