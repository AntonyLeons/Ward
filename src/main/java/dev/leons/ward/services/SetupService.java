package dev.leons.ward.services;

import dev.leons.ward.Ward;
import dev.leons.ward.dto.ResponseDto;
import dev.leons.ward.dto.SetupDto;
import dev.leons.ward.exceptions.ApplicationAlreadyConfiguredException;
import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * SetupService manipulating setup data
 *
 * @author Rudolf Barbu
 * @version 1.0.2
 */
@Service
public class SetupService {
    /**
     * Constant, that providing setup section name
     */
    private static final String SECTION_NAME = "setup";

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Puts new data in ini file
     *
     * @param file       ini file
     * @param optionName option in section
     * @throws IOException if file does not exists
     */
    private static void putInIniFile(final File file, final String optionName, final String value) throws IOException {
        Ini ini = new Ini(file);
        ini.put(SECTION_NAME, optionName, value);
        ini.store();
    }

    /**
     * Fills setup data in ini file
     *
     * @param setupDto user settings data
     * @return ResponseEntityWrapperAsset filled with ResponseDto
     * @throws IOException IoException if file is fot found, and cant be created
     */
    public ResponseDto postSetup(final SetupDto setupDto) throws IOException, ApplicationAlreadyConfiguredException {
        if (Ward.isFirstLaunch()) {
            File file = new File(Ward.SETUP_FILE_PATH);

            if (file.createNewFile()) {
                putInIniFile(file, "serverName", setupDto.getServerName());
                putInIniFile(file, "theme", setupDto.getTheme());
                putInIniFile(file, "port", setupDto.getPort());
                putInIniFile(file, "enableFog", setupDto.getEnableFog());
                putInIniFile(file, "backgroundColor", setupDto.getBackgroundColor());

                // Mark as no longer first launch to enable configuration loading
                Ward.setFirstLaunch(false);
                
                // Check if port is different from initial port - if so, restart is needed
                String initialPort = String.valueOf(Ward.INITIAL_PORT);
                if (!initialPort.equals(setupDto.getPort())) {
                    // Port changed from default, restart required
                    Ward.restart();
                } else {
                    // Using default port, no restart needed
                    configurationService.reloadConfiguration();
                }
            } else {
                throw new IOException();
            }
        } else {
            throw new ApplicationAlreadyConfiguredException();
        }

        return new ResponseDto("Settings saved correctly");
    }

    public static ResponseDto envSetup() {
        if (Ward.isFirstLaunch()) {
            try {
                File file = new File(Ward.SETUP_FILE_PATH);
                if (file.exists()) {
                    file.delete();
                }
                if (file.createNewFile()) {
                    String servername = (System.getenv("WARD_NAME") != null) ? System.getenv("WARD_NAME") : "Ward";
                    String theme = (System.getenv("WARD_THEME") != null) ? System.getenv("WARD_THEME").toLowerCase() : "light";
                    String port = (System.getenv("WARD_PORT") != null) ? System.getenv("WARD_PORT") : "4000";
                    String enableFog = (System.getenv("WARD_FOG") != null) ? System.getenv("WARD_FOG") : "true";
                    String backgroundColor = (System.getenv("WARD_BACKGROUND") != null) ? System.getenv("WARD_BACKGROUND") : "default";

                    putInIniFile(file, "serverName", servername);
                    putInIniFile(file, "theme", theme);
                    putInIniFile(file, "port", port);
                    putInIniFile(file, "enableFog", enableFog);
                    putInIniFile(file, "backgroundColor", backgroundColor);

                    // Mark as no longer first launch to enable configuration loading
                    Ward.setFirstLaunch(false);
                    
                    // Reload configuration without restart
                    // Note: For environment setup, restart is still needed for port changes
                    Ward.restart();
                } else {
                    throw new IOException();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseDto("Settings saved correctly");
    }
}