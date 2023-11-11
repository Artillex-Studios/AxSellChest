package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axsellchest.config.impl.ChestConfig;
import com.artillexstudios.axsellchest.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class ChestType {
    private final ChestConfig config;
    private final String name;

    public ChestType(File file) {
        name = file.getName()
                .replace(".yml", "")
                .replace(".yaml", "");

        this.config = new ChestConfig(name);

        this.config.reload();
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return new ItemBuilder(config.ITEM_SECTION).get();
    }

    public long getChestTick() {
        return config.SELL_INTERVAL;
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
