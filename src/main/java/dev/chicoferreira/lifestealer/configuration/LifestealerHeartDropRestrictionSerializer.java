package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.restrictions.DamageCauseHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.restrictions.SameIpReasonHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.restrictions.WorldSpecificHeartDropRestriction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class LifestealerHeartDropRestrictionSerializer implements TypeDeserializer<LifestealerHeartDropRestriction> {

    @Override
    public LifestealerHeartDropRestriction deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        String restrictionType = node.node("type").require(String.class);

        if (restrictionType.equalsIgnoreCase("death cause")) {
            return node.get(DamageCauseHeartDropRestriction.class);
        } else if (restrictionType.equalsIgnoreCase("same ip")) {
            return node.get(SameIpReasonHeartDropRestriction.class);
        } else if (restrictionType.equalsIgnoreCase("world")) {
            return node.get(WorldSpecificHeartDropRestriction.class);
        }

        throw new SerializationException("Unknown heart drop restriction type: " + restrictionType);
    }
}
