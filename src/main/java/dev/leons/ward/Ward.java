package dev.leons.ward;

import dev.leons.ward.services.SetupService;
import lombok.Getter;
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
    @Getter
    private static boolean isFirstLaunch;

    /**
     * Holder for application context
     */
    private static SolonApp solonApp;

    /**
     * Entry point of Ward application
     *
     * @param args Solon application arguments
     */
    public static void main(final String[] args) {

        isFirstLaunch = true;
        solonApp = Solon.start(Ward.class, args, app -> {
            app.cfg().loadAdd("server.port=" + INITIAL_PORT);
        });

        File setupFile = new File(Ward.SETUP_FILE_PATH);

        if (System.getenv("WARD_NAME") != null || (System.getenv("WARD_THEME") != null) || (System.getenv("WARD_PORT") != null) || (System.getenv("WARD_FOG") != null)) {
            SetupService.envSetup();
        } else if (setupFile.exists()) {
            restart();
        }
    }

    /**
     * Restarts application
     */
    public static void restart() {
        isFirstLaunch = false;

        Thread thread = new Thread(() ->
        {
            if (solonApp != null) {
                System.exit(0);
            }
            solonApp = Solon.start(Ward.class, new String[]{}, app -> {
                app.cfg().loadAdd("server.port=" + INITIAL_PORT);
            });
        });

        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Checks if this is the first launch of the application
     *
     * @return true if first launch, false otherwise
     */
    public static boolean isFirstLaunch() {
        return isFirstLaunch;
    }
}