package com.artillexstudios.axsellchest.integrations.stacker.impl;

import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedItem;
import org.bukkit.entity.Item;

public class RoseStackerIntegration implements StackerIntegration {
    private RoseStackerAPI api;

    @Override
    public int getAmount(Item item) {
        StackedItem stackedItem = api.getStackedItem(item);
        return stackedItem == null ? item.getItemStack().getAmount() : stackedItem.getStackSize();
    }

    @Override
    public void setAmount(Item item, int amount) {
        StackedItem stackedItem = api.getStackedItem(item);

        if (stackedItem != null) {
            stackedItem.setStackSize(amount);
        } else {
            item.getItemStack().setAmount(amount);
        }
    }

    @Override
    public void reload() {
        api = RoseStackerAPI.getInstance();
    }
}
