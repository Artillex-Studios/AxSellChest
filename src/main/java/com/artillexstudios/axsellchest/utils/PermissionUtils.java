package com.artillexstudios.axsellchest.utils;

import com.artillexstudios.axsellchest.config.impl.Config;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionUtils {

    public static int getChestLimit(Player player) {
        int limit = Config.DEFAULT_CHEST_LIMIT;

        for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String permission = effectivePermission.getPermission();
            if (permission.equals("*")) {
                return Integer.MAX_VALUE;
            }

            if (!permission.startsWith("axsellchest.limit.")) continue;
            if (permission.contains("*")) {
                return Integer.MAX_VALUE;
            }

            int value = Integer.parseInt(permission.substring(permission.lastIndexOf('.') + 1));

            if (value > limit) {
                limit = value;
            }
        }

        return limit;
    }
}
