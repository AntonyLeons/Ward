package dev.leons.ward.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * UsageDto is a values container for presenting server usage
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Getter
@Setter
public class UsageDto
{
    /**
     * Processor usage field
     */
    private int processor;

    /**
     * Ram usage field
     */
    private int ram;

    /**
     * Storage usage field
     */
    private int storage;

    public void setProcessor(int processor) {
        this.processor = processor;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }
}