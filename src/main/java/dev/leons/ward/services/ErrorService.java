package dev.leons.ward.services;

import dev.leons.ward.Ward;
import dev.leons.ward.components.UtilitiesComponent;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ModelAndView;

import java.io.IOException;

/**
 * ErrorService displays error pages of Ward application
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Component
public class ErrorService
{
    /**
     * Injected UtilitiesComponent object
     * Used for various utility functions
     */
    @Inject
    private UtilitiesComponent utilitiesComponent;

    /**
     * Returns 404 error page
     *
     * @return ModelAndView with template and data
     * @throws IOException if ini file is unreachable
     */
    public ModelAndView getError() throws IOException
    {
        if (Ward.isFirstLaunch())
        {
            return new ModelAndView("setup");
        }

        ModelAndView mv = new ModelAndView("error/404");
        mv.put("theme", utilitiesComponent.getFromIniFile("theme"));
        return mv;
    }
}