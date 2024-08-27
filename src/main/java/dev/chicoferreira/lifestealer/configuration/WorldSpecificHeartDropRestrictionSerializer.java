package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.restriction.restrictions.WorldSpecificHeartDropRestriction;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class WorldSpecificHeartDropRestrictionSerializer implements TypeDeserializer<WorldSpecificHeartDropRestriction> {

    @Override
    public WorldSpecificHeartDropRestriction deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new WorldSpecificHeartDropRestriction(require(node.node("world"), String.class));
    }
}
