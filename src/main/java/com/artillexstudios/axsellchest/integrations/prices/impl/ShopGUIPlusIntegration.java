package com.artillexstudios.axsellchest.integrations.prices.impl;

import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusIntegration implements PricesIntegration {

    @Override
    public double getPrice(OfflinePlayer player, ItemStack itemStack, int amount) {
        itemStack.setAmount(1);

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            return ShopGuiPlusApi.getItemStackPriceSell(onlinePlayer, itemStack) * amount;
        }

        return ShopGuiPlusApi.getItemStackPriceSell(itemStack) * amount;
    }
}
