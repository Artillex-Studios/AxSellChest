package com.artillexstudios.axsellchest.config.impl;

import com.artillexstudios.axsellchest.config.AbstractConfig;
import com.artillexstudios.axsellchest.utils.FileUtils;

import java.util.List;
import java.util.Map;

public class ChestConfig extends AbstractConfig {
    @Key("block")
    @Comment("""
            The type of the block
            """)
    public String BLOCK_TYPE = "chest";
    @Key("booster")
    @Comment("""
            The multiplier of this chest
            """)
    public double BOOSTER = 1.0;
    @Key("shift-click-open")
    @Comment("""
            What should we open for the player when they shift+rightclick on the chest?
            Can be gui or chest!
            """)
    public String SHIFT_CLICK_OPEN = "gui";
    @Key("options.sell-interval")
    @Comment("""
            How often should the chest attempt to sell the items in it's chunk/inventory?
            WARNING! This is in TICKS! 1 second is 20 ticks.
            """)
    public long SELL_INTERVAL = 1;
    @Key("options.auto-sell")
    public boolean AUTO_SELL = true;
    @Key("options.collect-chunk")
    public boolean COLLECT_CHUNK = true;
    @Key("options.delete-unsellable")
    public boolean DELETE_UNSELLABLE = true;
    @Key("options.hologram")
    public boolean HOLOGRAM = true;
    @Key("options.bank")
    public boolean BANK = true;
    @Key("options.instant-collect")
    public boolean INSTANT_COLLECT = true;
    @Key("options.charge")
    public boolean CHARGE = true;
    @Key("options.max-charge")
    @Comment("In hours")
    public int MAX_CHARGE = 24;
    @Key("inventory.title")
    public String INVENTORY_TITLE = "<green><owner>'s</green> <white>default sellchest";
    @Key("inventory.size")
    @Comment("""
            Must be divisible by 9!
            """)
    public int INVENTORY_SIZE = 45;
    @Key("inventory.items")
    public List<Map<Object, Object>> INVENTORY_ITEMS = List.of();
    @Key("hologram.height")
    public double HOLOGRAM_HEIGHT = 1.5;
    @Key("hologram.content")
    public List<String> HOLOGRAM_CONTENT = List.of();
    @Key("item")
    public Map<?, ?> ITEM_SECTION = Map.of();

    private final String fileName;

    public ChestConfig(String fileName) {
        this.fileName = fileName;
    }

    public void reload() {
        FileUtils.extractFile(ChestConfig.class, fileName, FileUtils.PLUGIN_DIRECTORY.resolve("chests"), false);

        this.reload(FileUtils.PLUGIN_DIRECTORY.resolve("chests").resolve(fileName), ChestConfig.class, this);
    }

    @Override
    public String toString() {
        return "ChestConfig{" +
                "BLOCK_TYPE='" + BLOCK_TYPE + '\'' +
                ", BOOSTER=" + BOOSTER +
                ", SHIFT_CLICK_OPEN='" + SHIFT_CLICK_OPEN + '\'' +
                ", SELL_INTERVAL=" + SELL_INTERVAL +
                ", AUTO_SELL=" + AUTO_SELL +
                ", COLLECT_CHUNK=" + COLLECT_CHUNK +
                ", DELETE_UNSELLABLE=" + DELETE_UNSELLABLE +
                ", HOLOGRAM=" + HOLOGRAM +
                ", BANK=" + BANK +
                ", INSTANT_COLLECT=" + INSTANT_COLLECT +
                ", CHARGE=" + CHARGE +
                ", INVENTORY_TITLE='" + INVENTORY_TITLE + '\'' +
                ", INVENTORY_SIZE=" + INVENTORY_SIZE +
                ", INVENTORY_ITEMS=" + INVENTORY_ITEMS +
                ", HOLOGRAM_HEIGHT=" + HOLOGRAM_HEIGHT +
                ", HOLOGRAM_CONTENT=" + HOLOGRAM_CONTENT +
                ", ITEM_SECTION=" + ITEM_SECTION +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
