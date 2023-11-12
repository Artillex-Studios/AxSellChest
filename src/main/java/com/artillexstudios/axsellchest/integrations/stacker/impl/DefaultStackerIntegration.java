package com.artillexstudios.axsellchest.integrations.stacker.impl;

import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import org.bukkit.entity.Item;

public class DefaultStackerIntegration implements StackerIntegration {

    @Override
    public int getAmount(Item item) {
        return item.getItemStack().getAmount();
    }

    @Override
    public void setAmount(Item item, int amount) {
        item.getItemStack().setAmount(amount);
    }
}
