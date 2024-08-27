package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.restriction.restrictions.DamageCauseHeartDropRestriction;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class DamageCauseHeartDropRestrictionSerializer implements TypeDeserializer<DamageCauseHeartDropRestriction> {

    @Override
    public DamageCauseHeartDropRestriction deserialize(Type type, ConfigurationNode node) throws SerializationException {
        EntityDamageEvent.DamageCause damageCause = require(node.node("cause"), EntityDamageEvent.DamageCause.class);
        return new DamageCauseHeartDropRestriction(damageCause);
    }
}
