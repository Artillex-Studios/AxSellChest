package com.artillexstudios.axsellchest.integrations.stacker;

import com.artillexstudios.axsellchest.integrations.Integration;
import org.bukkit.entity.Item;

public interface StackerIntegration extends Integration {

    int getAmount(Item item);

    void setAmount(Item item, int amount);

    Companion COMPANION = new Companion();

    static StackerIntegration getInstance() {
        return COMPANION.integration;
    }

    class Companion {
        StackerIntegration integration;

        public void reload() {

        }
    }
}
