package dev.leons.ward.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oshi.SystemInfoFFM;

/**
 * BeanConfiguration provides bean configuration for classes, which are not components
 *
 * @author Rudolf Barbu
 * @version 1.0.2
 */
@Configuration
public class BeanConfiguration
{
    /**
     * @return SystemInfo object
     */
    @Bean
    public SystemInfoFFM systemInfo()
    {
        return new SystemInfoFFM();
    }
}