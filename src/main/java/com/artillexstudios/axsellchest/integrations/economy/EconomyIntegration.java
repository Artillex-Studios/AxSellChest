package com.artillexstudios.axsellchest.integrations.economy;

import com.artillexstudios.axsellchest.integrations.Integration;
import org.bukkit.OfflinePlayer;

public interface EconomyIntegration extends Integration {

    void take(OfflinePlayer player, double amount);

    void give(OfflinePlayer player, double amount);

    double getBalance(OfflinePlayer player);

    Companion COMPANION = new Companion();

    static EconomyIntegration getInstance() {
        return COMPANION.integration;
    }

    class Companion {
        EconomyIntegration integration;

        public void reload() {

        }
    }
}
