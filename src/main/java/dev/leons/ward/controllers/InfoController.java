package dev.leons.ward.controllers;

import dev.leons.ward.dto.InfoDto;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import dev.leons.ward.services.InfoService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * InfoController displays responses from rest API, about server
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Controller
@Mapping("/api/info")
public class InfoController
{
    /**
     * Injected InfoService object
     * Used for getting information about server
     */
    @Inject
    private InfoService infoService;

    /**
     * Get request to display current usage information for processor, RAM and storage
     *
     * @return InfoDto object
     */
    @Mapping
    public InfoDto getInfo() throws ApplicationNotConfiguredException
    {
        return infoService.getInfo();
    }
}