package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class LifestealerHeartItemSerializer implements TypeDeserializer<LifestealerHeartItem> {


    @Override
    public LifestealerHeartItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String name = require(node.node("name"), String.class);
        int heartAmount = require(node.node("heart amount"), Integer.class);
        ItemStack itemStack = require(node.node("item"), ItemStack.class);

        return new LifestealerHeartItem(name, heartAmount, itemStack);
    }
}
