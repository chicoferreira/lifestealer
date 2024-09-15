package dev.chicoferreira.lifestealer.configuration;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class ItemStackSerializer implements TypeDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        String itemMaterialString = require(node.node("type"), String.class);

        Material material = Material.matchMaterial(itemMaterialString);
        if (material == null) {
            throw new SerializationException("ItemStack type " + itemMaterialString + " is invalid");
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        Component displayName = node.node("display name").get(Component.class);
        if (displayName != null) {
            itemMeta.displayName(displayName);
        }

        List<Component> loreComponents = node.node("lore").getList(Component.class);
        if (loreComponents != null) {
            itemMeta.lore(loreComponents);
        }

        boolean glint = node.node("glint").getBoolean(false);
        if (glint) {
            itemMeta.setEnchantmentGlintOverride(true);
        }

        List<LeveledEnchantment> enchantments = node.node("enchantments").getList(LeveledEnchantment.class);
        if (enchantments != null) {
            for (LeveledEnchantment enchantment : enchantments) {
                itemMeta.addEnchant(enchantment.enchantment(), enchantment.level(), true);
            }
        }

        List<ItemFlag> itemFlags = node.node("flags").getList(ItemFlag.class);
        if (itemFlags != null) {
            for (ItemFlag itemFlag : itemFlags) {
                itemMeta.addItemFlags(itemFlag);
            }
        }

        int customModelData = node.node("custom model data").getInt(-1);
        if (customModelData != -1) {
            itemMeta.setCustomModelData(customModelData);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
