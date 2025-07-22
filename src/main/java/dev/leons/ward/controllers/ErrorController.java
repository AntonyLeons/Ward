package dev.leons.ward.controllers;

import dev.leons.ward.services.ErrorService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

import java.io.IOException;

/**
 * ErrorController displays error pages of Ward application
 *
 * @author Rudolf Barbu
 * @version 1.0.2
 */
@Controller
@Mapping("/error")
public class ErrorController
{
    /**
     * Injected ErrorService object
     * Used to determine error page
     */
    @Inject
    private ErrorService errorService;

    /**
     * Get request to display error page, which corresponds status code
     *
     * @return ModelAndView with template and data
     */
    @Mapping
    public ModelAndView getError() throws IOException
    {
        return errorService.getError();
    }

}