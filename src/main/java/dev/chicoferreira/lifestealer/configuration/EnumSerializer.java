package dev.chicoferreira.lifestealer.configuration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;

public class EnumSerializer<T extends Enum<T>> implements TypeDeserializer<T> {

    private final Class<T> enumClass;

    public EnumSerializer(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String valueString = node.getString();

        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().replace("_", " ").equalsIgnoreCase(valueString)) {
                return enumConstant;
            }
        }

        throw new SerializationException("Could not find enum constant " + enumClass.getSimpleName() + " for value: " + valueString);
    }
}
