package com.artillexstudios.axsellchest.menu;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.menu.actions.Actions;
import com.artillexstudios.axsellchest.menu.prices.Prices;
import com.artillexstudios.axsellchest.utils.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.HumanEntity;
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

        updateGui();
    }

    public void updateGui() {
        for (Map<Object, Object> inventoryItem : chest.getType().getConfig().INVENTORY_ITEMS) {
            ItemStack itemStack = new ItemBuilder(inventoryItem, Placeholder.unparsed("owner", chest.getOwnerName()), Placeholder.parsed("bank", chest.isBank() ? Messages.TOGGLE_ON : Messages.TOGGLE_OFF), Placeholder.parsed("autosell", chest.isAutoSell() ? Messages.TOGGLE_ON : Messages.TOGGLE_OFF), Placeholder.parsed("collectchunk", chest.isCollectChunk() ? Messages.TOGGLE_ON : Messages.TOGGLE_OFF), Placeholder.parsed("deleteunsellable", chest.isDeleteUnsellable() ? Messages.TOGGLE_ON : Messages.TOGGLE_OFF)).get();
            GuiItem guiItem = getGuiItem(inventoryItem, itemStack);

            for (Integer slots : slots((List<String>) inventoryItem.getOrDefault("slots", List.of()))) {
                gui.updateItem(slots, guiItem);
            }
        }
    }

    @NotNull
    private GuiItem getGuiItem(Map<Object, Object> inventoryItem, ItemStack itemStack) {
        GuiItem guiItem = new GuiItem(itemStack);

        guiItem.setAction(event -> {
            Player player = (Player) event.getWhoClicked();

            if (Prices.pay(player, (List<String>) inventoryItem.getOrDefault("prices", List.of()))) {
                Actions.run(player, this.chest, (List<String>) inventoryItem.getOrDefault("actions", List.of()));
                updateGui();
            }
        });
        return guiItem;
    }

    public void open(Player player) {
        gui.open(player);
    }

    public void close() {
        List<HumanEntity> viewers = gui.getInventory().getViewers();
        int viewersSize = viewers.size();

        for (int i = 0; i < viewersSize; i++) {
            HumanEntity viewer = viewers.get(i);
            viewer.closeInventory();
        }
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
