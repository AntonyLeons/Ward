use crate::models::SetupDto;
use ini::Ini;
use std::path::Path;

/// ConfigManager handles reading and writing the setup.ini file.
pub struct ConfigManager {
    pub file_path: String,
}

impl ConfigManager {
    /// Create a new ConfigManager with the given file path.
    pub fn new(file_path: &str) -> Self {
        Self {
            file_path: file_path.to_string(),
        }
    }

    /// Check if the application has been configured by verifying the ini file exists.
    pub fn is_configured(&self) -> bool {
        Path::new(&self.file_path).exists()
    }

    /// Read the current configuration from the ini file.
    pub fn read_config(&self) -> Option<SetupDto> {
        if !self.is_configured() {
            return None;
        }

        let i = Ini::load_from_file(&self.file_path).ok()?;
        let section = i.section(Some("setup"))?;

        let server_name = section.get("serverName").unwrap_or("Ward").to_string();
        let theme = section.get("theme").unwrap_or("light").to_string();
        let port = section.get("port").unwrap_or("4000").to_string();
        let enable_fog = section.get("enableFog").unwrap_or("true").to_string();
        let background_color = section.get("backgroundColor").unwrap_or("default").to_string();

        Some(SetupDto {
            server_name,
            theme,
            port,
            enable_fog,
            background_color,
        })
    }

    /// Write the configuration to the ini file.
    pub fn write_config(&self, setup_dto: &SetupDto) -> Result<(), String> {
        let mut conf = Ini::new();
        conf.with_section(Some("setup"))
            .set("serverName", &setup_dto.server_name)
            .set("theme", &setup_dto.theme)
            .set("port", &setup_dto.port)
            .set("enableFog", &setup_dto.enable_fog)
            .set("backgroundColor", &setup_dto.background_color);

        conf.write_to_file(&self.file_path)
            .map_err(|e| e.to_string())
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::fs;

    #[test]
    fn test_config_manager() {
        let path = "test_setup.ini";
        let _ = fs::remove_file(path); // Ensure clean state
        
        let config_manager = ConfigManager::new(path);
        
        // Should not be configured initially
        assert!(!config_manager.is_configured());
        assert!(config_manager.read_config().is_none());

        let setup_dto = SetupDto {
            server_name: "TestServer".to_string(),
            theme: "dark".to_string(),
            port: "4000".to_string(),
            enable_fog: "false".to_string(),
            background_color: "#123456".to_string(),
        };

        // Write config
        assert!(config_manager.write_config(&setup_dto).is_ok());
        assert!(config_manager.is_configured());

        // Read config
        let read_dto = config_manager.read_config().unwrap();
        assert_eq!(read_dto.server_name, "TestServer");
        assert_eq!(read_dto.theme, "dark");
        assert_eq!(read_dto.port, "4000");
        assert_eq!(read_dto.enable_fog, "false");
        assert_eq!(read_dto.background_color, "#123456");

        // Clean up
        let _ = fs::remove_file(path);
    }
}
