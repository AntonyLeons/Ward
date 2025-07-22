package dev.leons.ward.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * UptimeDto is a values container for presenting uptime principal information
 *
 * @author Rudolf Barbu
 * @version 1.0.0
 */
@Getter
@Setter
public class UptimeDto
{
    /**
     * Uptime days field
     */
    private String days;

    /**
     * Uptime hours field
     */
    private String hours;

    /**
     * Uptime minutes field
     */
    private String minutes;

    /**
     * Uptime seconds field
     */
    private String seconds;

    public void setDays(String days) {
        this.days = days;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
}