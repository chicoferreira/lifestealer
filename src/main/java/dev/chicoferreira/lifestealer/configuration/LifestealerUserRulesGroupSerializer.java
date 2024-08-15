package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.heart.LifestealerUserRulesGroup;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class LifestealerUserRulesGroupSerializer implements TypeDeserializer<LifestealerUserRulesGroup> {
    @Override
    public LifestealerUserRulesGroup deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String permission = require(node.node("permission"), String.class);
        Optional<Integer> maxHearts = Optional.ofNullable(node.node("max hearts").get(Integer.class));
        Optional<Integer> minHearts = Optional.ofNullable(node.node("min hearts").get(Integer.class));
        Optional<Duration> banTime = Optional.ofNullable(node.node("ban time").get(Duration.class));

        return new LifestealerUserRulesGroup(permission, maxHearts, minHearts, banTime);
    }
}
