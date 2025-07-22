package dev.leons.ward.controllers;

import dev.leons.ward.dto.InfoDto;
import dev.leons.ward.dto.MachineDto;
import dev.leons.ward.dto.ProcessorDto;
import dev.leons.ward.dto.StorageDto;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import dev.leons.ward.handlers.ControllerExceptionHandler;
import dev.leons.ward.services.InfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class InfoControllerTest {

    @Mock
    private InfoService infoService;

    @InjectMocks
    private InfoController infoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(infoController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    void testGetInfo() throws ApplicationNotConfiguredException {
        // Arrange
        InfoDto infoDto = createMockInfoDto();
        when(infoService.getInfo()).thenReturn(infoDto);

        // Act
        ResponseEntity<InfoDto> response = infoController.getInfo();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(infoDto, response.getBody());
    }

    @Test
    void testGetInfoEndpoint() throws Exception {
        // Arrange
        InfoDto infoDto = createMockInfoDto();
        when(infoService.getInfo()).thenReturn(infoDto);

        // Act & Assert
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processor.name").value("Test Processor"))
                .andExpect(jsonPath("$.processor.coreCount").value("4 Cores"))
                .andExpect(jsonPath("$.processor.clockSpeed").value("3.0 GHz"))
                .andExpect(jsonPath("$.processor.bitDepth").value("64-bit"))
                .andExpect(jsonPath("$.machine.operatingSystem").value("Test OS"))
                .andExpect(jsonPath("$.machine.totalRam").value("8.0 GB"))
                .andExpect(jsonPath("$.machine.ramTypeOrOSBitDepth").value("DDR4"))
                .andExpect(jsonPath("$.machine.procCount").value("1 Processor"))
                .andExpect(jsonPath("$.storage.total").value("1.0 TB"));
    }

    @Test
    void testGetInfoThrowsException() throws Exception {
        // Arrange
        when(infoService.getInfo()).thenThrow(new ApplicationNotConfiguredException());

        // Act & Assert - this will be handled by the global exception handler
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isBadRequest());
    }

    private InfoDto createMockInfoDto() {
        ProcessorDto processorDto = new ProcessorDto();
        processorDto.setName("Test Processor");
        processorDto.setCoreCount("4 Cores");
        processorDto.setClockSpeed("3.0 GHz");
        processorDto.setBitDepth("64-bit");

        MachineDto machineDto = new MachineDto();
        machineDto.setOperatingSystem("Test OS");
        machineDto.setTotalRam("8.0 GB");
        machineDto.setRamTypeOrOSBitDepth("DDR4");
        machineDto.setProcCount("1 Processor");

        StorageDto storageDto = new StorageDto();
        storageDto.setTotal("1.0 TB");

        InfoDto infoDto = new InfoDto();
        infoDto.setProcessor(processorDto);
        infoDto.setMachine(machineDto);
        infoDto.setStorage(storageDto);

        return infoDto;
    }
}