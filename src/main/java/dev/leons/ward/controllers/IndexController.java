package dev.leons.ward.controllers;

import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import dev.leons.ward.services.IndexService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;

import java.io.IOException;

/**
 * IndexController displays index page of Ward application
 *
 * @author Rudolf Barbu
 * @version 1.0.2
 */
@Controller
public class IndexController
{
    /**
     * Injected IndexService object
     * Used for getting index page template
     */
    @Inject
    private IndexService indexService;

    /**
     * Get request to display index page
     *
     * @param ctx Solon context for handling request and response
     * @return ModelAndView with template and data
     */
    @Mapping("/")
    public ModelAndView getIndex(Context ctx) throws IOException, ApplicationNotConfiguredException
    {
        return indexService.getIndex(ctx);
    }
}