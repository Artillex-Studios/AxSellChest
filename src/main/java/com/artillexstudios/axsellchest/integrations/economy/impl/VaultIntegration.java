package com.artillexstudios.axsellchest.integrations.economy.impl;

import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegration implements EconomyIntegration {
    private Economy economy;

    @Override
    public void take(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public void give(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    @Override
    public void reload() {
        RegisteredServiceProvider<Economy> economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economy == null) {
            return;
        }

        this.economy = economy.getProvider();
    }
}
