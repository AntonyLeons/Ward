package dev.leons.ward.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InfoDtoTest {

    @Test
    void testInfoDtoGettersAndSetters() {
        // Arrange
        InfoDto infoDto = new InfoDto();
        ProcessorDto processorDto = new ProcessorDto();
        MachineDto machineDto = new MachineDto();
        StorageDto storageDto = new StorageDto();
        
        // Initial state should be null
        assertNull(infoDto.getProcessor());
        assertNull(infoDto.getMachine());
        assertNull(infoDto.getStorage());
        
        // Act - set values
        infoDto.setProcessor(processorDto);
        infoDto.setMachine(machineDto);
        infoDto.setStorage(storageDto);
        
        // Assert - getters should return the set values
        assertEquals(processorDto, infoDto.getProcessor());
        assertEquals(machineDto, infoDto.getMachine());
        assertEquals(storageDto, infoDto.getStorage());
    }
    
    @Test
    void testInfoDtoWithValues() {
        // Arrange
        ProcessorDto processorDto = new ProcessorDto();
        processorDto.setName("Test Processor");
        processorDto.setCoreCount("4 Cores");
        processorDto.setClockSpeed("3.0 GHz");
        processorDto.setBitDepth("64-bit");
        
        MachineDto machineDto = new MachineDto();
        machineDto.setOperatingSystem("Test OS");
        machineDto.setTotalRam("8.0 GB");
        machineDto.setRamTypeOrOSBitDepth("64-bit");
        machineDto.setProcCount("1 Processor");
        
        StorageDto storageDto = new StorageDto();
        storageDto.setTotal("1.0 TB");
        
        // Act
        InfoDto infoDto = new InfoDto();
        infoDto.setProcessor(processorDto);
        infoDto.setMachine(machineDto);
        infoDto.setStorage(storageDto);
        
        // Assert
        assertEquals("Test Processor", infoDto.getProcessor().getName());
        assertEquals("4 Cores", infoDto.getProcessor().getCoreCount());
        assertEquals("3.0 GHz", infoDto.getProcessor().getClockSpeed());
        assertEquals("64-bit", infoDto.getProcessor().getBitDepth());
        
        assertEquals("Test OS", infoDto.getMachine().getOperatingSystem());
        assertEquals("8.0 GB", infoDto.getMachine().getTotalRam());
        assertEquals("64-bit", infoDto.getMachine().getRamTypeOrOSBitDepth());
        assertEquals("1 Processor", infoDto.getMachine().getProcCount());
        
        assertEquals("1.0 TB", infoDto.getStorage().getTotal());
    }
}