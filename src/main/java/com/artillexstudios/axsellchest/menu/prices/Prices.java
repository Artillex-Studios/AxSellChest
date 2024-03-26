package com.artillexstudios.axsellchest.menu.prices;

import com.artillexstudios.axsellchest.menu.prices.impl.EconomyPrice;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Prices {
    private static final HashMap<String, Price> PRICES = new HashMap<>();

    private static final Price ECONOMY_PRICE = register(new EconomyPrice());

    public static Price register(Price price) {
        PRICES.put(price.getId(), price);
        return price;
    }

    public static boolean pay(Player player, List<String> prices) {
        for (String rawPrice : prices) {
            String id = StringUtils.substringBetween(rawPrice, "[", "]").toLowerCase(Locale.ENGLISH);
            String arguments = StringUtils.substringAfter(rawPrice, "] ");

            Price price = PRICES.get(id);
            if (price == null) continue;

            if (!price.pay(player, arguments, false)) {
                return false;
            }
        }

        for (String rawPrice : prices) {
            String id = StringUtils.substringBetween(rawPrice, "[", "]").toLowerCase(Locale.ENGLISH);
            String arguments = StringUtils.substringAfter(rawPrice, "] ").toLowerCase(Locale.ENGLISH);

            Price price = PRICES.get(id);
            if (price == null) continue;

            price.pay(player, arguments, true);
        }

        return true;
    }
}
