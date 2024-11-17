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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChestListener implements Listener {
    private static final BlockFace[] NEIGHBOUR = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final List<ItemStack> ITEMS = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.CHEST || event.getBlockPlaced().getType() == Material.TRAPPED_CHEST) {
            for (int i = 0; i < 4; i++) {
                BlockFace face = NEIGHBOUR[i];
                Block relative = event.getBlock().getRelative(face);
                Location location = relative.getLocation();
                if (Chests.getChestAt(location) != null) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        ItemStack itemStack = event.getItemInHand();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        String type = meta.getPersistentDataContainer().get(Keys.CHEST_TYPE, PersistentDataType.STRING);
        if (type == null) return;

        ChestType chestType = ChestTypes.valueOf(type);
        if (chestType == null) return;
        ChestConfig config = chestType.getConfig();
        String itemSold = meta.getPersistentDataContainer().get(Keys.ITEMS_SOLD, PersistentDataType.STRING);
        String moneyMade = meta.getPersistentDataContainer().get(Keys.MONEY_MADE, PersistentDataType.STRING);
        if (itemSold == null || moneyMade == null) return;
        Player player = event.getPlayer();
        int maxAmount = PermissionUtils.getChestLimit(player);
        event.setCancelled(true);

        // We don't want the item to be placed if it's already being placed
        if (meta.getPersistentDataContainer().has(Keys.PLACED, PersistentDataType.BYTE)) return;

        meta.getPersistentDataContainer().set(Keys.PLACED, PersistentDataType.BYTE, (byte) 1);
        ITEMS.add(itemStack);
        itemStack.setItemMeta(meta);
        DataHandler.QUEUE.submit(() -> {
            Location location = event.getBlockPlaced().getLocation();
            int placed = AxSellChestPlugin.getInstance().getDataHandler().getChests(player.getUniqueId());

            if (placed + 1 > maxAmount) {
                player.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.PLACE_LIMIT_REACHED, Placeholder.parsed("placed", String.valueOf(placed)), Placeholder.parsed("max", String.valueOf(maxAmount))));
                Scheduler.get().runAt(location, task -> {
                    meta.getPersistentDataContainer().remove(Keys.PLACED);
                    itemStack.setItemMeta(meta);
                    ITEMS.remove(itemStack);
                });
                return;
            }

            Scheduler.get().runAt(location, task -> {
                location.getWorld().getBlockAt(location).setType(Material.matchMaterial(chestType.getConfig().BLOCK_TYPE));
                meta.getPersistentDataContainer().remove(Keys.PLACED);
                itemStack.setItemMeta(meta);
                itemStack.setAmount(itemStack.getAmount() - 1);
                ITEMS.remove(itemStack);
            });

            int locationId = AxSellChestPlugin.getInstance().getDataHandler().getLocationId(location);

            Chest chest = new Chest(chestType, location, player.getUniqueId(), itemSold, moneyMade, locationId, config.AUTO_SELL, config.COLLECT_CHUNK, config.DELETE_UNSELLABLE, config.BANK, 0);
            AxSellChestPlugin.getInstance().getDataHandler().saveChest(chest);
            Chests.startTicking(location.getChunk());
            player.sendMessage(StringUtils.formatToString(Messages.PREFIX + Messages.PLACE, Placeholder.parsed("placed", String.valueOf(placed + 1)), Placeholder.parsed("max", String.valueOf(maxAmount))));
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        World world = location.getWorld();
        if (world == null) return;

        Chest chest = Chests.getChestAt(location);

        if (chest != null) {
            if (chest.isBroken()) {
                event.setCancelled(true);
                return;
            }

            event.setDropItems(false);
            chest.remove();
            DataHandler.QUEUE.submit(() -> {
                AxSellChestPlugin.getInstance().getDataHandler().deleteChest(chest);
            });

            ItemStack item = chest.getType().getItem(chest.getItemsSold(), chest.getMoneyMade());

            if (Config.PLACE_IN_INVENTORY) {
                HashMap<Integer, ItemStack> items = event.getPlayer().getInventory().addItem(item);

                items.forEach((slot, itemStack) -> {
                    world.dropItem(location, itemStack);
                });
            } else {
                world.dropItem(location, item);
            }

            for (ItemStack content : chest.getInventory().getContents()) {
                if (content == null || content.getType().isAir()) continue;

                world.dropItem(location, content);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getType() == Material.AIR) return;

        Chest chest = Chests.getChestAt(clickedBlock.getLocation());
        if (chest == null) return;
        if (chest.isBroken()) return;

        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.getPlayer().closeInventory();

        chest.getMenu().open(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.getType().isAir()) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        if (!meta.getPersistentDataContainer().has(Keys.PLACED, PersistentDataType.BYTE)) return;

        event.setCancelled(true);
    }

    public static List<ItemStack> getItems() {
        return ITEMS;
    }
}
