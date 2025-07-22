package dev.leons.ward;

import dev.leons.ward.components.UtilitiesComponent;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import oshi.SystemInfo;

/**
 * Test configuration class that provides mock beans for testing
 */
@TestConfiguration
public class TestConfig {

    /**
     * Creates a mock SystemInfo bean for testing
     * 
     * @return mocked SystemInfo instance
     */
    @Bean
    @Primary
    public SystemInfo systemInfo() {
        return Mockito.mock(SystemInfo.class);
    }
    
    /**
     * Creates a mock UtilitiesComponent bean for testing
     * 
     * @return mocked UtilitiesComponent instance
     */
    @Bean
    @Primary
    public UtilitiesComponent utilitiesComponent() {
        return Mockito.mock(UtilitiesComponent.class);
    }
}