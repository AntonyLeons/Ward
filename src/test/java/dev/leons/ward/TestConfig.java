package dev.leons.ward;

import dev.leons.ward.components.UtilitiesComponent;
import org.mockito.Mockito;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import oshi.SystemInfo;

/**
 * Test configuration class that provides mock beans for testing
 */
@Configuration
public class TestConfig {

    /**
     * Creates a mock SystemInfo bean for testing
     * 
     * @return mocked SystemInfo instance
     */
    @Bean
    public SystemInfo systemInfo() {
        return Mockito.mock(SystemInfo.class);
    }
    
    /**
     * Creates a mock UtilitiesComponent bean for testing
     * 
     * @return mocked UtilitiesComponent instance
     */
    @Bean
    public UtilitiesComponent utilitiesComponent() {
        return Mockito.mock(UtilitiesComponent.class);
    }
}