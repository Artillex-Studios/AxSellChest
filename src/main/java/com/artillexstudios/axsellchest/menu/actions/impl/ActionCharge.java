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
        long chargeSeconds = (chest.getCharge() - System.currentTimeMillis()) / 1000;

        if (chargeSeconds + Long.parseLong(arguments) > chest.getType().getConfig().MAX_CHARGE * 60L) {
            chest.setCharge(System.currentTimeMillis() + chest.getType().getConfig().MAX_CHARGE * 60L * 1000L);
            return;
        }

        if (chest.getCharge() < System.currentTimeMillis()) {
            chest.setCharge(System.currentTimeMillis() + Long.parseLong(arguments) * 1000);
        } else {
            chest.setCharge(chest.getCharge() + Long.parseLong(arguments) * 1000);
        }
    }
}
