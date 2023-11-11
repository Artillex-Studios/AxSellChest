/*
* MIT License
*
* Copyright (c) 2023 Artillex-Studios
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
package com.artillexstudios.axsellchest.utils;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.utils.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final TagResolver[] resolvers;

    public ItemBuilder(@NotNull Section section) {
        this.itemStack = new ItemStack(getMaterial(section.getString("type", "stone")));
        resolvers = new TagResolver[]{TagResolver.resolver()};
        section.getOptionalString("texture").ifPresent(this::setTextureValue);
        section.getOptionalString("name").ifPresent(name -> this.setName(name, resolvers));
        section.getOptionalString("color").ifPresent(this::setColor);
        section.getOptionalInt("custom-model-data").ifPresent(this::setCustomModelData);
        section.getOptionalInt("amount").ifPresent(this::amount);
        section.getOptionalBoolean("glow").ifPresent(this::glow);
        section.getOptionalStringList("lore").ifPresent(lore -> this.setLore(lore, resolvers));
        section.getOptionalStringList("enchants").ifPresent(enchants -> addEnchants(createEnchantmentsMap(enchants)));
        section.getOptionalStringList("item-flags").ifPresent(flags -> applyItemFlags(getItemFlags(flags)));
    }

    public ItemBuilder(@NotNull Section section, TagResolver... resolvers) {
        this.itemStack = new ItemStack(getMaterial(section.getString("type", "stone")));
        this.resolvers = resolvers;
        section.getOptionalString("texture").ifPresent(this::setTextureValue);
        section.getOptionalString("name").ifPresent(name -> this.setName(name, resolvers));
        section.getOptionalString("color").ifPresent(this::setColor);
        section.getOptionalInt("custom-model-data").ifPresent(this::setCustomModelData);
        section.getOptionalInt("amount").ifPresent(this::amount);
        section.getOptionalBoolean("glow").ifPresent(this::glow);
        section.getOptionalStringList("lore").ifPresent(lore -> this.setLore(lore, resolvers));
        section.getOptionalStringList("enchants").ifPresent(enchants -> addEnchants(createEnchantmentsMap(enchants)));
        section.getOptionalStringList("item-flags").ifPresent(flags -> applyItemFlags(getItemFlags(flags)));
    }

    public ItemBuilder(Map<?, ?> map, TagResolver... resolvers) {
        this.itemStack = new ItemStack(getMaterial((String) map.get("type")));
        this.resolvers = resolvers;
        Optional.ofNullable(map.get("texture")).ifPresent(string -> this.setTextureValue((String) string));
        Optional.ofNullable(map.get("name")).ifPresent(name -> this.setName((String) name, resolvers));
        Optional.ofNullable(map.get("color")).ifPresent(color -> this.setColor((String) color));
        Optional.ofNullable(map.get("custom-model-data")).ifPresent(number -> this.setCustomModelData((int) number));
        Optional.ofNullable(map.get("amount")).ifPresent(amount -> itemStack.setAmount((int) amount));
        Optional.ofNullable(map.get("glow")).ifPresent(glow -> this.glow((boolean) glow));
        Optional.ofNullable(map.get("lore")).ifPresent(lore -> this.setLore((List<String>) lore, resolvers));
        Optional.ofNullable(map.get("enchants")).ifPresent(enchants -> addEnchants(createEnchantmentsMap((List<String>) enchants)));
        Optional.ofNullable(map.get("item-flags")).ifPresent(flags -> applyItemFlags(getItemFlags((List<String>) flags)));
    }

    private static Material getMaterial(String name) {
        Material material = Material.matchMaterial(name.toUpperCase(Locale.ENGLISH));
        return material != null ? material : Material.BEDROCK;
    }

    public ItemBuilder(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
        resolvers = new TagResolver[]{TagResolver.resolver()};
    }

    public ItemBuilder(@NotNull ItemStack item) {
        this.itemStack = item;
        resolvers = new TagResolver[]{TagResolver.resolver()};
    }

    public ItemBuilder setName(String name) {
        setName(name, TagResolver.resolver());
        return this;
    }

    public ItemBuilder setName(String name, Map<String, String> replacements) {
        AtomicReference<String> toFormat = new AtomicReference<>(name);
        replacements.forEach((pattern, replacement) -> toFormat.set(toFormat.get().replace(pattern, replacement)));

        setName(toFormat.get(), TagResolver.resolver());
        return this;
    }

    public ItemBuilder setName(String name, TagResolver... resolvers) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(StringUtils.formatToString(name, resolvers));
        itemStack.setItemMeta(meta);
        return this;
    }

    public <T, Z> ItemBuilder storePersistentData(NamespacedKey key, PersistentDataType<T,Z> type, Z value) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(key, type, value);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setColor(String colorString) {
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta meta) {
            String[] rgb = colorString.replace(" ", "").split(",");
            meta.setColor(Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));

            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            ItemMeta meta = this.itemStack.getItemMeta();
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setCustomModelData(Integer modelData) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setCustomModelData(modelData);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        setLore(lore, TagResolver.resolver());
        return this;
    }

    public ItemBuilder setLore(List<String> lore, Map<String, String> replacements) {
        List<String> newList = new ArrayList<>(replacements.size());
        for (String line : lore) {
            AtomicReference<String> toFormat = new AtomicReference<>(line);
            replacements.forEach((pattern, replacement) -> toFormat.set(toFormat.get().replace(pattern, replacement)));
            newList.add(toFormat.get());
        }

        setLore(newList, TagResolver.resolver());
        return this;
    }

    public ItemBuilder setLore(List<String> lore, TagResolver... resolvers) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(StringUtils.formatListToString(lore, resolvers));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setTextureValue(String texture) {
        NMSHandlers.getNmsHandler().setItemStackTexture(this.itemStack, texture);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchantments) {
        this.itemStack.addEnchantments(enchantments);
        return this;
    }

    @NotNull
    private static Map<Enchantment, Integer> createEnchantmentsMap(@NotNull List<String> enchantments) {
        final Map<Enchantment, Integer> enchantsMap = new HashMap<>(enchantments.size());

        for (String enchantment : enchantments) {
            String[] enchant = enchantment.split(":");
            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchant[0]));
            if (ench == null) continue;
            int level;
            try {
                level = Integer.parseInt(enchant[1]);
            } catch (Exception exception) {
                continue;
            }

            enchantsMap.put(ench, level);
        }

        return enchantsMap;
    }

    public ItemBuilder applyItemFlags(@NotNull List<ItemFlag> flags) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        for (ItemFlag flag : flags) {
            itemMeta.addItemFlags(flag);
        }

        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    @NotNull
    private static List<ItemFlag> getItemFlags(@NotNull List<String> flags) {
        final List<ItemFlag> flagList = new ArrayList<>(flags.size());
        for (String flag : flags) {
            ItemFlag itemFlag;
            try {
                itemFlag = ItemFlag.valueOf(flag.toUpperCase(Locale.ENGLISH));
            } catch (Exception exception) {
                continue;
            }

            flagList.add(itemFlag);
        }

        return flagList;
    }

    public ItemStack get() {
        return itemStack;
    }

    public ItemStack clonedGet() {
        return itemStack.clone();
    }
}