package com.artillexstudios.axsellchest.integrations.stacker.impl;

import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import org.bukkit.entity.Item;

public class DefaultStackerIntegration implements StackerIntegration {

    @Override
    public long getAmount(Item item) {
        return item.getItemStack().getAmount();
    }

    @Override
    public void setAmount(Item item, long amount) {
        item.getItemStack().setAmount((int) amount);
    }
}
