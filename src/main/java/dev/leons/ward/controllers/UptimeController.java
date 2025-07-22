package dev.leons.ward.controllers;

import dev.leons.ward.dto.UptimeDto;
import dev.leons.ward.services.UptimeService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * UptimeController displays responses from rest API
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Controller
public class UptimeController
{
    /**
     * Injected UptimeService object
     * Used for getting uptime information
     */
    @Inject
    private UptimeService uptimeService;

    /**
     * Get request to display uptime information
     *
     * @return UptimeDto with uptime information
     */
    @Mapping("/api/uptime")
    public UptimeDto getUptime()
    {
        return uptimeService.getUptime();
    }
}