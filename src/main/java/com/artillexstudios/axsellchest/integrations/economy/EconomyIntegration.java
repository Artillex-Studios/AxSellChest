package com.artillexstudios.axsellchest.integrations.economy;

import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.Integration;
import com.artillexstudios.axsellchest.integrations.economy.impl.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Locale;

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
            switch (Config.ECONOMY_INTEGRATION.toLowerCase(Locale.ENGLISH)) {
                case "vault" -> {
                    if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                        integration = new VaultIntegration();
                    }
                }
            }

            if (integration != null) {
                integration.reload();
            }
        }
    }
}
