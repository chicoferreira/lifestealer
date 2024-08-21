package dev.chicoferreira.lifestealer;

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationUtils {

    /**
     * Creates a @{link {@link net.kyori.adventure.text.minimessage.MiniMessage}} tag resolver that formats a duration using the given format
     * using {@link DurationFormatUtils#formatDuration(long, String)}.
     *
     * @param key      The key of the tag
     * @param duration The duration to format
     * @return A tag resolver that formats the duration using the given format
     */
    public static TagResolver formatDuration(@TagPattern final @NotNull String key, final @NotNull Duration duration) {
        return TagResolver.resolver(key, (argumentQueue, context) -> {
            final String format = argumentQueue.popOr("Format expected").value();
            // Make sure the duration is at least 1 second because the formatter just doesn't output anything if it's 0 and all durations are optional
            long millis = Math.max(duration.toMillis(), 1000);
            return Tag.inserting(context.deserialize(DurationFormatUtils.formatDuration(millis, format)));
        });
    }

    /**
     * Parses a string into a {@link Duration}. The string should be formatted as a sequence of numbers followed by a unit.
     * Supported units: s or S (seconds), m (minutes), h or H (hours), d or D (days), w or W (weeks), M (months), y or Y (years)
     * <p>
     * Example: "1d 2h 3m 4s" returns a duration with 1 day, 2 hours, 3 minutes and 4 seconds
     *
     * @param string The string to parse
     * @return The parsed duration
     * @throws IllegalArgumentException If the string is not in a valid format
     */
    public static Duration parse(String string) throws IllegalArgumentException {
        Duration duration = Duration.ZERO;
        for (String durationParts : string.split(" ")) {
            String decimalPartString;
            char durationType;

            try {
                decimalPartString = durationParts.substring(0, durationParts.length() - 1);
                durationType = durationParts.charAt(durationParts.length() - 1);
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid duration format: '" + string + "'");
            }

            long decimalPart;
            try {
                decimalPart = Long.parseLong(decimalPartString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Couldn't parse number '" + decimalPartString + "' in duration: '" + string + "'");
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
