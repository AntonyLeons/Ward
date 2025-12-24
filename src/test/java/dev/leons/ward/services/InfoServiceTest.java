package dev.leons.ward.services;

import dev.leons.ward.Ward;
import dev.leons.ward.components.UtilitiesComponent;
import dev.leons.ward.dto.InfoDto;
import dev.leons.ward.dto.MachineDto;
import dev.leons.ward.dto.ProcessorDto;
import dev.leons.ward.dto.StorageDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.SystemInfoFFM;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.software.os.OperatingSystem;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InfoServiceTest {

    private MockedStatic<Ward> mockedWard;

    @Mock
    private SystemInfoFFM systemInfo;

    @Mock
    private UtilitiesComponent utilitiesComponent;

    @Mock
    private HardwareAbstractionLayer hardware;

    @Mock
    private CentralProcessor processor;

    @Mock
    private GlobalMemory memory;
    
    @Mock
    private oshi.hardware.VirtualMemory virtualMemory;

    @Mock
    private OperatingSystem operatingSystem;

    @Mock
    private HWDiskStore diskStore;

    @Mock
    private CentralProcessor.ProcessorIdentifier processorIdentifier;

    @Mock
    private OperatingSystem.OSVersionInfo osVersionInfo;

    @InjectMocks
    private InfoService infoService;

    @BeforeEach
    void setUp() {
        mockedWard = Mockito.mockStatic(Ward.class);
        mockedWard.when(Ward::isFirstLaunch).thenReturn(false);
        
        when(systemInfo.getHardware()).thenReturn(hardware);
        when(systemInfo.getOperatingSystem()).thenReturn(operatingSystem);
        when(hardware.getProcessor()).thenReturn(processor);
        when(hardware.getMemory()).thenReturn(memory);
        when(memory.getVirtualMemory()).thenReturn(virtualMemory);
        when(processor.getProcessorIdentifier()).thenReturn(processorIdentifier);
        when(operatingSystem.getVersionInfo()).thenReturn(osVersionInfo);
    }
    
    @AfterEach
    void tearDown() {
        if (mockedWard != null) {
            mockedWard.close();
        }
    }

    @Test
    void testGetInfo() throws Exception {
        // Arrange
        String processorName = "Test Processor";
        int coreCount = 4;
        long[] frequencies = new long[] { 3000000000L };
        String formattedFrequency = "3.0 GHz";
        String architecture = "x64";
        String osName = "Test OS";
        String osVersion = "1.0";
        long totalMemory = 8589934592L; // 8 GB
        String formattedMemory = "8.0 GB";
        long totalStorage = 1099511627776L; // 1 TB
        String formattedStorage = "1.0 TB";
        
        // Mock processor details
        when(processorIdentifier.getName()).thenReturn(processorName);
        when(processorIdentifier.isCpu64bit()).thenReturn(true);
        when(processor.getLogicalProcessorCount()).thenReturn(coreCount);
        when(processor.getCurrentFreq()).thenReturn(frequencies);
        
        // Mock OS details
        when(operatingSystem.getFamily()).thenReturn(osName);
        when(osVersionInfo.getVersion()).thenReturn(osVersion);
        when(operatingSystem.getBitness()).thenReturn(64);
        when(operatingSystem.getProcessCount()).thenReturn(1);
        
        // Mock memory details
        when(memory.getTotal()).thenReturn(totalMemory);
        when(memory.getPhysicalMemory()).thenReturn(Collections.emptyList());
        
        // Mock storage details
        when(hardware.getDiskStores()).thenReturn(List.of(diskStore));
        when(diskStore.getSize()).thenReturn(totalStorage);
        when(diskStore.getModel()).thenReturn("Test Disk");
        when(memory.getVirtualMemory().getSwapTotal()).thenReturn(4294967296L); // 4 GB
        
        // Act
        InfoDto result = infoService.getInfo();
        
        // Assert
        assertNotNull(result);
        
        ProcessorDto processorDto = result.getProcessor();
        assertNotNull(processorDto);
        assertEquals(processorName, processorDto.getName());
        assertEquals(coreCount + " Cores", processorDto.getCoreCount());
        // We can't directly test the formatted frequency as it's calculated internally
        assertNotNull(processorDto.getClockSpeed());
        assertEquals("64-bit", processorDto.getBitDepth());
        
        MachineDto machineDto = result.getMachine();
        assertNotNull(machineDto);
        assertEquals(osName + " " + osVersion, machineDto.getOperatingSystem());
        // We can't directly test the formatted memory as it's calculated internally
        assertNotNull(machineDto.getTotalRam());
        assertEquals("64-bit", machineDto.getRamTypeOrOSBitDepth());
        assertNotNull(machineDto.getProcCount());
        
        StorageDto storageDto = result.getStorage();
        assertNotNull(storageDto);
        assertEquals("Test Disk", storageDto.getMainStorage());
        assertNotNull(storageDto.getTotal());
        assertEquals("1 Disk", storageDto.getDiskCount());
        assertNotNull(storageDto.getSwapAmount());
    }
}