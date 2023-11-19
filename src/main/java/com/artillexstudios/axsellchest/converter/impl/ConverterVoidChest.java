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
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class ConverterVoidChest implements Converter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterVoidChest.class);

    @Override
    public String getName() {
        return "VoidChest";
    }

    @Override
    public void convert() {
        try {
            Files.walk(FileUtils.PLUGIN_DIRECTORY.resolve("../VoidChest/PlayerData/")).forEach((path) -> {
                File file = path.toFile();
                YamlConfiguration config = new YamlConfiguration();
                try {
                    config.load(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }

                String fileName = FilenameUtils.removeExtension(file.getName());
                UUID ownerUUId = UUID.fromString(fileName);
                ConfigurationSection section = config.getConfigurationSection("chests");
                if (section == null) return;

                for (String chests : section.getKeys(false)) {
                    String type = section.getString(chests + ".name");
                    String world = section.getString(chests + ".location.world");
                    double x = section.getDouble(chests + ".location.x");
                    double y = section.getDouble(chests + ".location.y");
                    double z = section.getDouble(chests + ".location.z");
                    double money = section.getDouble(chests + ".money");
                    long itemsSold = section.getLong(chests + ".items-sold");

                    if (world == null || type == null) return;
                    World bukkitWorld = Bukkit.getWorld(world);

                    Location location = new Location(bukkitWorld, x, y, z);
                    ChestType chestType = ChestTypes.valueOf(type);
                    if (chestType == null) return;

                    DataHandler.QUEUE.submit(() -> {
                        int locationId = AxSellChestPlugin.getInstance().getDataHandler().getLocationId(location);
                        ChestConfig chestConfig = chestType.getConfig();

                        Chest chest = new Chest(chestType, location, ownerUUId, itemsSold, money, locationId, chestConfig.AUTO_SELL, chestConfig.COLLECT_CHUNK, chestConfig.DELETE_UNSELLABLE, chestConfig.BANK, 0);

                        AxSellChestPlugin.getInstance().getDataHandler().saveChest(chest);
                        Chests.startTicking(location.getChunk());
                    });
                }
            });
        } catch (IOException exception) {
            LOGGER.error("An exception occurred while listing files in VoidChest folder!", exception);
        }
    }
}
