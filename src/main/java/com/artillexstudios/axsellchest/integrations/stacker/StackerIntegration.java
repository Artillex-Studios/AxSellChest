package com.artillexstudios.axsellchest.integrations.stacker;

import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.Integration;
import com.artillexstudios.axsellchest.integrations.stacker.impl.RoseStackerIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import java.util.Locale;

public interface StackerIntegration extends Integration {

    int getAmount(Item item);

    void setAmount(Item item, int amount);

    Companion COMPANION = new Companion();

    static StackerIntegration getInstance() {
        return COMPANION.integration;
    }

    class Companion {
        StackerIntegration integration;

        public void reload() {
            switch (Config.STACKER_INTEGRATION.toLowerCase(Locale.ENGLISH)) {
                case "rosestacker" -> {
                    if (Bukkit.getPluginManager().getPlugin("RoseStacker") != null) {
                        integration = new RoseStackerIntegration();
                    }
                }
            }
        }
    }
}
