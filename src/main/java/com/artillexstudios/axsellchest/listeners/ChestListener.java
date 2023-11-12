package com.artillexstudios.axsellchest.listeners;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.AxSellChestPlugin;
import com.artillexstudios.axsellchest.chests.Chest;
import com.artillexstudios.axsellchest.chests.ChestType;
import com.artillexstudios.axsellchest.chests.ChestTypes;
import com.artillexstudios.axsellchest.chests.Chests;
import com.artillexstudios.axsellchest.config.impl.ChestConfig;
import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.config.impl.Messages;
import com.artillexstudios.axsellchest.data.DataHandler;
import com.artillexstudios.axsellchest.utils.Keys;
import com.artillexstudios.axsellchest.utils.PermissionUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class ChestListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        String type = meta.getPersistentDataContainer().get(Keys.CHEST_TYPE, PersistentDataType.STRING);
        if (type == null) return;

        ChestType chestType = ChestTypes.valueOf(type);
        if (chestType == null) return;
        ChestConfig config = chestType.getConfig();
        Long itemSold = meta.getPersistentDataContainer().get(Keys.ITEMS_SOLD, PersistentDataType.LONG);
        Double moneyMade = meta.getPersistentDataContainer().get(Keys.MONEY_MADE, PersistentDataType.DOUBLE);
        if (itemSold == null || moneyMade == null) return;
        Player player = event.getPlayer();
        int maxAmount = PermissionUtils.getChestLimit(player);
        event.setCancelled(true);

        DataHandler.QUEUE.submit(() -> {
            int placed = AxSellChestPlugin.getInstance().getDataHandler().getChests(player.getUniqueId());

            if (placed > maxAmount) {
                player.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.PLACE_LIMIT_REACHED, Placeholder.parsed("placed", String.valueOf(placed)), Placeholder.parsed("max", String.valueOf(maxAmount))));
                return;
            }

            Location location = event.getBlockPlaced().getLocation();
            Scheduler.get().runAt(location, task -> {
                location.getWorld().getBlockAt(location).setType(Material.matchMaterial(chestType.getConfig().BLOCK_TYPE));
            });

            int locationId = AxSellChestPlugin.getInstance().getDataHandler().getLocationId(location);

            Chest chest = new Chest(chestType, location, player.getUniqueId(), itemSold, moneyMade, locationId, config.AUTO_SELL, config.COLLECT_CHUNK, config.DELETE_UNSELLABLE, config.BANK, 0);
            AxSellChestPlugin.getInstance().getDataHandler().saveChest(chest);
            Chests.startTicking(location.getChunk());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        World world = location.getWorld();
        if (world == null) return;

        Chest chest = Chests.getChestAt(location);

        if (chest != null) {
            event.setDropItems(false);
            ItemStack item = chest.getType().getItem(chest.getItemsSold(), chest.getMoneyMade());

            if (Config.PLACE_IN_INVENTORY) {
                HashMap<Integer, ItemStack> items = event.getPlayer().getInventory().addItem(item);

                items.forEach((slot, itemStack) -> {
                    world.dropItem(location, itemStack);
                });
            } else {
                world.dropItem(location, item);
            }

            chest.remove();
            DataHandler.QUEUE.submit(() -> {
                AxSellChestPlugin.getInstance().getDataHandler().deleteChest(chest);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getType() == Material.AIR) return;

        Chest chest = Chests.getChestAt(clickedBlock.getLocation());
        if (chest == null) return;

        chest.getMenu().open(event.getPlayer());
    }
}
