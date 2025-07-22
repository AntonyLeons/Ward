package dev.leons.ward.components;

import dev.leons.ward.Ward;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UtilitiesComponentTest {

    @InjectMocks
    private UtilitiesComponent utilitiesComponent;

    @Test
    void testGetFromIniFileWhenFileExists() throws IOException {
        // Arrange
        String optionName = "serverName";
        String expectedValue = "Test Server";
        
        // Create the setup.ini file
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        Ini ini = new Ini();
        ini.put("setup", optionName, expectedValue);
        ini.store(setupFile);
        
        try {
            // Act
            String result = utilitiesComponent.getFromIniFile(optionName);
            
            // Assert
            assertEquals(expectedValue, result);
        } finally {
            // Clean up
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }

    @Test
    void testGetFromIniFileWhenFileDoesNotExist() throws IOException {
        // Arrange
        String optionName = "serverName";
        
        // Ensure setup.ini doesn't exist
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        if (setupFile.exists()) {
            setupFile.delete();
        }
        
        try {
            // Act
            String result = utilitiesComponent.getFromIniFile(optionName);
            
            // Assert
            assertNull(result);
        } finally {
            // Clean up (just in case)
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }

    @Test
    void testPutInIniFileWhenFileExists() throws IOException {
        // Arrange
        String optionName = "serverName";
        String value = "New Server Name";
        
        // Create the setup.ini file
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        Ini ini = new Ini();
        ini.put("setup", "existingOption", "existingValue");
        ini.store(setupFile);
        
        try {
            // Act & Assert - no exception should be thrown
            assertDoesNotThrow(() -> utilitiesComponent.putInIniFile(optionName, value));
            
            // Verify the value was actually written
            Ini updatedIni = new Ini(setupFile);
            assertEquals(value, updatedIni.get("setup", optionName, String.class));
        } finally {
            // Clean up
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }

    @Test
    void testPutInIniFileWhenFileDoesNotExist() {
        // Arrange
        String optionName = "serverName";
        String value = "New Server Name";
        
        // Ensure setup.ini doesn't exist
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        if (setupFile.exists()) {
            setupFile.delete();
        }
        
        try {
            // Act & Assert
            assertThrows(IOException.class, () -> utilitiesComponent.putInIniFile(optionName, value));
        } finally {
            // Clean up (just in case)
            if (setupFile.exists()) {
                setupFile.delete();
            }
        }
    }
}