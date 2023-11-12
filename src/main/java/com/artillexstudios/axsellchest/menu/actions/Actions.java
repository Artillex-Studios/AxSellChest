package com.artillexstudios.axsellchest.menu.actions;

import com.artillexstudios.axsellchest.chests.Chest;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Actions {
    private static final HashMap<String, Action> ACTIONS = new HashMap<>();

    public static Action register(Action action) {
        ACTIONS.put(action.getId(), action);
        return action;
    }

    public static void run(Player player, Chest chest, List<String> actions) {
        for (String rawAction : actions) {
            String id = StringUtils.substringBetween(rawAction, "[", "]").toLowerCase(Locale.ENGLISH);
            String arguments = StringUtils.substringAfter(rawAction, "] ").toLowerCase(Locale.ENGLISH);

            Action action = ACTIONS.get(id);
            if (action == null) continue;

            action.run(player, chest, arguments);
        }
    }
}
