package com.artillexstudios.axsellchest.menu.actions.impl;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionPlayerCommand extends Action {

    public ActionPlayerCommand() {
        super("player");
    }

    @Override
    public void run(Player player, Chest chest, String arguments) {
        Bukkit.dispatchCommand(player, arguments);
    }
}
