package com.artillexstudios.axsellchest.config.impl;

import com.artillexstudios.axsellchest.config.AbstractConfig;
import com.artillexstudios.axsellchest.utils.FileUtils;

public class Config extends AbstractConfig {

    @Key("integrations.stacker")
    @Comment("""
            Can be RoseStacker, WildStacker, or default
            \\""")
    public static String STACKER_INTEGRATION = "RoseStacker";

    @Key("integrations.economy")
    @Comment("""
            Can be Vault
            \\""")
    public static String ECONOMY_INTEGRATION = "Vault";

    @Key("integrations.prices")
    @Comment("""
            Can be ShopGUIPlus or EconomyShopGUI, or zShop
            \\""")
    public static String PRICES_INTEGRATION = "ShopGUIPlus";

    @Key("integrations.bank")
    @Comment("""
            Can be SuperiorSkyBlock or BentoBox
            \\""")
    public static String BANK_INTEGRATION = "SuperiorSkyBlock2";

    @Key("autosave-minutes")
    @Comment("""
            How often should we save data about chests?
            Note: this is only data like money made, items sold. Other changes are saved immediately.
            \\""")
    public static int AUTOSAVE_MINUTES = 1;

    @Key("default-chest-limit")
    @Comment("""
            This can be overwritten per player by giving them the
            axsellchest.limit.<amount> permission.
            \\""")
    public static int DEFAULT_CHEST_LIMIT = 10;

    @Key("place-in-inventory")
    public static boolean PLACE_IN_INVENTORY = true;

    @Key("debug")
    public static boolean DEBUG = false;

    private static final Config CONFIG = new Config();

    public static void reload() {
        FileUtils.extractFile(Config.class, "config.yml", FileUtils.PLUGIN_DIRECTORY, false);

        CONFIG.reload(FileUtils.PLUGIN_DIRECTORY.resolve("config.yml"), Config.class, null);
    }
}
