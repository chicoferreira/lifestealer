package dev.chicoferreira.lifestealer.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class BukkitSerializers {

    public static class NamespacedKeys implements TypeDeserializer<NamespacedKey> {
        @Override
        public NamespacedKey deserialize(Type type, ConfigurationNode node) throws SerializationException {
            String string = node.getString();
            if (string == null) {
                return null;
            }
            NamespacedKey namespacedKey = NamespacedKey.fromString(string);
            if (namespacedKey == null) {
                throw new SerializationException("NamespacedKey " + string + " not valid");
            }
            return namespacedKey;
        }
    }

    public static class MiniMessageComponents implements TypeDeserializer<Component> {
        @Override
        public Component deserialize(Type type, ConfigurationNode node) {
            String componentString = node.getString();
            Component component = MiniMessage.miniMessage().deserializeOrNull(componentString);
            if (component != null) {
                // Remove italic to avoid item lore and display name to be italic
                return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
            }
            return null;
        }
    }

    public static class ItemFlags implements TypeDeserializer<ItemFlag> {
        @Override
        public ItemFlag deserialize(Type type, ConfigurationNode node) throws SerializationException {
            String string = node.getString();
            if (string == null) {
                return null;
            }

            try {
                return ItemFlag.valueOf(string.replace(" ", "_").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new SerializationException("ItemFlag " + string + " is invalid");
            }
        }
    }
}
