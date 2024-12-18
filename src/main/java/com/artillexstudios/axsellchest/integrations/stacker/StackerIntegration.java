package com.artillexstudios.axsellchest.integrations.stacker;

import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.Integration;
import com.artillexstudios.axsellchest.integrations.stacker.impl.AxStackerIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.impl.DefaultStackerIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.impl.RoseStackerIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.impl.WildStackerIntegration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import java.util.Locale;

public interface StackerIntegration extends Integration {

    long getAmount(Item item);

    void setAmount(Item item, long amount);

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
                    } else {
                        integration = new DefaultStackerIntegration();
                    }
                }
                case "wildstacker" -> {
                    if (Bukkit.getPluginManager().getPlugin("WildStacker") != null) {
                        integration = new WildStackerIntegration();
                    } else {
                        integration = new DefaultStackerIntegration();
                    }
                }
                case "axstacker" -> {
                    if (Bukkit.getPluginManager().getPlugin("AxStacker") != null) {
                        integration = new AxStackerIntegration();
                    } else {
                        integration = new DefaultStackerIntegration();
                    }
                }
                default -> {
                    integration = new DefaultStackerIntegration();
                }
            }

            integration.reload();
        }
    }
}
