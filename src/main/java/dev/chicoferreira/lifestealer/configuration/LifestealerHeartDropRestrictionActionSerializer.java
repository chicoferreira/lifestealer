package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropAction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestrictionAction;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class LifestealerHeartDropRestrictionActionSerializer implements TypeDeserializer<LifestealerHeartDropRestrictionAction> {

    @Override
    public LifestealerHeartDropRestrictionAction deserialize(Type type, ConfigurationNode node) throws SerializationException {
        LifestealerHeartDropRestriction heartDropRestriction = require(node, LifestealerHeartDropRestriction.class);
        LifestealerHeartDropAction action = require(node.node("action"), LifestealerHeartDropAction.class);

        return new LifestealerHeartDropRestrictionAction(heartDropRestriction, action);
    }
}
