package com.artillexstudios.axsellchest.integrations.prices.impl;

import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.objects.ShopItem;
import org.bukkit.inventory.ItemStack;

public class EconomyShopGuiIntegration implements PricesIntegration {

    @Override
    public double getPrice(ItemStack itemStack, long amount) {
        ShopItem item = EconomyShopGUIHook.getShopItem(itemStack);
        //noinspection ConstantValue
        return item == null ? 0 : EconomyShopGUIHook.getItemSellPrice(item, itemStack) / itemStack.getAmount() * amount;
    }
}
