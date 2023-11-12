package com.artillexstudios.axsellchest.menu.actions.impl;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Action;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ActionToggle extends Action {

    public ActionToggle() {
        super("toggle");
    }

    @Override
    public void run(Player player, Chest chest, String arguments) {
        switch (arguments.toLowerCase(Locale.ENGLISH)) {
            case "bank" -> {
                chest.setBank(!chest.isBank());
            }
            case "autosell" -> {
                chest.setAutoSell(!chest.isAutoSell());
            }
            case "collectchunk" -> {
                chest.setCollectChunk(!chest.isCollectChunk());
            }
            case "deleteunsellable" -> {
                chest.setDeleteUnsellable(!chest.isDeleteUnsellable());
            }
        }
    }
}
