package dev.leons.ward;

import dev.leons.ward.services.SetupService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ini4j.Ini;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Ward is a Spring Boot application class
 *
 * @author Rudolf Barbu
 * @version 1.0.4
 */
@Slf4j
@SpringBootApplication
public class Ward extends SpringBootServletInitializer {
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
    private static ConfigurableApplicationContext configurableApplicationContext;

    /**
     * Entry point of Ward application
     *
     * @param args Spring Boot application arguments
     */
    public static void main(final String[] args) {

        isFirstLaunch = true;
        configurableApplicationContext = SpringApplication.run(Ward.class, args);
        logAccessUrls(configurableApplicationContext);

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
        ApplicationArguments args = configurableApplicationContext.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() ->
        {
            configurableApplicationContext.close();
            configurableApplicationContext = SpringApplication.run(Ward.class, args.getSourceArgs());
            logAccessUrls(configurableApplicationContext);
        });

        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Logs the access URLs for the application
     *
     * @param context Application context
     */
    private static void logAccessUrls(ConfigurableApplicationContext context) {
        String port = String.valueOf(INITIAL_PORT);
        try {
            File setupFile = new File(SETUP_FILE_PATH);
            if (setupFile.exists()) {
                Ini ini = new Ini(setupFile);
                String configuredPort = ini.get("setup", "port");
                if (configuredPort != null) {
                    port = configuredPort;
                }
            }
        } catch (Exception e) {
            log.warn("Could not read setup.ini for port, using default");
        }

        String protocol = "http";
        String localAddress = "localhost";
        String networkAddress = "127.0.0.1";
        try {
            networkAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        log.info("\n----------------------------------------------------------\n\t" +
                        "Application Ward is running! Access URLs:\n\t" +
                        "Local: \t\t{}://{}:{}\n\t" +
                        "Network: \t{}://{}:{}\n" +
                        "----------------------------------------------------------",
                protocol, localAddress, port,
                protocol, networkAddress, port);
    }
}