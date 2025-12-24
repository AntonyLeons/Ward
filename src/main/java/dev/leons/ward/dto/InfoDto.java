package dev.leons.ward.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * InfoDto is a container for other info objects
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Getter
@Setter
public class InfoDto
{
    /**
     * Processor info field
     */
    private ProcessorDto processor;

    /**
     * Machine info field
     */
    private MachineDto machine;

    /**
     * Storage info field
     */
    private StorageDto storage;
}