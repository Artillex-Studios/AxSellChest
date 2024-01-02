package com.artillexstudios.axsellchest;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.utils.Version;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestTicker;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.commands.AxSellChestCommand;
import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.data.impl.H2DataHandler;
import com.artillexstudios.axsellchest.integrations.bank.BankIntegration;
import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.artillexstudios.axsellchest.library.Libraries;
import com.artillexstudios.axsellchest.listeners.ChestListener;
import com.artillexstudios.axsellchest.listeners.ChunkListener;
import com.artillexstudios.axsellchest.listeners.WorldListener;
import com.artillexstudios.axsellchest.utils.Keys;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AxSellChestPlugin extends AxPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(AxSellChestPlugin.class);
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
            LOGGER.info("Loading library: {}", value.getLibrary().getGroupId());
            manager.loadLibrary(value.getLibrary());
        }
    }

    @Override
    public void enable() {
        if (Version.getServerVersion().isOlderThan(Version.v1_18)) {
            LOGGER.error("Your server version is not supported! Disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        INSTANCE = this;
        this.dataHandler = new H2DataHandler();
        this.dataHandler.setup();

        loadCommand();
        reload();

        // Retroactively load the chests in worlds that have already loaded
        List<World> worlds = Bukkit.getWorlds();
        int worldsSize = worlds.size();

        DataHandler.QUEUE.submit(() -> {
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

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            DataHandler.QUEUE.submit(() -> {
                for (Chest chest : Chests.getChests()) {
                    getDataHandler().saveChest(chest);
                }
            });
        }, 1, Config.AUTOSAVE_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void disable() {
        List<Chest> chests = Chests.getChests();
        int chestSize = chests.size();

        for (int i = 0; i < chestSize; i++) {
            Chest chest = chests.get(i);
            chest.setBroken(true);
            chest.getMenu().close();
        }

        for (ItemStack item : ChestListener.getItems()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.getPersistentDataContainer().remove(Keys.PLACED);
            item.setItemMeta(meta);
        }

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
        BankIntegration.COMPANION.reload();
    }

    private void loadCommand() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerValueResolver(ChestType.class, resolver -> {
            String type = resolver.popForParameter();

            return ChestTypes.valueOf(type);
        });

        commandHandler.getAutoCompleter().registerParameterSuggestions(ChestType.class, (args, sender, command) -> {
            return ChestTypes.getTypes().keySet();
        });

        commandHandler.register(new AxSellChestCommand());

        if (commandHandler.isBrigadierSupported()) {
            commandHandler.registerBrigadier();
        }
    }
}
