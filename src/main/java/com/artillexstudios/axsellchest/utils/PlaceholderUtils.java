package com.artillexstudios.axsellchest.utils;

import java.util.List;

public class PlaceholderUtils {
    private static final List<String> PLACEHOLDERS = List.of("<items_sold>", "<money_made>", "<charge>", "<owner>");
    private static final int placeholderSize = PLACEHOLDERS.size();

    public static boolean containsPlaceholders(String string) {
        for (int i = 0; i < placeholderSize; i++) {
            String placeholder = PLACEHOLDERS.get(i);

            if (placeholder.contains(string)) {
                return true;
            }
        }

        return false;
    }
}