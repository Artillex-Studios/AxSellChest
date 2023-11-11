package com.artillexstudios.axsellchest.utils;

import com.artillexstudios.axsellchest.chests.Chest;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.UUID;

public class ChunkPos {
    private final ArrayList<Chest> chests = new ArrayList<>(4);
    private final UUID worldUUID;
    private final int x;
    private final int z;
    private boolean ticking = false;

    public ChunkPos(World world, int x, int z) {
        this.worldUUID = world.getUID();
        this.x = x;
        this.z = z;
    }

    public void addChest(Chest chest) {
        this.chests.add(chest);
    }

    public boolean removeChest(Chest chest) {
        this.chests.remove(chest);

        return this.chests.isEmpty();
    }

    public void setTicking(boolean ticking) {
        this.ticking = ticking;

        ArrayList<Chest> chests = this.chests;
        int size = chests.size();

        for (int i = 0; i < size; i++) {
            Chest chest = chests.get(i);
            chest.setTicking(ticking);

            if (!ticking) {
                chest.onUnload();
            }
        }
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public UUID getWorldUUID() {
        return this.worldUUID;
    }

    public ArrayList<Chest> getChests() {
        return this.chests;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChunkPos chunkPos)) return false;

        if (x != chunkPos.x) return false;
        if (z != chunkPos.z) return false;
        return getWorldUUID().equals(chunkPos.getWorldUUID());
    }

    @Override
    public int hashCode() {
        int result = getWorldUUID().hashCode();
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "chests=" + chests +
                ", worldUUID=" + worldUUID +
                ", x=" + x +
                ", z=" + z +
                '}';
    }
}
