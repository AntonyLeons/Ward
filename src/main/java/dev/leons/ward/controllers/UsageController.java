package dev.leons.ward.controllers;

import dev.leons.ward.dto.UsageDto;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import dev.leons.ward.services.UsageService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * UsageController displays responses from rest API
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Controller
@Mapping("/api/usage")
public class UsageController
{
    /**
     * Injected UsageService object
     * Used for getting usage information
     */
    @Inject
    private UsageService usageService;

    /**
     * Get request to display current usage information for processor, RAM and storage
     *
     * @return UsageDto object
     */
    @Mapping
    public UsageDto getUsage() throws ApplicationNotConfiguredException
    {
        return usageService.getUsage();
    }
}