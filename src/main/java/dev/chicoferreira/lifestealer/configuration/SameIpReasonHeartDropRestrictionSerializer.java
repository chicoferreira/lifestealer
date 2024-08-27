package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.restriction.restrictions.SameIpReasonHeartDropRestriction;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class SameIpReasonHeartDropRestrictionSerializer implements TypeDeserializer<SameIpReasonHeartDropRestriction> {

    @Override
    public SameIpReasonHeartDropRestriction deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return new SameIpReasonHeartDropRestriction();
    }
}
