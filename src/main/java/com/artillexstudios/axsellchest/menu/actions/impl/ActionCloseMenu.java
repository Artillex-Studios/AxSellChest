package com.artillexstudios.axsellchest.menu.actions.impl;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Action;
import org.bukkit.entity.Player;

public class ActionCloseMenu extends Action {

    public ActionCloseMenu() {
        super("close");
    }

    @Override
    public void run(Player player, Chest chest, String arguments) {
        player.closeInventory();
    }
}
