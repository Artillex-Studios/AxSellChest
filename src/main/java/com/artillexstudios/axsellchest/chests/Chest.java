package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramFactory;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellchest.integrations.economy.EconomyIntegration;
import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import com.artillexstudios.axsellchest.integrations.stacker.StackerIntegration;
import com.artillexstudios.axsellchest.menu.Menu;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class Chest {
    private static final List<Item> EMPTY_ITEMS = Collections.emptyList();
    private final Location location;
    private final ChestType type;
    private final OfflinePlayer owner;
    private final ArrayList<Item> items = new ArrayList<>();
    private final UUID ownerUUID;
    private final String ownerName;
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
    private Hologram hologram;
    private final Menu menu;

    public Chest(ChestType type, Location location, UUID ownerUUID, long itemsSold, double moneyMade, int locationId, boolean autoSell, boolean collectChunk, boolean deleteUnsellable, boolean bank, long charge) {
        this.type = type;
        this.location = location;
        this.owner = Bukkit.getOfflinePlayer(ownerUUID);
        this.ownerName = owner.getName();
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

        Scheduler.get().runAt(location, task -> {
            BlockState state = location.getBlock().getState();
            if (state instanceof Container container) {
                inventory = container.getInventory();
            }
        });
    }

    public void tick() {
        if (!ticking) return;
        if (this.type.getConfig().CHARGE && charge < System.currentTimeMillis()) return;
        if (ChestTicker.getTick() % type.getChestTick() != 0) return;

        System.out.println("Yes");
        double moneyMade = 0;
        if (autoSell) {
            System.out.println("Autosell");
            if (collectChunk) {
                System.out.println("Collectchunk");
                moneyMade += instantCollectAndSell();
            }

            moneyMade += sellInventory();
        } else {
            if (collectChunk) {
                collectToChest();
            }
        }

        moneyMade *= this.type.getConfig().BOOSTER;

        EconomyIntegration.getInstance().give(owner, moneyMade);
        System.out.printf("Oreo %s", moneyMade);
    }

    public void updateHologram() {
        if (!this.type.getConfig().HOLOGRAM) return;
        if (hologram == null) {
            hologram = HologramFactory.get().spawnHologram(location.clone().add(0, this.type.getConfig().HOLOGRAM_HEIGHT, 0), "chest-" + Serializers.LOCATION.serialize(this.location));

            List<String> lines = this.type.getConfig().HOLOGRAM_CONTENT;
            int contentSize = lines.size();
            for (int i = 0; i < contentSize; i++) {
                String line = lines.get(i);
                hologram.addLine(StringUtils.format(line, Placeholder.parsed("items_sold", String.valueOf(itemsSold)),
                        Placeholder.parsed("money_made", String.valueOf(moneyMade)),
                        Placeholder.parsed("charge", StringUtils.formatTime(System.currentTimeMillis() - charge)),
                        Placeholder.parsed("owner", ownerName)
                ));
            }
        }

        List<String> lines = this.type.getConfig().HOLOGRAM_CONTENT;
        int contentSize = lines.size();

        for (int i = 0; i < contentSize; i++) {
            String line = lines.get(i);
            hologram.setLine(i, StringUtils.format(line, Placeholder.parsed("items_sold", String.valueOf(itemsSold)),
                    Placeholder.parsed("money_made", String.valueOf(moneyMade)),
                    Placeholder.parsed("charge", StringUtils.formatTime(System.currentTimeMillis() - charge)),
                    Placeholder.parsed("owner", ownerName)
            ));
        }
    }

    public void remove() {
        this.ticking = false;
        Chests.remove(this);
    }

    public double instantCollectAndSell() {
        List<Item> chunkItems = getItemsInChunk();
        int chunkItemSize = chunkItems.size();
        if (chunkItemSize == 0) return 0;

        double price = 0;
        for (int i = 0; i < chunkItemSize; i++) {
            Item item = chunkItems.get(i);
            ItemStack itemStack = item.getItemStack();
            int amount = StackerIntegration.getInstance().getAmount(item);

            double itemPrice = PricesIntegration.getInstance().getPrice(this.owner, itemStack, amount);
            if (itemPrice == 0) {
                continue;
            }

            // Remove the item only if it can be sold
            item.remove();

            price += itemPrice;
        }

        return price;
    }

    public double sellInventory() {
        if (inventory == null) return 0;
        if (inventory.isEmpty()) return 0;

        ListIterator<ItemStack> iterator = inventory.iterator();
        double price = 0;
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            if (itemStack == null || itemStack.getType().isAir()) continue;

            int amount = itemStack.getAmount();
            double itemPrice = PricesIntegration.getInstance().getPrice(this.owner, itemStack, amount);
            if (itemPrice == 0) continue;

            iterator.remove();
            price += itemPrice;
        }

        return price;
    }

    public void collectToChest() {
        if (inventory == null) return;
        List<Item> chunkItems = getItemsInChunk();
        int chunkItemSize = chunkItems.size();
        if (chunkItemSize == 0) return;

        for (int i = 0; i < chunkItemSize; i++) {
            Item item = chunkItems.get(i);
            ItemStack itemStack = item.getItemStack();
            int amount = StackerIntegration.getInstance().getAmount(item);

            double itemPrice = PricesIntegration.getInstance().getPrice(this.owner, itemStack, amount);
            if (itemPrice == 0) {
                continue;
            }

            itemStack.setAmount(amount);
            HashMap<Integer, ItemStack> remaining = inventory.addItem(itemStack);
            if (remaining.isEmpty()) {
                // Remove the item only if it can be sold
                item.remove();
            } else {
                // TODO: Test
                StackerIntegration.getInstance().setAmount(item, amount);
            }
        }
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

    public boolean isBank() {
        return bank;
    }

    public boolean isCollectChunk() {
        return collectChunk;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public boolean isDeleteUnsellable() {
        return deleteUnsellable;
    }

    public long getCharge() {
        return charge;
    }

    public void setAutoSell(boolean autoSell) {
        this.autoSell = autoSell;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
    }

    public void setCollectChunk(boolean collectChunk) {
        this.collectChunk = collectChunk;
    }

    public void setDeleteUnsellable(boolean deleteUnsellable) {
        this.deleteUnsellable = deleteUnsellable;
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

    public void onLoad() {

    }

    public void onReload() {
        this.hologram.remove();
        this.hologram = null;
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
