package com.artillexstudios.axsellchest.integrations.prices.impl;

import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusIntegration implements PricesIntegration {

    @Override
    public double getPrice(ItemStack itemStack, int amount) {
        double price = ShopGuiPlusApi.getItemStackPriceSell(itemStack) / itemStack.getAmount();
        return price == -1 ? 0 : price * amount;
    }
}
