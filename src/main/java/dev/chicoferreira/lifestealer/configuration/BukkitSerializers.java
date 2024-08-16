package dev.chicoferreira.lifestealer.configuration;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.intellij.lang.annotations.Subst;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

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

    public static class Sounds implements TypeDeserializer<Sound> {

        @Override
        public Sound deserialize(Type type, ConfigurationNode node) throws SerializationException {
            @Subst("empty") String simpleSoundString = node.getString();
            if (simpleSoundString != null) {
                return Sound.sound(Key.key(simpleSoundString), Sound.Source.PLAYER, 1, 1);
            }

            if (node.isMap()) {
                @Subst("empty") String soundString = require(node.node("key"), String.class);
                String sourceString = node.node("source").getString();

                Sound.Source source = Sound.Source.PLAYER;
                if (sourceString != null) {
                    source = Sound.Source.NAMES.value(sourceString);
                    if (source == null) {
                        throw new SerializationException("Sound source '" + sourceString + "' is invalid");
                    }
                }

                float volume = (float) node.node("volume").getDouble(1);
                float pitch = (float) node.node("pitch").getDouble(1);

                return Sound.sound(Key.key(soundString), source, volume, pitch);
            }
            return null;
        }
    }

    public static class TitleTimes implements TypeDeserializer<Title.Times> {
        @Override
        public Title.Times deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (node.virtual()) {
                return null;
            }
            int fadeIn = require(node.node("fade in"), Integer.class);
            int stay = require(node.node("stay"), Integer.class);
            int fadeOut = require(node.node("fade out"), Integer.class);

            return Title.Times.times(fromTicks(fadeIn), fromTicks(stay), fromTicks(fadeOut));
        }

        private Duration fromTicks(int ticks) {
            return Duration.ofMillis(ticks * 50L);
        }
    }
}
