package dev.chicoferreira.lifestealer.item;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a lifestealer heart item.
 * <p>
 * The {@link ItemStack} saved here does not contain the NBT data for the amount of hearts.
 * Use the {@link LifestealerHeartItemManager#generateItem(String)} method to generate the item stack with the correct properties.
 *
 * @param typeName      The name of the item
 * @param amount        The amount of hearts the item gives
 * @param baseItemStack The base item stack model of the item
 */
public record LifestealerHeartItem(String typeName, int amount, ItemStack baseItemStack) {
}
