package dev.leons.ward.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * SetupDto is a values container for setup data
 *
 * @author Rudolf Barbu
 * @version 1.0.3
 */
@Getter
@Setter
public class SetupDto
{
    /**
     * Server name Field
     */
    @NotNull
    @Size(min = 0, max = 10)
    private String serverName;

    /**
     * Theme name field
     */
    @NotNull
    @NotEmpty
    @Pattern(regexp = "light|dark")
    private String theme;

    /**
     * Port port field
     */
    @NotNull
    @NotEmpty
    @Min(value = 10)
    @Max(value = 65535)
    private String port;

    /**
     * Enable fog field
     */
    @NotNull
    @NotEmpty
    @Pattern(regexp = "true|false")
    private String enableFog;

    /**
     * Background Color field
     */
    @NotEmpty
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$|default")
    private String backgroundColor;

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setEnableFog(String enableFog) {
        this.enableFog = enableFog;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getServerName() {
        return serverName;
    }

    public String getTheme() {
        return theme;
    }

    public String getPort() {
        return port;
    }

    public String getEnableFog() {
        return enableFog;
    }
}