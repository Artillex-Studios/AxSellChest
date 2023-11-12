package com.artillexstudios.axsellchest.menu.actions;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.impl.ActionCharge;
import com.artillexstudios.axsellchest.menu.actions.impl.ActionCloseMenu;
import com.artillexstudios.axsellchest.menu.actions.impl.ActionConsoleCommand;
import com.artillexstudios.axsellchest.menu.actions.impl.ActionPlayerCommand;
import com.artillexstudios.axsellchest.menu.actions.impl.ActionSendMessage;
import com.artillexstudios.axsellchest.menu.actions.impl.ActionToggle;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Actions {
    private static final HashMap<String, Action> ACTIONS = new HashMap<>();

    private static final Action CHARGE = register(new ActionCharge());
    private static final Action CLOSE_MENU = register(new ActionCloseMenu());
    private static final Action CONSOLE_COMMAND = register(new ActionConsoleCommand());
    private static final Action PLAYER_COMMAND = register(new ActionPlayerCommand());
    private static final Action SEND_MESSAGE = register(new ActionSendMessage());
    private static final Action TOGGLE = register(new ActionToggle());

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
