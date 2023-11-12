package com.artillexstudios.axsellchest.menu.actions.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Action;
import org.bukkit.entity.Player;

public class ActionSendMessage extends Action {

    public ActionSendMessage() {
        super("message");
    }

    @Override
    public void run(Player player, Chest chest, String arguments) {
        player.sendMessage(StringUtils.formatToString(arguments));
    }
}
