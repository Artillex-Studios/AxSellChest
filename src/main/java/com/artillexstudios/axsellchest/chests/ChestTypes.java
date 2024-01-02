package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.utils.FileUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChestTypes {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChestTypes.class);
    private static final HashMap<String, ChestType> TYPES = new HashMap<>();
    private static final File CHEST_TYPES_FOLDER = FileUtils.PLUGIN_DIRECTORY.resolve("chests/").toFile();

    public static void reload() {
        if (CHEST_TYPES_FOLDER.mkdirs()) {
            FileUtils.copyFromResource("chests");
        }

        Collection<File> files = org.apache.commons.io.FileUtils.listFiles(CHEST_TYPES_FOLDER, new String[]{"yaml", "yml"}, true);

        for (File file : files) {
            ChestType type = TYPES.get(file.getName()
                    .replace(".yml", "")
                    .replace(".yaml", ""));

            if (type == null) {
                new ChestType(file);
            } else {
                type.reload();
            }
        }

        ArrayList<ChestType> removedTypes = new ArrayList<>();
        TYPES.entrySet().removeIf((entry) -> {
            boolean contains = files.contains(entry.getValue().getFile());

            if (!contains) {
                removedTypes.add(entry.getValue());
            }

            return !contains;
        });

        List<Chest> chests = Chests.getChests();
        int chestSize = chests.size();

        for (int i = 0; i < chestSize; i++) {
            Chest chest = chests.get(i);

            if (removedTypes.contains(chest.getType())) {
                chest.remove();
            } else {
                chest.onReload();
            }
        }
    }

    public static ChestType valueOf(String name) {
        return TYPES.get(name.toLowerCase(Locale.ENGLISH));
    }

    public static void loadForWorld(World world) {
        TYPES.forEach((name, type) -> {
            AxSellChestPlugin.getInstance().getDataHandler().loadChestsForWorld(type, world);

            for (Chunk loadedChunk : world.getLoadedChunks()) {
                Chests.startTicking(loadedChunk);
            }
        });
    }

    public static void register(ChestType chestType) {
        if (TYPES.containsKey(chestType.getName())) {
            LOGGER.warn("A chest type named {} has already been registered! Skipping!", chestType.getName());
            return;
        }

        TYPES.put(chestType.getName().toLowerCase(Locale.ENGLISH), chestType);
        DataHandler.QUEUE.submit(() -> {
            AxSellChestPlugin.getInstance().getDataHandler().insertType(chestType);
        });
    }

    public static Map<String, ChestType> getTypes() {
        return Map.copyOf(TYPES);
    }
}
