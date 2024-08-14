package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class LifestealerHeartItemSerializer implements TypeDeserializer<LifestealerHeartItem> {

    @Override
    public LifestealerHeartItem deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String name = node.node("name").getString();
        if (name == null) {
            throw new SerializationException("Heart type name is missing");
        }

        int heartAmount = node.node("heart amount").getInt(1);

        ItemStack itemStack = node.node("item").get(ItemStack.class);
        if (itemStack == null) {
            throw new SerializationException("Heart item is missing in " + name);
        }

        return new LifestealerHeartItem(name, heartAmount, itemStack);
    }
}
