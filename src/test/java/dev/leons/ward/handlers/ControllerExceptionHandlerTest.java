package dev.leons.ward.handlers;

import dev.leons.ward.components.UtilitiesComponent;
import dev.leons.ward.dto.ErrorDto;
import dev.leons.ward.exceptions.ApplicationAlreadyConfiguredException;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControllerExceptionHandlerTest {

    @Mock
    private UtilitiesComponent utilitiesComponent;

    @Mock
    private Model model;

    @InjectMocks
    private ControllerExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() throws IOException {
        lenient().when(utilitiesComponent.getFromIniFile("theme")).thenReturn("dark");
    }

    @Test
    void testApplicationNotConfiguredExceptionHandler() {
        // Arrange
        ApplicationNotConfiguredException exception = new ApplicationNotConfiguredException();

        // Act
        ResponseEntity<ErrorDto> response = exceptionHandler.applicationNotSetUpExceptionHandler(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getErrMessage());
    }

    @Test
    void testApplicationAlreadyConfiguredExceptionHandler() {
        // Arrange
        ApplicationAlreadyConfiguredException exception = new ApplicationAlreadyConfiguredException();

        // Act
        ResponseEntity<ErrorDto> response = exceptionHandler.applicationNotSetUpExceptionHandler(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getErrMessage());
    }

    @Test
    void testMethodArgumentNotValidExceptionHandler() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("Validation failed");

        // Act
        ResponseEntity<ErrorDto> response = exceptionHandler.methodArgumentNotValidExceptionHandler(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getErrMessage());
    }

    @Test
    void testHttpMessageNotReadableExceptionHandler() {
        // Arrange
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("Message not readable");

        // Act
        ResponseEntity<ErrorDto> response = exceptionHandler.methodArgumentNotValidExceptionHandler(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getErrMessage());
    }

    @Test
    void testExceptionHandlerFor404() throws IOException {
        // Arrange
        HttpRequestMethodNotSupportedException exception = mock(HttpRequestMethodNotSupportedException.class);

        // Act
        String viewName = exceptionHandler.exceptionHandler(exception, model);

        // Assert
        assertEquals("error/404", viewName);
        verify(model).addAttribute("theme", "dark");
    }

    @Test
    void testExceptionHandlerFor500() throws IOException {
        // Arrange
        Exception exception = new RuntimeException("General error");

        // Act
        String viewName = exceptionHandler.exceptionHandler(exception, model);

        // Assert
        assertEquals("error/500", viewName);
        verify(model).addAttribute("theme", "dark");
    }
}