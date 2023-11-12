package com.artillexstudios.axsellchest.menu.actions.impl;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ActionOpenContainer extends Action {

    public ActionOpenContainer() {
        super("container");
    }

    @Override
    public void run(Player player, Chest chest, String arguments) {
        Inventory inventory = chest.getInventory();
        if (inventory == null) return;

        player.openInventory(inventory);
    }
}
