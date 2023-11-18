package com.artillexstudios.axsellchest.utils;

import com.artillexstudios.axapi.utils.Version;
import com.artillexstudios.axsellchest.AxSellChestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSUtils {
    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(NMSUtils.class);
    private static Method getInventoryMethod;
    private static Method isEmptyMethod;
    private static FieldAccessor accessor;

    static {
        try {
            Method getInventory = Class.forName(getCraftbukkitClass("inventory.CraftInventory")).getDeclaredMethod("getInventory");
            getInventory.setAccessible(true);
            getInventoryMethod = getInventory;

            Field contentsField;
            Class<?> tileEntityClass = Class.forName("net.minecraft.world.level.block.entity.TileEntityChest");
            if (Version.getServerVersion().ordinal() < Version.v1_19_3.ordinal()) {
                contentsField = tileEntityClass.getDeclaredField("f");
            } else {
                contentsField = tileEntityClass.getDeclaredField("c");
            }

            contentsField.setAccessible(true);
            accessor = new FieldAccessor(contentsField);

            Method isEmpty = Class.forName("net.minecraft.world.item.ItemStack").getDeclaredMethod("b");
            isEmpty.setAccessible(true);
            isEmptyMethod = isEmpty;
        } catch (Exception exception) {
            LOGGER.error("An error occurred while enabling nms support! Disabling...", exception);
            Bukkit.getPluginManager().disablePlugin(AxSellChestPlugin.getInstance());
        }
    }

    public static String getCraftbukkitClass(String clazz) {
        return CRAFTBUKKIT_PACKAGE + "." + clazz;
    }

    public static boolean isEmpty(Inventory inventory) {
        try {
            return isEmpty0(inventory);
        } catch (Throwable throwable) {
            LOGGER.error("An unexpected error occurred while checking if inventory is empty!", throwable);
            return false;
        }
    }

    private static boolean isEmpty0(Inventory inventory) throws Throwable {
        Object container = getInventoryMethod.invoke(inventory);

        java.util.List<Object> contents = (java.util.List<Object>) accessor.get(container);
        int contentSize = contents.size();

        for (int i = 0; i < contentSize; i++) {
            Object content = contents.get(i);

            boolean empty = (boolean) isEmptyMethod.invoke(content);
            if (!empty) {
                return false;
            }
        }

        return true;
    }

    static class FieldAccessor {
        private static sun.misc.Unsafe unsafe;

        static {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe) f.get(null);
            } catch (Exception exception) {
                LOGGER.error("An issue occurred while creatinf new fieldAccessor!", exception);
            }
        }

        private final Field field;
        private final long fieldOffset;

        public FieldAccessor(Field field) {
            this.field = field;
            fieldOffset = unsafe.objectFieldOffset(field);
        }

        public Object get(Object object) {
            return unsafe.getObject(object, fieldOffset);
        }
    }
}
