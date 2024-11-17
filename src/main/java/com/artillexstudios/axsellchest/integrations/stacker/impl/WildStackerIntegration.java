package com.artillexstudios.axsellchest.integrations.stacker.impl;

import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedItem;
import org.bukkit.entity.Item;

public class WildStackerIntegration implements StackerIntegration {

    @Override
    public long getAmount(Item item) {
        StackedItem stackedItem = WildStackerAPI.getStackedItem(item);
        return stackedItem == null ? item.getItemStack().getAmount() : stackedItem.getStackAmount();
    }

    @Override
    public void setAmount(Item item, long amount) {
        StackedItem stackedItem = WildStackerAPI.getStackedItem(item);

        if (stackedItem != null) {
            stackedItem.setStackAmount((int) amount, true);
        } else {
            item.getItemStack().setAmount((int) amount);
        }
    }
}
