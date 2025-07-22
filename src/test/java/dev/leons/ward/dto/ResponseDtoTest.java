package dev.leons.ward.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResponseDtoTest {

    @Test
    void testResponseDtoConstructor() {
        // Arrange & Act
        ResponseDto responseDto = new ResponseDto("Test message");
        
        // Assert
        assertEquals("Test message", responseDto.getMessage());
    }
    
    @Test
    void testResponseDtoWithEmptyMessage() {
        // Arrange & Act
        ResponseDto responseDto = new ResponseDto("");
        
        // Assert
        assertEquals("", responseDto.getMessage());
    }
    
    @Test
    void testResponseDtoWithNullMessage() {
        // Arrange & Act
        ResponseDto responseDto = new ResponseDto(null);
        
        // Assert
        assertNull(responseDto.getMessage());
    }
}