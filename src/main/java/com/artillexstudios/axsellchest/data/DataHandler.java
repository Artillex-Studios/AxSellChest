package com.artillexstudios.axsellchest.data;

import com.artillexstudios.axapi.data.ThreadedQueue;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public interface DataHandler {
    ThreadedQueue<Runnable> QUEUE = new ThreadedQueue<>("AxSellChest-Database-Thread");

    String getType();

    void setup();

    void insertType(ChestType chestType);

    void loadChestsForWorld(ChestType type, World world);

    int getLocationId(Location location);

    Location getLocation(int id);

    World getWorld(int id);

    void saveChest(Chest chest);

    void deleteChest(Chest chest);

    int getChests(UUID uuid);

    void disable();
}
