package com.artillexstudios.axsellchest.integrations.stacker.impl;

import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.artillexstudios.axstacker.api.AxStackerAPI;
import com.artillexstudios.axstacker.stack.item.StackedItem;
import org.bukkit.entity.Item;

public class AxStackerIntegration implements StackerIntegration {

    @Override
    public long getAmount(Item item) {
        StackedItem stackedItem = AxStackerAPI.getItemStack(item);
        return stackedItem == null ? 1 : stackedItem.getStackSize();
    }

    @Override
    public void setAmount(Item item, long amount) {
        StackedItem stackedItem = AxStackerAPI.getItemStack(item);
        if (stackedItem != null) {
            stackedItem.setStackSize(amount);
        } else {
            item.getItemStack().setAmount((int) amount);
        }
    }
}
