package dev.leons.ward.services;

import dev.leons.ward.Ward;
import dev.leons.ward.components.UtilitiesComponent;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Props;
import org.noear.solon.Solon;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ConfigurationService manages application configuration loading and reloading
 * without requiring a full application restart
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Slf4j
@Component
public class ConfigurationService {

    @Inject
    private UtilitiesComponent utilitiesComponent;

    private Map<String, String> currentConfiguration = new HashMap<>();

    /**
     * Loads configuration from setup.ini file
     */
    public void loadConfiguration() {
        try {
            File setupFile = new File(Ward.SETUP_FILE_PATH);
            if (setupFile.exists()) {
                // Load configuration from INI file
                String serverName = utilitiesComponent.getFromIniFile("serverName");
                String theme = utilitiesComponent.getFromIniFile("theme");
                String port = utilitiesComponent.getFromIniFile("port");
                String enableFog = utilitiesComponent.getFromIniFile("enableFog");
                String backgroundColor = utilitiesComponent.getFromIniFile("backgroundColor");

                // Store in current configuration
                if (serverName != null) currentConfiguration.put("serverName", serverName);
                if (theme != null) currentConfiguration.put("theme", theme);
                if (port != null) currentConfiguration.put("port", port);
                if (enableFog != null) currentConfiguration.put("enableFog", enableFog);
                if (backgroundColor != null) currentConfiguration.put("backgroundColor", backgroundColor);

                // Apply port configuration if different from current
                applyPortConfiguration(port);

                log.info("Configuration loaded successfully from {}", Ward.SETUP_FILE_PATH);
            } else {
                log.warn("Setup file {} does not exist", Ward.SETUP_FILE_PATH);
            }
        } catch (IOException e) {
            log.error("Failed to load configuration: {}", e.getMessage());
        }
    }

    /**
     * Applies port configuration if it differs from the current port
     */
    private void applyPortConfiguration(String portStr) {
        if (portStr != null && !portStr.trim().isEmpty()) {
            try {
                int newPort = Integer.parseInt(portStr.trim());
                int currentPort = Solon.cfg().getInt("server.port", Ward.INITIAL_PORT);
                
                if (newPort != currentPort) {
                    log.info("Port configuration changed from {} to {}. Note: Port change requires application restart.", currentPort, newPort);
                    // For port changes, we can update the configuration but it won't take effect until restart
                    // This is a limitation of most web servers - port binding happens at startup
                    Props props = Solon.cfg();
                    props.put("server.port", String.valueOf(newPort));
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid port configuration: {}", portStr);
            }
        }
    }

    /**
     * Gets a configuration value
     */
    public String getConfigurationValue(String key) {
        return currentConfiguration.get(key);
    }

    /**
     * Gets all current configuration
     */
    public Map<String, String> getCurrentConfiguration() {
        return new HashMap<>(currentConfiguration);
    }

    /**
     * Checks if configuration is loaded
     */
    public boolean isConfigurationLoaded() {
        return !currentConfiguration.isEmpty();
    }

    /**
     * Reloads configuration and updates the application state
     */
    public void reloadConfiguration() {
        currentConfiguration.clear();
        loadConfiguration();
        Ward.setFirstLaunch(false);
        log.info("Configuration reloaded successfully");
    }
}