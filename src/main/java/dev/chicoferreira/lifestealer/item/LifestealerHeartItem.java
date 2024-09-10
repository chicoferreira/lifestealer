package dev.chicoferreira.lifestealer.item;

import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * Represents a lifestealer heart item.
 * <p>
 * The {@link ItemStack} saved here does not contain the NBT data for the amount of hearts.
 * Use the {@link LifestealerHeartItemManager#generateItem(String)} method to generate the item stack with the correct properties.
 *
 * @param typeName      The name of the item
 * @param heartAmount   The amount of hearts the item gives
 * @param baseItemStack The base item stack model of the item
 */
@ConfigSerializable
public record LifestealerHeartItem(@Setting(value = "name") @Required String typeName,
                                   @Required int heartAmount,
                                   @Setting(value = "item") @Required ItemStack baseItemStack) {
}
