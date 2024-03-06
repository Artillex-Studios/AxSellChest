package com.artillexstudios.axsellchest.integrations.prices.impl;

import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import fr.maxlego08.zshop.api.ShopManager;
import fr.maxlego08.zshop.api.buttons.ItemButton;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;

public class ZShopIntegration implements PricesIntegration {
    private ShopManager shopManager;

    @Override
    public double getPrice(ItemStack itemStack, int amount) {
        Optional<ItemButton> button = shopManager.getItemButton(itemStack.getType());
        return button.map(itemButton -> itemButton.getSellPrice(amount)).orElse(0.0);
    }

    @Override
    public void reload() {
        RegisteredServiceProvider<ShopManager> rsp = Bukkit.getServer().getServicesManager().getRegistration(ShopManager.class);
        shopManager = rsp.getProvider();
    }
}
