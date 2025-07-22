package dev.leons.ward.services;

import dev.leons.ward.Ward;
import dev.leons.ward.dto.ResponseDto;
import dev.leons.ward.dto.SetupDto;
import dev.leons.ward.exceptions.ApplicationAlreadyConfiguredException;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import java.nio.file.Files;
import java.nio.file.Path;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SetupServiceTest {

    @InjectMocks
    private SetupService setupService;

    private MockedStatic<Ward> wardMockedStatic;
    
    @BeforeEach
    void setUp() {
        // Mock the static methods of Ward class
        wardMockedStatic = mockStatic(Ward.class);
        wardMockedStatic.when(Ward::isFirstLaunch).thenReturn(true);
        wardMockedStatic.when(Ward::restart).then(invocation -> null);
    }
    
    @AfterEach
    void tearDown() {
        // Close the static mock to prevent memory leaks
        if (wardMockedStatic != null) {
            wardMockedStatic.close();
        }
    }

    @Test
    void testPostSetupSuccess() throws IOException, ApplicationAlreadyConfiguredException {
        // Arrange
        SetupDto setupDto = new SetupDto();
        setupDto.setServerName("Test Server");
        setupDto.setTheme("dark");
        setupDto.setPort("8080");
        setupDto.setEnableFog("true");
        setupDto.setBackgroundColor("#000000");
        
        // Ensure setup.ini doesn't exist before test
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        if (setupFile.exists()) {
            setupFile.delete();
        }
        
        try {
            // Act
            ResponseDto response = setupService.postSetup(setupDto);
            
            // Assert
            assertNotNull(response);
            assertEquals("Settings saved correctly", response.getMessage());
            
            // Verify that Ward.restart() was called
            wardMockedStatic.verify(Ward::restart);
            
            // Verify the file was created and contains expected values
            assertTrue(setupFile.exists());
            Ini ini = new Ini(setupFile);
            assertEquals("Test Server", ini.get("setup", "serverName", String.class));
            assertEquals("dark", ini.get("setup", "theme", String.class));
        } finally {
            // Clean up
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }

    @Test
    void testPostSetupFileCreationFailed() throws IOException {
        // Arrange
        SetupDto setupDto = new SetupDto();
        setupDto.setServerName("Test Server");
        
        // Create the setup.ini file to simulate createNewFile() returning false
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        setupFile.createNewFile();
        
        try {
            // Act & Assert
            assertThrows(IOException.class, () -> setupService.postSetup(setupDto));
            
            // Verify that Ward.restart() was not called
            wardMockedStatic.verify(Ward::restart, never());
        } finally {
            // Clean up
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }

    @Test
    void testPostSetupApplicationAlreadyConfigured() {
        // Arrange
        SetupDto setupDto = new SetupDto();
        wardMockedStatic.when(Ward::isFirstLaunch).thenReturn(false);
        
        // Act & Assert
        assertThrows(ApplicationAlreadyConfiguredException.class, () -> setupService.postSetup(setupDto));
    }

    @Test
    void testEnvSetupSuccess() throws IOException {
        // Arrange
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        if (setupFile.exists()) {
            setupFile.delete();
        }
        
        try {
            // Act
            ResponseDto response = SetupService.envSetup();
            
            // Assert
            assertNotNull(response);
            assertEquals("Settings saved correctly", response.getMessage());
            
            // Verify that Ward.restart() was called
            wardMockedStatic.verify(Ward::restart);
            
            // Verify the file was created and contains expected default values
            assertTrue(setupFile.exists());
            Ini ini = new Ini(setupFile);
            assertEquals("Ward", ini.get("setup", "serverName", String.class));
            assertEquals("light", ini.get("setup", "theme", String.class));
        } finally {
            // Clean up
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }
}