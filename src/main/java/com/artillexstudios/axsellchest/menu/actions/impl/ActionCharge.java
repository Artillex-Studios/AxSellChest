package com.artillexstudios.axsellchest.menu.actions.impl;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Action;
import org.bukkit.entity.Player;

public class ActionCharge extends Action {

    public ActionCharge() {
        super("charge");
    }

    @Override
    public void run(Player player, Chest chest, String arguments) {
        if (chest.getCharge() < System.currentTimeMillis()) {
            chest.setCharge(System.currentTimeMillis() + Long.parseLong(arguments));
        } else {
            chest.setCharge(chest.getCharge() + Long.parseLong(arguments));
        }
    }
}
