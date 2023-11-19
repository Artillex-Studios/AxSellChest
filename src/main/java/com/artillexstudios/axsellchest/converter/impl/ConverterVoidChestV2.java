package com.artillexstudios.axsellchest.converter.impl;

import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.config.impl.ChestConfig;
import com.artillexstudios.axsellchest.converter.Converter;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ConverterVoidChestV2 implements Converter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterVoidChestV2.class);

    @Override
    public String getName() {
        return "VoidChestV2";
    }

    @Override
    public void convert() {
        AtomicInteger integer = new AtomicInteger();
        Arrays.stream(new File(FileUtils.PLUGIN_DIRECTORY.toFile(), "../VoidChest/voiddata/").listFiles()).forEach((path) -> {
            System.out.println("Converting: " + path);
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(path);
            } catch (IOException | InvalidConfigurationException e) {
                LOGGER.error("An exception occurred while loading file:", e);
                return;
            }

            try {
                String uuidString = config.getString("ownerUUID");
                if (uuidString == null) return;
                UUID ownerUUID = UUID.fromString(uuidString);
                String type = config.getString("name");
                if (type == null) return;
                String locationToSplit = config.getString("location");
                if (locationToSplit == null) return;
                String[] locationString = locationToSplit.split(":");
                if (locationString.length == 0) return;
                String stringMoney = config.getString("stats.money");
                String stringItemsSold = config.getString("stats.itemsSold");
                if (stringItemsSold == null || stringMoney == null) return;

                String world = locationString[0];
                double x = Double.parseDouble(locationString[1]);
                double y = Double.parseDouble(locationString[2]);
                double z = Double.parseDouble(locationString[3]);
                double money = Double.parseDouble(stringMoney);
                long itemsSold = Long.parseLong(stringItemsSold);

                if (world == null) return;
                World bukkitWorld = Bukkit.getWorld(world);
                if (bukkitWorld == null) return;

                Location location = new Location(bukkitWorld, x, y, z);
                ChestType chestType = ChestTypes.valueOf(type);
                if (chestType == null) return;

                DataHandler.QUEUE.submit(() -> {
                    integer.addAndGet(1);
                    int locationId = AxSellChestPlugin.getInstance().getDataHandler().getLocationId(location);
                    ChestConfig chestConfig = chestType.getConfig();

                    Chest chest = new Chest(chestType, location, ownerUUID, itemsSold, money, locationId, chestConfig.AUTO_SELL, chestConfig.COLLECT_CHUNK, chestConfig.DELETE_UNSELLABLE, chestConfig.BANK, 0);

                    AxSellChestPlugin.getInstance().getDataHandler().saveChest(chest);
                    Chests.startTicking(location.getChunk());
                });
            } catch (NullPointerException exception) {
                LOGGER.error("Uh-oh! An error occurred while converting!", exception);
                return;
            }
        });
        System.out.println("Converted: " + integer.get());
    }
}
