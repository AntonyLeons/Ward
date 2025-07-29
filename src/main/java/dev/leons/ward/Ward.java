package dev.leons.ward;

import dev.leons.ward.services.SetupService;
import dev.leons.ward.services.ConfigurationService;
import dev.leons.ward.services.PerformanceService;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.SolonMain;

import java.io.File;

/**
 * Ward is a Solon application class
 *
 * @author Rudolf Barbu
 * @version 1.0.4
 */
@SolonMain
public class Ward {
    /**
     * Constant for determine settings file name
     */
    public static final String SETUP_FILE_PATH = "setup.ini";

    /**
     * Constant for determine initial application port
     */
    public static final int INITIAL_PORT = 4000;

    /**
     * Holder for determine first launch of application
     */
    private static boolean isFirstLaunch;

    /**
     * Holder for application context
     */
    private static SolonApp solonApp;

    /**
     * Configuration service for managing app settings
     */
    private static ConfigurationService configurationService;

    /**
     * Entry point of Ward application
     *
     * @param args Solon application arguments
     */
    public static void main(final String[] args) {
        // Record startup time at the very beginning
        PerformanceService.recordStartTime();
        
        isFirstLaunch = true;
        String[] modifiedArgs = new String[args.length + 1];
        System.arraycopy(args, 0, modifiedArgs, 0, args.length);
        modifiedArgs[args.length] = "--server.port=" + INITIAL_PORT;
        solonApp = Solon.start(Ward.class, modifiedArgs);

        // Initialize configuration service
        configurationService = solonApp.context().getBean(ConfigurationService.class);
        
        File setupFile = new File(Ward.SETUP_FILE_PATH);

        if (System.getenv("WARD_NAME") != null || (System.getenv("WARD_THEME") != null) || (System.getenv("WARD_PORT") != null) || (System.getenv("WARD_FOG") != null)) {
            SetupService.envSetup();
        } else if (setupFile.exists()) {
            // Configuration exists, mark as not first launch
            isFirstLaunch = false;
            configurationService.loadConfiguration();
        }
        
        // Record when application is fully ready
        PerformanceService.recordReadyTime();
        
        // Get performance service and log startup summary
        try {
            PerformanceService performanceService = solonApp.context().getBean(PerformanceService.class);
            System.out.println(performanceService.getPerformanceSummary());
        } catch (Exception e) {
            System.out.println("Ward application started successfully");
        }
    }

    /**
     * Reloads application configuration without restarting
     */
    public static void reloadConfiguration() {
        isFirstLaunch = false;
        
        if (configurationService != null) {
            configurationService.loadConfiguration();
        }
    }

    /**
     * Gets the configuration service instance
     */
    public static ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Checks if this is the first launch of the application
     *
     * @return true if first launch, false otherwise
     */
    public static boolean isFirstLaunch() {
        return isFirstLaunch;
    }

    /**
     * Sets the first launch flag
     */
    public static void setFirstLaunch(boolean firstLaunch) {
        isFirstLaunch = firstLaunch;
    }
}