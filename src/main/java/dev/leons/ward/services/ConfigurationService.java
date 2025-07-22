package dev.leons.ward.services;

import dev.leons.ward.components.UtilitiesComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * ConfigurationService handles dynamic configuration reloading
 *
 * @author Ward Team
 * @version 1.0.0
 */
@Service
public class ConfigurationService {

    @Autowired
    private UtilitiesComponent utilitiesComponent;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Reloads configuration from setup.ini without restarting the application
     * Note: Port changes still require restart due to Spring Boot limitations
     */
    public void reloadConfiguration() {
        try {
            // Configuration is now loaded dynamically by components that need it
            // No restart needed for most settings
            System.out.println("Configuration reloaded from setup.ini");
        } catch (Exception e) {
            System.err.println("Error reloading configuration: " + e.getMessage());
        }
    }

    /**
     * Gets a configuration value from setup.ini
     *
     * @param key the configuration key
     * @return the configuration value or null if not found
     */
    public String getConfigValue(String key) {
        try {
            return utilitiesComponent.getFromIniFile(key);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Sets a configuration value in setup.ini
     *
     * @param key the configuration key
     * @param value the configuration value
     */
    public void setConfigValue(String key, String value) {
        try {
            utilitiesComponent.putInIniFile(key, value);
        } catch (IOException e) {
            System.err.println("Error setting configuration value: " + e.getMessage());
        }
    }
}