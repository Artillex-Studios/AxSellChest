package com.artillexstudios.axsellchest.menu.prices;

import org.bukkit.entity.Player;

public abstract class Price {
    private final String id;

    public Price(String id) {
        this.id = id;
    }

    public abstract boolean pay(Player player, String argument, boolean take);

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price price)) return false;

        return getId().equals(price.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
