package dev.leons.ward.services;

import dev.leons.ward.Ward;
import dev.leons.ward.components.UtilitiesComponent;
import dev.leons.ward.exceptions.ApplicationNotConfiguredException;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * IndexService displays index page of Ward application
 *
 * @author Rudolf Barbu
 * @version 1.0.1
 */
@Component
public class IndexService
{
    /**
     * Injected InfoService object
     * Used for getting machine information for html template
     */
    @Inject
    private InfoService infoService;

    /**
     * Injected UptimeService object
     * Used for getting uptime for html template
     */
    @Inject
    private UptimeService uptimeService;

    /**
     * Injected UtilitiesComponent object
     * Used for various utility functions
     */
    @Inject
    private UtilitiesComponent utilitiesComponent;

    /**
     * Gets project version information
     *
     * @return MavenDto with filled field
     * @throws IOException if file does not exists
     */
    private String getVersion() throws IOException
    {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getResourceAsStream("/META-INF/maven/dev.leons/ward/pom.properties");

        if (inputStream != null)
        {
            properties.load(inputStream);
            String version = properties.getProperty("version");

            return "v" + version;
        }
        else
        {
            return "Developer mode";
        }
    }

    /**
     * Fills model and returns template name
     *
     * @param ctx Solon context
     * @return ModelAndView with template and data
     */
    public ModelAndView getIndex(final Context ctx) throws IOException, ApplicationNotConfiguredException
    {
        // Check if setup file exists but we're still in first launch mode
        File setupFile = new File(Ward.SETUP_FILE_PATH);
        if (Ward.isFirstLaunch() && setupFile.exists()) {
            // Configuration exists but wasn't loaded, reload it
            Ward.reloadConfiguration();
        }
        
        if (Ward.isFirstLaunch())
        {
            return new ModelAndView("setup.html");
        }

        updateDefaultsInSetupFile();

        ModelAndView mv = new ModelAndView("index.html");
        mv.put("theme", utilitiesComponent.getFromIniFile("theme"));
        mv.put("serverName", utilitiesComponent.getFromIniFile("serverName"));
        mv.put("enableFog", utilitiesComponent.getFromIniFile("enableFog"));
        mv.put("backgroundColor", utilitiesComponent.getFromIniFile("backgroundColor"));

        mv.put("info", infoService.getInfo());
        mv.put("uptime", uptimeService.getUptime());
        mv.put("version", getVersion());

        return mv;
    }

    private void updateDefaultsInSetupFile() throws IOException {
        if (utilitiesComponent.getFromIniFile("enableFog") == null) {
            utilitiesComponent.putInIniFile("enableFog", "true");
        }
        if (utilitiesComponent.getFromIniFile("backgroundColor") == null) {
            utilitiesComponent.putInIniFile("backgroundColor", "#303030");
        }
    }
}