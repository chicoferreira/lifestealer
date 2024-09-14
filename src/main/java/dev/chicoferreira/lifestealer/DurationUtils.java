package dev.chicoferreira.lifestealer;

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DurationUtils {

    private static Map<String, DurationFormatSettings> FORMATS;

    public static void setFormats(Map<String, DurationFormatSettings> formats) {
        FORMATS = formats;
    }

    /**
     * Creates a @{link {@link net.kyori.adventure.text.minimessage.MiniMessage}} tag resolver that formats a duration using the given format
     * using {@link DurationFormatUtils#formatDuration(long, String)}.
     *
     * @param key      The key of the tag
     * @param duration The duration to format
     * @return A tag resolver that formats the duration using the given format
     */
    public static TagResolver formatDurationTag(@TagPattern final @NotNull String key, final @NotNull Duration duration) {
        return TagResolver.resolver(key,
                (argumentQueue, context) -> {
                    String formatType = argumentQueue.popOr("Missing format type").value();
                    DurationFormatSettings format = FORMATS.get(formatType);
                    if (format == null) {
                        throw new IllegalArgumentException("Invalid format type: " + formatType);
                    }
                    return Tag.selfClosingInserting(context.deserialize(format.format(duration)));
                }
        );
    }

    @ConfigSerializable
    public record DurationFormatSettings(
            Map<TimeUnit, TimeUnitTranslation> translations,
            String separator,
            String lastSeparator,
            long amountOfUnitsToShow,
            boolean showZeroValues
    ) {

        @ConfigSerializable
        public record TimeUnitTranslation(String singular, String plural) {
            public String format(long amount) {
                return amount + (amount == 1 ? singular : plural);
            }
        }

        public String format(Duration duration) {
            long daysPart = duration.toDaysPart();
            long hoursPart = duration.toHoursPart();
            long minutesPart = duration.toMinutesPart();
            long secondsPart = duration.toSecondsPart();

            List<String> parts = new ArrayList<>();
            long amount = amountOfUnitsToShow;
            boolean foundFirst = false;

            if (daysPart > 0) {
                parts.add(translations.get(TimeUnit.DAYS).format(daysPart));
                foundFirst = true;
                amount--;
            }
            if ((amount != 0 && (hoursPart > 0 || foundFirst)) && (showZeroValues || hoursPart > 0)) {
                parts.add(translations.get(TimeUnit.HOURS).format(hoursPart));
                foundFirst = true;
                amount--;
            }
            if ((amount != 0 && (minutesPart > 0 || foundFirst)) && (showZeroValues || minutesPart > 0)) {
                parts.add(translations.get(TimeUnit.MINUTES).format(minutesPart));
                foundFirst = true;
                amount--;
            }
            if (((amount != 0 && (secondsPart > 0 || foundFirst)) && (showZeroValues || secondsPart > 0)) || !foundFirst) {
                parts.add(translations.get(TimeUnit.SECONDS).format(secondsPart));
            }

            return join(parts, separator, lastSeparator);
        }
    }

    private static <E> String join(Iterable<E> objects, String separator, String lastSeparator) {
        final StringBuilder builder = new StringBuilder();

        final Iterator<E> iterator = objects.iterator();
        while (iterator.hasNext()) {
            final E next = iterator.next();
            if (!builder.isEmpty()) {
                builder.append(iterator.hasNext() ? separator : lastSeparator);
            }
            builder.append(next);
        }

        return builder.toString();
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
