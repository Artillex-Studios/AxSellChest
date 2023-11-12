package com.artillexstudios.axsellchest.integrations.prices;

import com.artillexstudios.axsellchest.integrations.Integration;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public interface PricesIntegration extends Integration {

    double getPrice(OfflinePlayer player, ItemStack itemStack, int amount);

    Companion COMPANION = new Companion();

    static PricesIntegration getInstance() {
        return COMPANION.integration;
    }

    class Companion {
        PricesIntegration integration;

        public void reload() {

        }
    }
}
