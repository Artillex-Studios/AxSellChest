package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axsellchest.config.impl.ChestConfig;
import com.artillexstudios.axsellchest.utils.Keys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;

public class ChestType {
    private final ChestConfig config;
    private final String name;
    private final File file;

    public ChestType(File file) {
        this.file = file;

        this.name = file.getName()
                .replace(".yml", "")
                .replace(".yaml", "");

        this.config = new ChestConfig(file.getName());

        this.config.reload();

        ChestTypes.register(this);
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem(long itemsSold, double moneyMade) {
        return new ItemBuilder(config.ITEM_SECTION)
                .storePersistentData(Keys.CHEST_TYPE, PersistentDataType.STRING, this.getName())
                .storePersistentData(Keys.ITEMS_SOLD, PersistentDataType.LONG, itemsSold)
                .storePersistentData(Keys.MONEY_MADE, PersistentDataType.DOUBLE, moneyMade)
                .get();
    }

    public void reload() {
        config.reload();
    }

    public File getFile() {
        return file;
    }

    public long getChestTick() {
        return config.SELL_INTERVAL;
    }

    public ChestConfig getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChestType chestType)) return false;

        if (!config.equals(chestType.config)) return false;
        return getName().equals(chestType.getName());
    }

    @Override
    public int hashCode() {
        int result = config.hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChestType{" +
                "config=" + config +
                ", name='" + name + '\'' +
                '}';
    }
}
