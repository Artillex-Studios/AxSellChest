package com.artillexstudios.axsellchest.menu.actions;

import com.artillexstudios.axsellchest.chests.Chest;
import org.bukkit.entity.Player;

public abstract class Action {
    private final String id;

    public Action(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract void run(Player player, Chest chest, String arguments);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action action)) return false;

        return getId().equals(action.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
