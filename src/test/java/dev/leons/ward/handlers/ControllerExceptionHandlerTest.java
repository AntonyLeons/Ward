package dev.leons.ward.handlers;

import dev.leons.ward.components.UtilitiesComponent;
import dev.leons.ward.exceptions.ApplicationAlreadyConfiguredException;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noear.solon.core.handle.ModelAndView;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControllerExceptionHandlerTest {

    @Mock
    private UtilitiesComponent utilitiesComponent;



    @InjectMocks
    private ControllerExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() throws IOException {
        lenient().when(utilitiesComponent.getFromIniFile("theme")).thenReturn("dark");
    }

    @Test
    void testApplicationNotConfiguredExceptionHandler() throws IOException {
        // Arrange
        ApplicationNotConfiguredException exception = new ApplicationNotConfiguredException();

        // Act
        dev.leons.ward.dto.ErrorDto result = exceptionHandler.handleApplicationConfigurationException(exception);

        // Assert
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getErrMessage());
    }

    @Test
    void testApplicationAlreadyConfiguredExceptionHandler() throws IOException {
        // Arrange
        ApplicationAlreadyConfiguredException exception = new ApplicationAlreadyConfiguredException();

        // Act
        dev.leons.ward.dto.ErrorDto result = exceptionHandler.handleApplicationConfigurationException(exception);

        // Assert
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getErrMessage());
    }

    @Test
    void testValidationExceptionHandler() throws IOException {
        // Arrange
        Exception exception = new RuntimeException("Validation failed");

        // Act
        dev.leons.ward.dto.ErrorDto result = exceptionHandler.handleValidationException(exception);

        // Assert
        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getErrMessage());
    }

    @Test
    void testGeneralExceptionHandler() throws IOException {
        // Arrange
        Exception exception = new RuntimeException("General error");

        // Act
        ModelAndView result = exceptionHandler.handleGeneralException(exception);

        // Assert
        assertNotNull(result);
        assertEquals("error/500", result.view());
    }
}