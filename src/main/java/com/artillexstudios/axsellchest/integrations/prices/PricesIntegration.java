package com.artillexstudios.axsellchest.integrations.prices;

import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.Integration;
import com.artillexstudios.axsellchest.integrations.prices.impl.ShopGUIPlusIntegration;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public interface PricesIntegration extends Integration {

    double getPrice(ItemStack itemStack, int amount);

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
            }

            if (integration != null) {
                integration.reload();
            }
        }
    }
}
