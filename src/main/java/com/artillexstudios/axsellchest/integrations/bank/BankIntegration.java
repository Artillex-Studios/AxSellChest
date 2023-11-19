package com.artillexstudios.axsellchest.integrations.bank;

import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.Integration;
import com.artillexstudios.axsellchest.integrations.bank.impl.SuperiorSkyBlockIntegration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Locale;

public interface BankIntegration extends Integration {

    Companion COMPANION = new Companion();

    static BankIntegration getInstance() {
        return COMPANION.integration;
    }

    boolean deposit(OfflinePlayer player, double amount);

    class Companion {
        BankIntegration integration;

        public void reload() {
            switch (Config.BANK_INTEGRATION.toLowerCase(Locale.ENGLISH)) {
                case "superiorskyblock2" -> {
                    if (Bukkit.getPluginManager().getPlugin("SuperiorSkyBlock2") != null) {
                        integration = new SuperiorSkyBlockIntegration();
                    } else {
                        integration = (player, amount) -> false;
                    }
                }
                default -> integration = (player, amount) -> false;
            }

            integration.reload();
        }
    }
}
