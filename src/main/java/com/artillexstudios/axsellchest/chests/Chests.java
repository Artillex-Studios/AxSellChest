package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axsellchest.utils.ChunkPos;
import com.artillexstudios.axsellchest.utils.Math;
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
                    System.out.println("Started ticking!");
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

        int x = Math.round(location.getX()) >> 4;
        int z = Math.round(location.getZ()) >> 4;
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

        int x = Math.round(location.getX()) >> 4;
        int z = Math.round(location.getZ()) >> 4;
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
        System.out.println("CCCC");

        UUID worldUUID = world.getUID();
        int x = Math.round(location.getX()) >> 4;
        int z = Math.round(location.getZ()) >> 4;

        ArrayList<ChunkPos> chunks = Chests.chunks;
        int chunksSize = chunks.size();

        for (int i = 0; i < chunksSize; i++) {
            ChunkPos pos = chunks.get(i);
            System.out.println("DDDD");
            // There's no way that someone is interacting with a chest
            // In a chunk that's not tracked by us
//            if (!pos.isTicking()) continue;
            System.out.println("EEEE" + pos.getX() + " " + x + " " + pos.getZ() + " " + pos.getZ() + " " + pos.getWorldUUID() + " " + worldUUID);

            if (pos.getX() == x && pos.getZ() == z && pos.getWorldUUID().equals(worldUUID)) {
                System.out.println("FFFF");
                ArrayList<Chest> chests = pos.getChests();
                int chestSize = chests.size();

                for (int j = 0; j < chestSize; j++) {
                    Chest chest = chests.get(j);
                    System.out.println("AAAA");
                    if (!chest.getLocation().equals(location)) continue;

                    System.out.println("BBBB");
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
                    Chest chest = chests.get(i);
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
