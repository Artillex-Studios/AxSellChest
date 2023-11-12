package com.artillexstudios.axsellchest.menu;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.menu.actions.Actions;
import com.artillexstudios.axsellchest.menu.prices.Prices;
import com.artillexstudios.axsellchest.utils.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Menu {
    private final Chest chest;
    private final Gui gui;

    public Menu(Chest chest) {
        this.chest = chest;

        gui = Gui.gui()
                .title(StringUtils.format(chest.getType().getConfig().INVENTORY_TITLE, Placeholder.parsed("owner", chest.getOwnerName())))
                .type(GuiType.CHEST)
                .rows(chest.getType().getConfig().INVENTORY_SIZE / 9)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        for (Map<Object, Object> inventoryItem : chest.getType().getConfig().INVENTORY_ITEMS) {
            ItemStack itemStack = new ItemBuilder(inventoryItem).get();
            GuiItem guiItem = new GuiItem(itemStack);

            guiItem.setAction(event -> {
                if (Prices.pay((Player) event.getWhoClicked(), (List<String>) inventoryItem.getOrDefault("prices", List.of()))) {
                    Actions.run((Player) event.getWhoClicked(), this.chest, (List<String>) inventoryItem.getOrDefault("actions", List.of()));
                }
            });

            gui.setItem(slots((List<String>) inventoryItem.getOrDefault("slots", List.of())), guiItem);
        }
    }

    public void open(Player player) {
        gui.open(player);
    }

    public List<Integer> slots(@NotNull List<String> slotString) {
        List<Integer> returnedSlots = new ArrayList<>();

        for (String s : slotString) {
            if (s.contains("-")) {
                String[] split = s.split("-");
                returnedSlots.addAll(getSlots(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            } else {
                returnedSlots.add(Integer.parseInt(s));
            }
        }

        return returnedSlots;
    }

    @NotNull
    private List<Integer> getSlots(int small, int max) {
        List<Integer> slots = new ArrayList<>();

        for (int i = small; i <= max; i++) {
            slots.add(i);
        }
        return slots;
    }
}
