package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axapi.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Chest {
    private static final List<Item> EMPTY_ITEMS = Collections.emptyList();
    private final Location location;
    private final ChestType type;
    private final OfflinePlayer owner;
    private final ArrayList<Item> items = new ArrayList<>();
    private final UUID ownerUUID;
    private boolean ticking = false;
    private double moneyMade;
    private long itemsSold;
    private int locationId;
    private boolean autoSell;
    private boolean collectChunk;
    private boolean deleteUnsellable;
    private boolean bank;
    private long charge;
    private Inventory inventory;

    public Chest(ChestType type, Location location, UUID ownerUUID, long itemsSold, double moneyMade, int locationId, boolean autoSell, boolean collectChunk, boolean deleteUnsellable, boolean bank, long charge) {
        this.type = type;
        this.location = location;
        this.owner = Bukkit.getOfflinePlayer(ownerUUID);
        this.itemsSold = itemsSold;
        this.moneyMade = moneyMade;
        this.locationId = locationId;
        this.ownerUUID = ownerUUID;
        this.autoSell = autoSell;
        this.collectChunk = collectChunk;
        this.deleteUnsellable = deleteUnsellable;
        this.bank = bank;
        this.charge = charge;

        Chests.load(this);

        Scheduler.get().runAt(location, task -> {
            BlockState state = location.getBlock().getState();
            if (state instanceof Container container) {
                inventory = container.getInventory();
            }
        });
    }

    public void tick() {
        if (!ticking) return;
        if (type.getChestTick() % ChestTicker.getTick() != 0) return;
        if (inventory == null) return;


    }

    private List<Item> getItemsInChunk() {
        Entity[] entities = location.getChunk().getEntities();
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

    public boolean isBank() {
        return bank;
    }

    public boolean isCollectChunk() {
        return collectChunk;
    }

    public boolean isDeleteUnsellable() {
        return deleteUnsellable;
    }

    public long getCharge() {
        return charge;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public void setTicking(boolean ticking) {
        this.ticking = ticking;
    }

    public void onLoad() {

    }

    public void onUnload() {
        this.items.clear();
        this.items.trimToSize();
        inventory = null;
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