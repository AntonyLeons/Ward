package dev.leons.ward.components;

import dev.leons.ward.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ServletComponent used for application port changing
 * Note: Port changes still require application restart due to Spring Boot limitations
 * @author Rudolf Barbu
 * @version 1.0.4
 */
@Component
public class ServletComponent implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>
{
    /**
     * Autowired UtilitiesComponent object
     * Used for various utility functions
     */
    @Autowired
    private UtilitiesComponent utilitiesComponent;

    /**
     * Customizes port of application
     * This is called during application startup to set the initial port
     *
     * @param tomcatServletWebServerFactory servlet factory
     */
    @Override
    public void customize(final TomcatServletWebServerFactory tomcatServletWebServerFactory)
    {
        if (!Ward.isFirstLaunch())
        {
            try
            {
                String portStr = utilitiesComponent.getFromIniFile("port");
                if (portStr != null) {
                    tomcatServletWebServerFactory.setPort(Integer.parseInt(portStr));
                } else {
                    tomcatServletWebServerFactory.setPort(Ward.INITIAL_PORT);
                }
            }
            catch (IOException | NumberFormatException exception)
            {
                // Fall back to default port if configuration is invalid
                tomcatServletWebServerFactory.setPort(Ward.INITIAL_PORT);
                exception.printStackTrace();
            }
        }
        else
        {
            tomcatServletWebServerFactory.setPort(Ward.INITIAL_PORT);
        }
    }
}