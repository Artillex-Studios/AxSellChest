package com.artillexstudios.axsellchest.integrations.prices.impl;

import com.artillexstudios.axsellchest.integrations.prices.PricesIntegration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AxGensIntegration implements PricesIntegration {
    private Method getPriceMethod;
    private Object shopPrices;

    @Override
    public double getPrice(ItemStack itemStack, int amount) {
        try {
            return ((Double) getPriceMethod.invoke(shopPrices, itemStack)) / itemStack.getAmount() * amount;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        try {
            Class<?> clazz = Class.forName("com.artillexstudios.axgens.api.AxGensAPI");
            shopPrices = clazz.getDeclaredMethod("getShopPrices").invoke(null);
            getPriceMethod = shopPrices.getClass().getDeclaredMethod("getPrice", ItemStack.class);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
