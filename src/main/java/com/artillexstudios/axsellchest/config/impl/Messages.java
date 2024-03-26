package com.artillexstudios.axsellchest.config.impl;

import com.artillexstudios.axsellchest.config.AbstractConfig;
import com.artillexstudios.axsellchest.utils.FileUtils;

import java.util.List;

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

    @Key("place-limit-reached")
    public static String PLACE_LIMIT_REACHED = "<red>You have reached the maximum number of chests you can place!</red> <gray>(<placed>/<max>)</gray>!";

    @Key("place")
    public static String PLACE = "<green>You have placed a sellchest!</green> <gray>(<placed>/<max>)</gray>!";

    @Key("help")
    public static List<String> HELP = List.of(" ", "<gradient:#FFAA00:#FFBB00><b>AxSellchest</gradient> <white>|</white>", " <gray>- <white>/axsc reload <gray>| <#FFAA00>Reload the plugin", " <gray>- <white>/axsc give <player> <chest> (<amount>) <gray>| <#FFAA00>Give <amount> <chest> type of chest to <player>", " <gray>- <white>/axsc stats <gray>| <#FFAA00>Get the statistics of the plugin", " <gray>- <white>/axsc convert <voidchest|voidchestv2> <gray>| <#FFAA00>Convert data from a different plugin", "");

    @Key("time.day")
    public static String DAY = "d";

    @Key("time.hour")
    public static String HOUR = "h";

    @Key("time.minute")
    public static String MINUTE = "m";

    @Key("time.second")
    public static String SECOND = "s";

    private static final Messages MESSAGES = new Messages();

    public static void reload() {
        FileUtils.extractFile(Messages.class, "messages.yml", FileUtils.PLUGIN_DIRECTORY, false);

        MESSAGES.reload(FileUtils.PLUGIN_DIRECTORY.resolve("messages.yml"), Messages.class, null);
    }
}