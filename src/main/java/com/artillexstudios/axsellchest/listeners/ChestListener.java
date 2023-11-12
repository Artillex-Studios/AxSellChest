package com.artillexstudios.axsellchest.listeners;

import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.config.impl.ChestConfig;
import com.artillexstudios.axsellchest.utils.Keys;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ChestListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        String type = itemStack.getItemMeta().getPersistentDataContainer().get(Keys.CHEST_TYPE, PersistentDataType.STRING);
        if (type == null) return;

        ChestType chestType = ChestTypes.valueOf(type);
        if (chestType == null) return;
        ChestConfig config = chestType.getConfig();

        Location location = event.getBlockPlaced().getLocation();
        int locationId = AxSellChestPlugin.getInstance().getDataHandler().getLocationId(location);

        new Chest(chestType, location, event.getPlayer().getUniqueId(), 0, 0, locationId, config.AUTO_SELL, config.COLLECT_CHUNK, config.DELETE_UNSELLABLE, config.BANK, 0);
        Chests.startTicking(location.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        if (Chests.isChestAt(location)) {
            // TODO: Handle break
            System.out.println("Chest!");
        }
    }
}
