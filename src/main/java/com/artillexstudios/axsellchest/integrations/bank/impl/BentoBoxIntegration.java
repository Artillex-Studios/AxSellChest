package com.artillexstudios.axsellchest.integrations.bank.impl;

import com.artillexstudios.axsellchest.integrations.bank.BankIntegration;
import org.bukkit.OfflinePlayer;
import world.bentobox.bank.Bank;
import world.bentobox.bank.data.Money;
import world.bentobox.bank.data.TxType;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Optional;

public class BentoBoxIntegration implements BankIntegration {
    private Bank bank;

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        User user = User.getInstance(player);
        for (Island island : BentoBox.getInstance().getIslandsManager().getIslands()) {
            boolean contains = island.getMembers().containsKey(player.getUniqueId());
            if (contains) {
                bank.getBankManager().deposit(user, island, Money.of(amount), TxType.UNKNOWN);
                return true;
            }
        }

        return false;
    }

    @Override
    public void reload() {
        Optional<Bank> bank = BentoBox.getInstance().getAddonsManager().getAddonByMainClassName("world.bentobox.bank.Bank");
        bank.ifPresent(value -> this.bank = value);
    }
}
