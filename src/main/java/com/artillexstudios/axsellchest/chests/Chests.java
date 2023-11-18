package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axsellchest.utils.ChunkPos;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Chests {
    private static final Object mutex = new Object();
    private static final ArrayList<ChunkPos> chunks = new ArrayList<>();

    public static void startTicking(Chunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();
        UUID worldUUID = chunk.getWorld().getUID();

        synchronized (mutex) {
            ArrayList<ChunkPos> chunks = Chests.chunks;
            int chunksSize = chunks.size();

            for (int i = 0; i < chunksSize; i++) {
                ChunkPos pos = chunks.get(i);
                if (pos.getX() == x && pos.getZ() == z && pos.getWorldUUID() == worldUUID) {
                    System.out.println("Started ticking!" + x + " " + z);
                    pos.setTicking(true);
                    break;
                }
            }
        }
    }

    public static void stopTicking(Chunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();
        UUID worldUUID = chunk.getWorld().getUID();

        synchronized (mutex) {
            ArrayList<ChunkPos> chunks = Chests.chunks;
            int chunksSize = chunks.size();

            for (int i = 0; i < chunksSize; i++) {
                ChunkPos pos = chunks.get(i);
                if (pos.getX() == x && pos.getZ() == z && pos.getWorldUUID() == worldUUID) {
                    System.out.println("Stopped ticking! " + x + " " + z);
                    pos.setTicking(false);
                    break;
                }
            }
        }
    }

    public static void load(Chest chest) {
        Location location = chest.getLocation();
        World world = location.getWorld();
        if (world == null) return;

        int x = (int) Math.round(location.getX()) >> 4;
        int z = (int) Math.round(location.getZ()) >> 4;
        UUID worldUUID = world.getUID();

        synchronized (mutex) {
            ArrayList<ChunkPos> chunks = Chests.chunks;
            int chunksSize = chunks.size();

            ChunkPos chunkPos = null;

            for (int i = 0; i < chunksSize; i++) {
                ChunkPos pos = chunks.get(i);
                if (pos.getX() == x && pos.getZ() == z && pos.getWorldUUID() == worldUUID) {
                    chunkPos = pos;
                    break;
                }
            }

            if (chunkPos == null) {
                System.out.println("Adding new position");
                chunkPos = new ChunkPos(world, x, z);
                chunks.add(chunkPos);
            }

            chunkPos.addChest(chest);
        }
    }

    public static void remove(Chest chest) {
        Location location = chest.getLocation();
        World world = location.getWorld();
        if (world == null) return;
        System.out.println("Removing!");

        int x = (int) Math.round(location.getX()) >> 4;
        int z = (int) Math.round(location.getZ()) >> 4;
        UUID worldUUID = world.getUID();

        synchronized (mutex) {
            ArrayList<ChunkPos> chunks = Chests.chunks;

            Iterator<ChunkPos> iterator = chunks.iterator();
            while (iterator.hasNext()) {
                ChunkPos next = iterator.next();

                if (next.getX() == x && next.getZ() == z && next.getWorldUUID() == worldUUID) {
                    if (next.removeChest(chest)) {
                        iterator.remove();
                    }

                    break;
                }
            }
        }
    }

    public static List<Chest> getChests() {
        ArrayList<Chest> chests = new ArrayList<>(chunks.size());

        synchronized (mutex) {
            for (ChunkPos pos : Chests.chunks) {
                chests.addAll(pos.getChests());
            }

            return chests;
        }
    }

    public static Chest getChestAt(Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        UUID worldUUID = world.getUID();
        int x = (int) Math.round(location.getX()) >> 4;
        int z = (int) Math.round(location.getZ()) >> 4;

        ArrayList<ChunkPos> chunks = Chests.chunks;
        int chunksSize = chunks.size();

        for (int i = 0; i < chunksSize; i++) {
            ChunkPos pos = chunks.get(i);
            // There's no way that someone is interacting with a chest
            // In a chunk that's not tracked by us
            if (!pos.isTicking()) continue;

            if (pos.getX() == x && pos.getZ() == z && pos.getWorldUUID().equals(worldUUID)) {
                ArrayList<Chest> chests = pos.getChests();
                int chestSize = chests.size();

                for (int j = 0; j < chestSize; j++) {
                    Chest chest = chests.get(j);
                    if (!chest.getLocation().equals(location)) continue;

                    return chest;
                }

                return null;
            }
        }

        return null;
    }

    public static void tickHolograms() {
        synchronized (mutex) {
            ArrayList<ChunkPos> chunks = Chests.chunks;
            int chunksSize = chunks.size();

            for (int i = 0; i < chunksSize; i++) {
                ChunkPos pos = chunks.get(i);
                if (!pos.isTicking()) continue;

                ArrayList<Chest> chests = pos.getChests();
                int chestSize = chests.size();
                for (int j = 0; j < chestSize; j++) {
                    Chest chest = chests.get(j);
                    chest.updateHologram();
                }
            }
        }
    }

    protected static ArrayList<ChunkPos> getChunks() {
        synchronized (mutex) {
            return chunks;
        }
    }
}
