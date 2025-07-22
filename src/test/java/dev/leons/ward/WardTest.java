package dev.leons.ward;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WardTest {

    @Test
    void testMainMethod() {
        // Arrange
        String[] args = new String[]{"--test"};
        SolonApp mockApp = mock(SolonApp.class);
        
        try (MockedStatic<Solon> solonMock = mockStatic(Solon.class)) {
            solonMock.when(() -> Solon.start(eq(Ward.class), any(String[].class), any()))
                    .thenReturn(mockApp);
            
            // Act
            Ward.main(args);
            
            // Assert
            assertTrue(Ward.isFirstLaunch());
            solonMock.verify(() -> Solon.start(eq(Ward.class), eq(args), any()));
        }
    }
    
    @Test
    void testConstants() {
        // Assert that constants have expected values
        assertEquals("setup.ini", Ward.SETUP_FILE_PATH);
        assertEquals(4000, Ward.INITIAL_PORT);
    }
    
    @Test
    void testIsFirstLaunch() {
        // The main method sets isFirstLaunch to true
        // We can test this by calling main and then checking the value
        SolonApp mockApp = mock(SolonApp.class);
        
        try (MockedStatic<Solon> solonMock = mockStatic(Solon.class)) {
            solonMock.when(() -> Solon.start(eq(Ward.class), any(String[].class), any()))
                    .thenReturn(mockApp);
            
            // Act
            Ward.main(new String[]{});
            
            // Assert
            assertTrue(Ward.isFirstLaunch());
        }
    }
}