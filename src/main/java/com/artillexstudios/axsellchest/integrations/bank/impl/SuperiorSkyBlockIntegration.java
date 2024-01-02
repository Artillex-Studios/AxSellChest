package com.artillexstudios.axsellchest.integrations.bank.impl;

import com.artillexstudios.axsellchest.integrations.bank.BankIntegration;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.bank.IslandBank;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public class SuperiorSkyBlockIntegration implements BankIntegration {

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
        if (superiorPlayer == null) return false;
        Island island = superiorPlayer.getIsland();
        if (island == null) return false;
        IslandBank bank = island.getIslandBank();
        BigDecimal decimal = BigDecimal.valueOf(amount);

        if (bank.canDepositMoney(decimal)) {
            bank.depositAdminMoney(Bukkit.getConsoleSender(), decimal);
            return true;
        }

        return false;
    }
}
