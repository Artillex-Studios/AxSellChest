package com.artillexstudios.axsellchest.utils;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.config.impl.Messages;

import java.time.Duration;

public class TimeUtils {

    public static String format(long time, Chest chest) {
        if (time < 0) return "---";

        final Duration remainingTime = Duration.ofMillis(time);
        long total = remainingTime.getSeconds();
        long days = total / 86400;
        long hours = (total % 86400) / 3600;
        long minutes = (total % 3600) / 60;
        long seconds = total % 60;

        if (chest.getType().getConfig().TIMER_FORMAT == 1) {
            if (days > 0) return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
            if (hours > 0) return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            return String.format("%02d:%02d", minutes, seconds);
        } else if (chest.getType().getConfig().TIMER_FORMAT == 2) {
            if (days > 0) return days + Messages.DAY;
            if (hours > 0) return hours + Messages.HOUR;
            if (minutes > 0) return minutes + Messages.MINUTE;
            return seconds + Messages.SECOND;
        } else {
            if (days > 0)
                return String.format("%02d" + Messages.DAY + " %02d" + Messages.HOUR + " %02d" + Messages.MINUTE + " %02d" + Messages.SECOND, days, hours, minutes, seconds);
            if (hours > 0)
                return String.format("%02d" + Messages.HOUR + " %02d" + Messages.MINUTE + " %02d" + Messages.SECOND, hours, minutes, seconds);
            return String.format("%02d" + Messages.MINUTE + " %02d" + Messages.SECOND, minutes, seconds);
        }
    }
}
