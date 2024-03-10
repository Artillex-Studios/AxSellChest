package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramFactory;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.Placeholder;
import com.artillexstudios.axsellchest.config.impl.Config;
import com.artillexstudios.axsellchest.integrations.bank.BankIntegration;
import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.artillexstudios.axsellchest.menu.Menu;
import com.artillexstudios.axsellchest.utils.NMSUtils;
import com.artillexstudios.axsellchest.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.NumberFormatter;
import java.text.CompactNumberFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Chest {
    private static final List<Item> EMPTY_ITEMS = Collections.emptyList();
    private static final Logger log = LoggerFactory.getLogger(Chest.class);
    private static final NumberFormat formatter = NumberFormat.getCompactNumberInstance(Locale.ENGLISH, NumberFormat.Style.SHORT);
    private final Location location;
    private final ChestType type;
    private final OfflinePlayer owner;
    private final ArrayList<Item> items = new ArrayList<>();
    private final UUID ownerUUID;
    private final String ownerName;
    private final int locationId;
    private final Menu menu;
    private boolean ticking = false;
    private boolean broken = false;
    private double moneyMade;
    private long itemsSold;
    private boolean autoSell;
    private boolean collectChunk;
    private boolean deleteUnsellable;
    private boolean bank;
    private long charge;
    private Inventory inventory;
    private Hologram hologram;
    private Chunk chunk;

    public Chest(ChestType type, Location location, UUID ownerUUID, long itemsSold, double moneyMade, int locationId, boolean autoSell, boolean collectChunk, boolean deleteUnsellable, boolean bank, long charge) {
        this.type = type;
        this.location = location;
        this.owner = Bukkit.getOfflinePlayer(ownerUUID);
        String ownerName = owner.getName();
        this.ownerName = ownerName == null ? "---" : ownerName;
        this.itemsSold = itemsSold;
        this.moneyMade = moneyMade;
        this.locationId = locationId;
        this.ownerUUID = ownerUUID;
        this.autoSell = autoSell;
        this.collectChunk = collectChunk;
        this.deleteUnsellable = deleteUnsellable;
        this.bank = bank;
        this.charge = charge;

        menu = new Menu(this);

        Chests.load(this);
        updateHologram();
    }

    public void tick() {
        if (!ticking || broken) return;
        if (this.type.getConfig().CHARGE && charge < System.currentTimeMillis()) return;
        if (ChestTicker.getTick() % type.getChestTick() != 0) return;

        Scheduler.get().executeAt(location, () -> {
            double moneyMade = 0;
            if (autoSell) {
                if (collectChunk) {
                    moneyMade += instantCollectAndSell();
                }

                moneyMade += sellInventory();
            } else {
                if (collectChunk) {
                    collectToChest();
                }
            }

            moneyMade *= this.type.getConfig().BOOSTER;

            if (moneyMade <= 0) return;
            if (bank && !BankIntegration.getInstance().deposit(owner, moneyMade)) {
                EconomyIntegration.getInstance().give(owner, moneyMade);
                this.moneyMade += moneyMade;
                return;
            }

            EconomyIntegration.getInstance().give(owner, moneyMade);
            this.moneyMade += moneyMade;
        });
    }

    public void updateHologram() {
        if (!this.type.getConfig().HOLOGRAM) return;
        if (hologram == null) {
            hologram = HologramFactory.get().spawnHologram(location.clone().add(0.5, this.type.getConfig().HOLOGRAM_HEIGHT, 0.5), "chest-" + Serializers.LOCATION.serialize(this.location), 0.3);

            hologram.addPlaceholder(new Placeholder((player, s) -> s.replace("<items_sold>", String.valueOf(itemsSold)).replace("<money_made>", formatter.format(moneyMade)).replace("<charge>", TimeUtils.format(charge -  System.currentTimeMillis(), this)).replace("<owner>", ownerName)));

            List<String> lines = this.type.getConfig().HOLOGRAM_CONTENT;
            int contentSize = lines.size();
            for (int i = 0; i < contentSize; i++) {
                String line = lines.get(i);
                hologram.addLine(StringUtils.format(line));
            }
        }
    }

    public void remove() {
        this.broken = true;
        this.ticking = false;

        Chests.remove(this);
        if (this.hologram != null) {
            this.hologram.remove();
        }
    }

    public double instantCollectAndSell() {
        List<Item> chunkItems = getItemsInChunk();
        int chunkItemSize = chunkItems.size();
        if (chunkItemSize == 0) return 0;

        if (Config.DEBUG) {
            log.warn("We have {} items in the chunk! Now we're going through them...", chunkItems);
        }

        double price = 0;
        for (int i = 0; i < chunkItemSize; i++) {
            Item item = chunkItems.get(i);
            ItemStack itemStack = item.getItemStack();
            int amount = StackerIntegration.getInstance().getAmount(item);

            double itemPrice = PricesIntegration.getInstance().getPrice(itemStack, amount);
            if (itemPrice <= 0) {
                if (Config.DEBUG) {
                    log.warn("Continuing! Price: {}", itemPrice);
                }
                continue;
            }

            // Remove the item only if it can be sold
            item.remove();
            itemsSold += amount;

            price += itemPrice;
        }

        return price;
    }

    public double sellInventory() {
        if (inventory == null) return 0;
        if (NMSUtils.isEmpty(inventory)) return 0;

        double price = 0;
        ItemStack[] contents = inventory.getContents();
        int contentSize = contents.length;

        for (int i = 0; i < contentSize; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null || itemStack.getType().isAir()) continue;

            int amount = itemStack.getAmount();
            double itemPrice = PricesIntegration.getInstance().getPrice(itemStack, amount);
            if (itemPrice <= 0) {
                if (getType().getConfig().DELETE_UNSELLABLE) {
                    itemStack.setAmount(0);
                }
                continue;
            }

            itemsSold += amount;
            itemStack.setAmount(0);
            price += itemPrice;
        }

        return price;
    }

    public void collectToChest() {
        if (inventory == null) return;
        List<Item> chunkItems = getItemsInChunk();
        int chunkItemSize = chunkItems.size();
        if (chunkItemSize == 0) return;

        if (Config.DEBUG) {
            log.warn("We have {} items in the chunk! Now we're going through them...", chunkItems);
        }

        for (int i = 0; i < chunkItemSize; i++) {
            Item item = chunkItems.get(i);
            ItemStack itemStack = item.getItemStack();
            int amount = StackerIntegration.getInstance().getAmount(item);

            double itemPrice = PricesIntegration.getInstance().getPrice(itemStack, amount);
            if (itemPrice <= 0) {
                if (Config.DEBUG) {
                    log.warn("Continuing! Price: {}", itemPrice);
                }
                continue;
            }

            itemStack.setAmount(amount);
            HashMap<Integer, ItemStack> remaining = inventory.addItem(itemStack);
            if (remaining.isEmpty()) {
                // Remove the item only if it can be sold
                item.remove();
            } else {
                StackerIntegration.getInstance().setAmount(item, itemStack.getAmount());
            }
        }
    }

    private List<Item> getItemsInChunk() {
        Entity[] entities = chunk.getEntities();
        int entitySize = entities.length;
        // No entities in chunk, so we can just ignore
        if (entitySize == 0) {
            return EMPTY_ITEMS;
        }
        // We don't want to allocate a new list every time we want to get the items in a chunk,
        // because object allocation is expensive!
        items.clear();

        for (int i = 0; i < entitySize; i++) {
            Entity entity = entities[i];
            if (entity instanceof Item item) {
                items.add(item);
            }
        }

        return items;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getLocationId() {
        return locationId;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public ChestType getType() {
        return type;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public double getMoneyMade() {
        return moneyMade;
    }

    public long getItemsSold() {
        return itemsSold;
    }

    public boolean isAutoSell() {
        return autoSell;
    }

    public void setAutoSell(boolean autoSell) {
        this.autoSell = autoSell;
    }

    public boolean isBank() {
        return bank;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
    }

    public boolean isCollectChunk() {
        return collectChunk;
    }

    public void setCollectChunk(boolean collectChunk) {
        this.collectChunk = collectChunk;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public boolean isDeleteUnsellable() {
        return deleteUnsellable;
    }

    public void setDeleteUnsellable(boolean deleteUnsellable) {
        this.deleteUnsellable = deleteUnsellable;
    }

    public long getCharge() {
        return charge;
    }

    public void setCharge(long charge) {
        this.charge = charge;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public void setTicking(boolean ticking) {
        this.ticking = ticking;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public void onLoad(Chunk chunk) {
        this.chunk = chunk;

        Scheduler.get().runAt(location, task -> {
            if (!location.getWorld().isChunkLoaded(chunk)) return;

            BlockState state = location.getBlock().getState();
            if (state instanceof Container container) {
                this.inventory = container.getInventory();
            }
        });
    }

    public void onReload() {
        if (this.hologram != null) {
            this.hologram.remove();
            this.hologram = null;
            updateHologram();
        }

        menu.updateGui();
    }

    public void onUnload() {
        this.items.clear();
        this.items.trimToSize();
        inventory = null;
        chunk = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chest chest)) return false;

        return getLocation().equals(chest.getLocation());
    }

    @Override
    public int hashCode() {
        return getLocation().hashCode();
    }

    @Override
    public String toString() {
        return "Chest{" +
                "location=" + location +
                ", type=" + type +
                ", owner=" + owner +
                ", items=" + items +
                ", ownerUUID=" + ownerUUID +
                ", ticking=" + ticking +
                ", moneyMade=" + moneyMade +
                ", itemsSold=" + itemsSold +
                ", locationId=" + locationId +
                ", autoSell=" + autoSell +
                ", collectChunk=" + collectChunk +
                ", deleteUnsellable=" + deleteUnsellable +
                ", bank=" + bank +
                ", charge=" + charge +
                ", inventory=" + inventory +
                '}';
    }
}
