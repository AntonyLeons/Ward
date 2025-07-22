package dev.leons.ward.handlers;

import dev.leons.ward.components.UtilitiesComponent;
import dev.leons.ward.dto.ErrorDto;
import dev.leons.ward.exceptions.ApplicationAlreadyConfiguredException;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;

import java.io.IOException;

/**
 * ControllerExceptionHandler is standard exception handler for rest api, and white labels
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Component
public class ControllerExceptionHandler
{
    /**
     * Injected UtilitiesComponent object
     * Used for various utility functions
     */
    @Inject
    private UtilitiesComponent utilitiesComponent;

    /**
     * Handles application configuration exceptions
     */
    public ErrorDto handleApplicationConfigurationException(final Exception exception)
    {
        return new ErrorDto(exception);
    }

    /**
     * Handles validation exceptions
     */
    public ErrorDto handleValidationException(final Exception exception)
    {
        return new ErrorDto(exception);
    }

    /**
     * Handles general exceptions
     * Also handles all other servlet exceptions, which were not handled by others handlers
     *
     * @throws IOException if ini file is unreachable
     */
    public ModelAndView handleGeneralException(final Exception exception) throws IOException
    {
        ModelAndView mv = new ModelAndView("error/500");
        mv.put("theme", utilitiesComponent.getFromIniFile("theme"));
        System.out.println(exception.getMessage());
        return mv;
    }
}