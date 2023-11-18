package com.artillexstudios.axsellchest.config.impl;

import com.artillexstudios.axsellchest.config.AbstractConfig;
import com.artillexstudios.axsellchest.utils.FileUtils;

public class Messages extends AbstractConfig {
    @Key("prefix")
    public static String PREFIX = "<gradient:#FFAA00:#FFBB00><b>AxSellchest</gradient> <white>|</white> ";

    @Key("reload")
    public static String RELOAD = "<green>Successfully reloaded in <white><time>ms</green>!";

    @Key("give-success")
    public static String GIVE_SUCCESS = "<green>You have successfully given <player> <amount>x <type> chests!";

    @Key("receive-success")
    public static String RECEIVE_SUCCESS = "<green>You have been given <amount>x <type> chests!";

    @Key("toggle.on")
    public static String TOGGLE_ON = "<green>Enabled";

    @Key("toggle.off")
    public static String TOGGLE_OFF = "<red>Disabled";

    @Key("sell")
    public static String SELL_MESSAGE = "<white>Sold <amount> items for <green>$<price></green> in the last 60 seconds!";

    @Key("place-limit-reached")
    public static String PLACE_LIMIT_REACHED = "<red>You have reached the maximum number of chests you can place!</red> <gray>(<placed>/<max>)</gray>!";

    @Key("place")
    public static String PLACE = "<green>You have placed a sellchest!</red> <gray>(<placed>/<max>)</gray>!";


    private static final Messages MESSAGES = new Messages();

    public static void reload() {
        FileUtils.extractFile(Messages.class, "messages.yml", FileUtils.PLUGIN_DIRECTORY, false);

        MESSAGES.reload(FileUtils.PLUGIN_DIRECTORY.resolve("messages.yml"), Messages.class, null);
    }
}
