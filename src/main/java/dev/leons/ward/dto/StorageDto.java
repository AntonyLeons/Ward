package dev.leons.ward.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * StorageDto is a values container for presenting storage principal information
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Getter
@Setter
public class StorageDto
{
    /**
     * Host0 storage name field
     */
    private String mainStorage;

    /**
     * Amount of total installed storage field
     */
    private String total;

    /**
     * Disk count field
     */
    private String diskCount;

    /**
     * Total amount of virtual memory (Swap on Linux) field
     */
    private String swapAmount;

    public void setMainStorage(String mainStorage) {
        this.mainStorage = mainStorage;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setDiskCount(String diskCount) {
        this.diskCount = diskCount;
    }

    public void setSwapAmount(String swapAmount) {
        this.swapAmount = swapAmount;
    }
}