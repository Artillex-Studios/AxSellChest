package com.artillexstudios.axsellchest.menu.prices.impl;

import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import com.artillexstudios.axsellchest.menu.prices.Price;
import org.bukkit.entity.Player;

public class EconomyPrice extends Price {

    public EconomyPrice() {
        super("economy");
    }

    @Override
    public boolean pay(Player player, String argument, boolean take) {
        double amount = Double.parseDouble(argument);

        if (take) {
            EconomyIntegration.getInstance().take(player, amount);
            return true;
        }

        return EconomyIntegration.getInstance().getBalance(player) >= amount;
    }
}
