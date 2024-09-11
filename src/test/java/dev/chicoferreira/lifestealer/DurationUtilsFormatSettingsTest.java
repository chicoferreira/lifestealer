package dev.chicoferreira.lifestealer;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static dev.chicoferreira.lifestealer.DurationUtils.DurationFormatSettings;
import static dev.chicoferreira.lifestealer.DurationUtils.DurationFormatSettings.TimeUnitTranslation;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationUtilsFormatSettingsTest {

    private static final Map<TimeUnit, TimeUnitTranslation> EXTENDED_TRANSLATIONS = Map.of(
            TimeUnit.DAYS, new TimeUnitTranslation(" day", " days"),
            TimeUnit.HOURS, new TimeUnitTranslation(" hour", " hours"),
            TimeUnit.MINUTES, new TimeUnitTranslation(" minute", " minutes"),
            TimeUnit.SECONDS, new TimeUnitTranslation(" second", " seconds")
    );

    private static final Map<TimeUnit, TimeUnitTranslation> SHORT_TRANSLATIONS = Map.of(
            TimeUnit.DAYS, new TimeUnitTranslation("d", "d"),
            TimeUnit.HOURS, new TimeUnitTranslation("h", "h"),
            TimeUnit.MINUTES, new TimeUnitTranslation("m", "m"),
            TimeUnit.SECONDS, new TimeUnitTranslation("s", "s")
    );

    @Test
    void testFormatSettings2UnitsToShow() {
        DurationFormatSettings settings = new DurationFormatSettings(EXTENDED_TRANSLATIONS, ", ", " and ", 2);

        assertEquals("1 minute and 0 seconds", settings.format(Duration.ofSeconds(60)));
        assertEquals("1 day and 1 hour", settings.format(Duration.ofDays(1).plusHours(1).plusMinutes(1)));
        assertEquals("2 days and 3 hours", settings.format(Duration.ofDays(2).plusHours(3)));
        assertEquals("30 seconds", settings.format(Duration.ofSeconds(30)));
    }

    @Test
    void testFormatSettings3UnitsToShow() {
        DurationFormatSettings settings = new DurationFormatSettings(EXTENDED_TRANSLATIONS, ", ", " and ", 3);

        assertEquals("1 minute and 0 seconds", settings.format(Duration.ofSeconds(60)));
        assertEquals("1 day, 1 hour and 1 minute", settings.format(Duration.ofDays(1).plusHours(1).plusMinutes(1)));
        assertEquals("2 days, 3 hours and 0 minutes", settings.format(Duration.ofDays(2).plusHours(3)));
    }

    @Test
    void testFormatSettings1UnitToShow() {
        DurationFormatSettings settings = new DurationFormatSettings(EXTENDED_TRANSLATIONS, ", ", " and ", 1);

        assertEquals("1 minute", settings.format(Duration.ofSeconds(60)));
        assertEquals("1 day", settings.format(Duration.ofDays(1).plusHours(1).plusMinutes(1)));
        assertEquals("2 days", settings.format(Duration.ofDays(2).plusHours(3)));
    }

    @Test
    void testFormatSettingsAllUnitsToShow() {
        DurationFormatSettings settings = new DurationFormatSettings(EXTENDED_TRANSLATIONS, ", ", " and ", 0);

        assertEquals("1 day, 1 hour, 1 minute and 0 seconds", settings.format(Duration.ofDays(1).plusHours(1).plusMinutes(1)));
        assertEquals("2 days, 3 hours, 0 minutes and 0 seconds", settings.format(Duration.ofDays(2).plusHours(3)));
    }

    @Test
    void testFormatSettingsShortTranslations() {
        DurationFormatSettings settings = new DurationFormatSettings(SHORT_TRANSLATIONS, "", "", 3);

        assertEquals("1m0s", settings.format(Duration.ofSeconds(60)));
        assertEquals("1d1h1m", settings.format(Duration.ofDays(1).plusHours(1).plusMinutes(1)));
        assertEquals("2d3h0m", settings.format(Duration.ofDays(2).plusHours(3)));
        assertEquals("30s", settings.format(Duration.ofSeconds(30)));
    }
}