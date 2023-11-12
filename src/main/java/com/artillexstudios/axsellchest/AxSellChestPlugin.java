package com.artillexstudios.axsellchest;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axsellchest.chests.ChestTicker;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.data.impl.H2DataHandler;
import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.artillexstudios.axsellchest.listeners.ChestListener;
import com.artillexstudios.axsellchest.listeners.ChunkListener;
import org.bukkit.Bukkit;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AxSellChestPlugin extends AxPlugin {
    private static AxSellChestPlugin INSTANCE;
    private DataHandler dataHandler;

    public static AxSellChestPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void enable() {
        INSTANCE = this;
        this.dataHandler = new H2DataHandler();

        Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestListener(), this);

        reload();

        ChestTicker.startTicking();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(Chests::tickHolograms, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        DataHandler.QUEUE.stop();
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    @Override
    public void reload() {
        Config.reload();
        ChestTypes.reload();

        EconomyIntegration.COMPANION.reload();
        StackerIntegration.COMPANION.reload();
        PricesIntegration.COMPANION.reload();
    }
}
