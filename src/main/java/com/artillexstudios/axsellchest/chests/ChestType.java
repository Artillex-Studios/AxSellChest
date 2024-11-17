package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axsellchest.config.impl.ChestConfig;
import com.artillexstudios.axsellchest.utils.Keys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

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

    public ItemStack getItem(BigInteger itemsSold, BigDecimal moneyMade) {
        ItemStack itemStack = new ItemBuilder(config.ITEM_SECTION, Placeholder.parsed("money_made", Chest.formatter.format(moneyMade)), Placeholder.parsed("items_sold", String.valueOf(itemsSold)))
                .get();
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.CHEST_TYPE, PersistentDataType.STRING, this.getName());
        meta.getPersistentDataContainer().set(Keys.ITEMS_SOLD, PersistentDataType.STRING, itemsSold.toString());
        meta.getPersistentDataContainer().set(Keys.MONEY_MADE, PersistentDataType.STRING, moneyMade.toString());
        itemStack.setItemMeta(meta);
        return itemStack;
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
