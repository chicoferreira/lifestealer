package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.heart.LifestealerUserRules;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class LifestealerUserRulesSerializer implements TypeDeserializer<LifestealerUserRules> {
    @Override
    public LifestealerUserRules deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Integer maxHearts = require(node.node("max hearts"), Integer.class);
        Integer minHearts = require(node.node("min hearts"), Integer.class);
        Duration banTime = require(node.node("ban time"), Duration.class);
        Integer returnHearts = require(node.node("return hearts"), Integer.class);

        return new LifestealerUserRules(maxHearts, minHearts, banTime, returnHearts);
    }
}
