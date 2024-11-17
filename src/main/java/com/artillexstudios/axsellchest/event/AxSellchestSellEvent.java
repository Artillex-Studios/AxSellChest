package com.artillexstudios.axsellchest.event;

import com.artillexstudios.axsellchest.chests.Chest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AxSellchestSellEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Chest chest;
    private double amount;

    public AxSellchestSellEvent(Chest chest, double amount) {
        this.chest = chest;
        this.amount = amount;
    }

    public double amount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Chest chest() {
        return chest;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
