package dev.chicoferreira.lifestealer.configuration;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

/**
 * Represents an enchantment with a level attached to it used for serialization purposes
 *
 * @param enchantment The enchantment
 * @param level       The level of the enchantment
 */
public record LeveledEnchantment(Enchantment enchantment, int level) {

    public static class Serializer implements TypeDeserializer<LeveledEnchantment> {

        @Override
        public LeveledEnchantment deserialize(Type type, ConfigurationNode node) throws SerializationException {
            NamespacedKey enchantmentKey = require(node.node("name"), NamespacedKey.class);

            Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.ENCHANTMENT);
            Enchantment enchantment = enchantmentRegistry.get(enchantmentKey);

            if (enchantment == null) {
                throw new SerializationException("Invalid enchantment: " + enchantmentKey);
            }

            int level = node.node("level").getInt(1);

            return new LeveledEnchantment(enchantment, level);
        }
    }

}
