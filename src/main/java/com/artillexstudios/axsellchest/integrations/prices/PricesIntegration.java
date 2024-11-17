package com.artillexstudios.axsellchest.integrations.prices;

import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.Integration;
import com.artillexstudios.axsellchest.integrations.prices.impl.AxGensIntegration;
import com.artillexstudios.axsellchest.integrations.prices.impl.EconomyShopGuiIntegration;
import com.artillexstudios.axsellchest.integrations.prices.impl.ShopGUIPlusIntegration;
import com.artillexstudios.axsellchest.integrations.prices.impl.ZShopIntegration;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public interface PricesIntegration extends Integration {

    double getPrice(ItemStack itemStack, long amount);

    Companion COMPANION = new Companion();

    static PricesIntegration getInstance() {
        return COMPANION.integration;
    }

    class Companion {
        PricesIntegration integration;

        public void reload() {
            switch (Config.PRICES_INTEGRATION.toLowerCase(Locale.ENGLISH)) {
                case "shopguiplus" -> {
                    if (Bukkit.getPluginManager().getPlugin("ShopGUIPlus") != null) {
                        integration = new ShopGUIPlusIntegration();
                    }
                }
                case "zshop" -> {
                    if (Bukkit.getPluginManager().getPlugin("zShop") != null) {
                        integration = new ZShopIntegration();
                    }
                }
                case "economyshopgui" -> {
                    if (Bukkit.getPluginManager().getPlugin("EconomyShopGUI") != null || Bukkit.getPluginManager().getPlugin("EconomyShopGUI-Premium") != null) {
                        integration = new EconomyShopGuiIntegration();
                    }
                }
                case "axgens" -> {
                    if (Bukkit.getPluginManager().getPlugin("AxGens") != null) {
                        integration = new AxGensIntegration();
                    }
                }
                default -> integration = ((itemStack, amount) -> 0);
            }

            if (integration != null) {
                integration.reload();
            }
        }
    }
}
