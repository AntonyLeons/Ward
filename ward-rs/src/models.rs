use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Clone)]
pub struct UsageDto {
    pub processor: i32,
    pub ram: i32,
    pub storage: i32,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct ProcessorDto {
    pub name: String,
    #[serde(rename = "coreCount")]
    pub core_count: String,
    #[serde(rename = "clockSpeed")]
    pub clock_speed: String,
    #[serde(rename = "bitDepth")]
    pub bit_depth: String,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct MachineDto {
    #[serde(rename = "operatingSystem")]
    pub operating_system: String,
    #[serde(rename = "totalRam")]
    pub total_ram: String,
    #[serde(rename = "ramTypeOrOSBitDepth")]
    pub ram_type_or_os_bit_depth: String,
    #[serde(rename = "procCount")]
    pub proc_count: String,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct StorageDto {
    #[serde(rename = "mainStorage")]
    pub main_storage: String,
    pub total: String,
    #[serde(rename = "diskCount")]
    pub disk_count: String,
    #[serde(rename = "swapAmount")]
    pub swap_amount: String,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct InfoDto {
    pub processor: ProcessorDto,
    pub machine: MachineDto,
    pub storage: StorageDto,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct UptimeDto {
    pub days: String,
    pub hours: String,
    pub minutes: String,
    pub seconds: String,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct SetupDto {
    #[serde(rename = "serverName")]
    pub server_name: String,
    pub theme: String,
    pub port: String,
    #[serde(rename = "enableFog")]
    pub enable_fog: String,
    #[serde(rename = "backgroundColor")]
    pub background_color: String,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct ResponseDto {
    pub message: String,
}

#[derive(Serialize, Deserialize, Clone)]
pub struct ErrorDto {
    pub message: String,
    pub exception: String,
    pub timestamp: String,
}
