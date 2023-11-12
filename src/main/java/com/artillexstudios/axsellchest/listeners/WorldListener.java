package com.artillexstudios.axsellchest.listeners;

import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.data.DataHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        DataHandler.QUEUE.submit(() -> {
            ChestTypes.loadForWorld(event.getWorld());
        });
    }
}
