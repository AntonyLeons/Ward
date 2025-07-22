package dev.leons.ward.dto;

import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorDtoTest {

    @Test
    void testErrorDtoConstructor() {
        // Arrange
        Exception testException = new RuntimeException("Test error message");
        
        // Act
        ErrorDto errorDto = new ErrorDto(testException);
        
        // Assert
        assertEquals("Test error message", errorDto.getErrMessage());
        assertEquals("java.lang.RuntimeException", errorDto.getExceptionName());
        assertNotNull(errorDto.getTimestamp());
        
        // Verify timestamp is a valid LocalDateTime string
        assertDoesNotThrow(() -> LocalDateTime.parse(errorDto.getTimestamp()));
    }
    
    @Test
    void testErrorDtoWithCustomException() {
        // Arrange
        Exception testException = new ApplicationNotConfiguredException();
        
        // Act
        ErrorDto errorDto = new ErrorDto(testException);
        
        // Assert
        assertEquals(testException.getMessage(), errorDto.getErrMessage());
        assertEquals("dev.leons.ward.exceptions.ApplicationNotConfiguredException", errorDto.getExceptionName());
    }
    
    @Test
    void testErrorDtoWithNullMessage() {
        // Arrange - create exception with null message
        Exception testException = new NullPointerException();
        
        // Act
        ErrorDto errorDto = new ErrorDto(testException);
        
        // Assert
        assertNull(errorDto.getErrMessage());
        assertEquals("java.lang.NullPointerException", errorDto.getExceptionName());
    }
}