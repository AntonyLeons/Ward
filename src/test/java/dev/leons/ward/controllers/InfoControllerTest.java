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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InfoControllerTest {

    @Mock
    private InfoService infoService;

    @InjectMocks
    private InfoController infoController;

    @Test
    void testGetInfo() throws ApplicationNotConfiguredException {
        // Arrange
        InfoDto infoDto = createMockInfoDto();
        when(infoService.getInfo()).thenReturn(infoDto);

        // Act
        InfoDto response = infoController.getInfo();

        // Assert
        assertNotNull(response);
        assertEquals(infoDto, response);
        assertEquals("Test Processor", response.getProcessor().getName());
        assertEquals("4 Cores", response.getProcessor().getCoreCount());
        assertEquals("Test OS", response.getMachine().getOperatingSystem());
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