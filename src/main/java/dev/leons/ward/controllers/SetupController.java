package dev.leons.ward.controllers;

import dev.leons.ward.dto.ResponseDto;
import dev.leons.ward.exceptions.ApplicationAlreadyConfiguredException;
import dev.leons.ward.services.SetupService;
import dev.leons.ward.dto.SetupDto;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

import jakarta.validation.Valid;
import java.io.IOException;

/**
 * SetupController displays responses from rest API
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Controller
@Mapping("/api/setup")
public class SetupController
{
    /**
     * Injected SetupService object
     * Used for posting settings information in ini file
     */
    @Inject
    private SetupService setupService;

    /**
     * Posting setup info in database
     *
     * @param setupDto dto with data
     * @return ResponseEntity to servlet
     */
    @Mapping(method = MethodType.POST)
    public ResponseDto postSetup(Context ctx, @Valid final SetupDto setupDto) throws IOException, ApplicationAlreadyConfiguredException
    {
        return setupService.postSetup(setupDto);
    }
}