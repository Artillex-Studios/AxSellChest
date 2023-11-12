package com.artillexstudios.axsellchest.config.impl;

import com.artillexstudios.axsellchest.config.AbstractConfig;
import com.artillexstudios.axsellchest.utils.FileUtils;

public class Config extends AbstractConfig {

    @Key("integrations.stacker")
    @Comment("""
            Can be RoseStacker, WildStacker, or default
            """)
    private static final String STACKER_INTEGRATION = "RoseStacker";

    @Key("integrations.economy")
    @Comment("""
            Can be Vault
            """)
    private static final String ECONOMY_INTEGRATION = "Vault";

    @Key("integrations.prices")
    @Comment("""
            Can be ShopGUIPlus or local
            """)
    private static final String PRICES_INTEGRATION = "ShopGUIPlus";

    private static final Config CONFIG = new Config();

    public static void reload() {
        FileUtils.extractFile(ChestConfig.class, "config.yml", FileUtils.PLUGIN_DIRECTORY, false);

        CONFIG.reload(FileUtils.PLUGIN_DIRECTORY.resolve("config.yml"), ChestConfig.class, null);
    }
}
