package com.artillexstudios.axsellchest;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.data.impl.H2DataHandler;
import com.artillexstudios.axsellchest.listeners.ChunkListener;
import org.bukkit.Bukkit;

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
    }

    @Override
    public void disable() {
        DataHandler.QUEUE.stop();
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }
}
