package com.artillexstudios.axsellchest;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axsellchest.chests.ChestTicker;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.commands.AxSellChestCommand;
import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.data.impl.H2DataHandler;
import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.artillexstudios.axsellchest.library.Libraries;
import com.artillexstudios.axsellchest.listeners.ChestListener;
import com.artillexstudios.axsellchest.listeners.ChunkListener;
import com.artillexstudios.axsellchest.listeners.WorldListener;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AxSellChestPlugin extends AxPlugin {
    private static AxSellChestPlugin INSTANCE;
    private DataHandler dataHandler;

    public static AxSellChestPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void load() {
        BukkitLibraryManager manager = new BukkitLibraryManager(this);
        manager.addJitPack();
        manager.addMavenCentral();

        for (Libraries value : Libraries.values()) {
            manager.loadLibrary(value.getLibrary());
        }
    }

    @Override
    public void enable() {
        INSTANCE = this;
        this.dataHandler = new H2DataHandler();
        this.dataHandler.setup();

        loadCommand();
        reload();

        // Retroactively load the chests in worlds that have already loaded
        DataHandler.QUEUE.submit(() -> {
            List<World> worlds = Bukkit.getWorlds();
            int worldsSize = worlds.size();

            for (int i = 0; i < worldsSize; i++) {
                World world = worlds.get(i);

                ChestTypes.loadForWorld(world);
            }
        });

        Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);

        ChestTicker.startTicking();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(Chests::tickHolograms, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        dataHandler.disable();
        DataHandler.QUEUE.stop();
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    @Override
    public void reload() {
        Config.reload();
        Messages.reload();

        ChestTypes.reload();

        EconomyIntegration.COMPANION.reload();
        StackerIntegration.COMPANION.reload();
        PricesIntegration.COMPANION.reload();
    }

    private void loadCommand() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerValueResolver(ChestType.class, resolver -> {
            String type = resolver.popForParameter();

            return ChestTypes.valueOf(type);
        });

        commandHandler.getAutoCompleter().registerSuggestion("chestTypes", (args, sender, command) -> {
            return ChestTypes.getTypes().keySet();
        });

        commandHandler.register(new AxSellChestCommand());

        if (commandHandler.isBrigadierSupported()) {
            commandHandler.registerBrigadier();
        }
    }
}
