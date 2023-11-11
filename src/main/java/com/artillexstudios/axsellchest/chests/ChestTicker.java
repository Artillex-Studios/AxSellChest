package com.artillexstudios.axsellchest.chests;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axsellchest.utils.ChunkPos;

import java.util.ArrayList;

public class ChestTicker {
    private static long tick;

    private static void tick() {
        ArrayList<ChunkPos> chunks = Chests.getChunks();
        int chunksSize = chunks.size();

        for (int i = 0; i < chunksSize; i++) {
            ChunkPos pos = chunks.get(i);
            // Nothing in the chunk should be ticking, we can continue
            if (!pos.isTicking()) continue;

            ArrayList<Chest> chests = pos.getChests();
            int chestsSize = chests.size();

            for (int j = 0; j < chestsSize; j++) {
                Chest chest = chests.get(i);
                chest.tick();
            }
        }

        tick++;
    }

    public static void startTicking() {
        Scheduler.get().runTimer(task -> {
            tick();
        }, 1, 1);
    }

    public static long getTick() {
        return tick;
    }
}
