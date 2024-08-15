package dev.chicoferreira.lifestealer.configuration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationSerializer implements TypeDeserializer<Duration> {
    @Override
    public Duration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String string = node.getString();
        if (string == null) {
            return null;
        }

        Duration duration = Duration.ZERO;
        for (String durationParts : string.split(" ")) {
            String decimalPartString;
            char durationType;

            try {
                decimalPartString = durationParts.substring(0, durationParts.length() - 1);
                durationType = durationParts.charAt(durationParts.length() - 1);
            } catch (IndexOutOfBoundsException e) {
                throw new SerializationException("Invalid duration format: '" + string + "'");
            }

            long decimalPart;
            try {
                decimalPart = Long.parseLong(decimalPartString);
            } catch (NumberFormatException e) {
                throw new SerializationException("Couldn't parse number '" + decimalPartString + "' in duration: '" + string + "'");
            }
            switch (durationType) {
                case 's' | 'S' -> duration = duration.plusSeconds(decimalPart);
                case 'm' -> duration = duration.plusMinutes(decimalPart);
                case 'h' | 'H' -> duration = duration.plusHours(decimalPart);
                case 'd' | 'D' -> duration = duration.plusDays(decimalPart);
                case 'w' | 'W' -> duration = duration.plus(decimalPart, ChronoUnit.WEEKS);
                case 'M' -> duration = duration.plus(decimalPart, ChronoUnit.MONTHS);
                case 'y' | 'Y' -> duration = duration.plus(decimalPart, ChronoUnit.YEARS);
            }
        }

        return duration;
    }
}