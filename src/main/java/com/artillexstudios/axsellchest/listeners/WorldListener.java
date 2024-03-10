package com.artillexstudios.axsellchest.listeners;

import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.data.DataHandler;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.UUID;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        ChestTypes.loadForWorld(event.getWorld());

        for (Chunk loadedChunk : event.getWorld().getLoadedChunks()) {
            Chests.startTicking(loadedChunk);
        }
    }

    @EventHandler
    public void onWorldUnloadEvent(WorldUnloadEvent event) {
        UUID worldUUID = event.getWorld().getUID();

        for (Chest chest : Chests.getChests()) {
            if (chest.getLocation().getWorld().getUID().equals(worldUUID)) {
                Chests.remove(chest);
            }
        }
    }
}
