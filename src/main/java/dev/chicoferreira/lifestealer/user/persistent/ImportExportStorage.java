package dev.chicoferreira.lifestealer.user.persistent;

import com.google.gson.*;
import dev.chicoferreira.lifestealer.user.LifestealerUser;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is responsible for importing and exporting user data to and from files.
 */
public class ImportExportStorage {

    private final Path basePath;
    private final UserPersistentStorage userPersistentStorage;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public ImportExportStorage(Path basePath, UserPersistentStorage userPersistentStorage) {
        this.basePath = basePath;
        this.userPersistentStorage = userPersistentStorage;
    }

    /**
     * Imports every user from a file.
     * This file must be a JSON file containing an array of {@link LifestealerUser}.
     * <p>
     * The format of the file format should follow the default serialization format of {@link LifestealerUser} in GSON.
     *
     * @param path The path to the file to import from
     * @return The number of users imported
     * @throws Exception If an error occurs while importing
     */
    public int importFromFile(String path) throws Exception {
        Path readPath = basePath.resolve(path).normalize();

        if (!readPath.startsWith(basePath)) {
            throw new IllegalArgumentException("Cannot read outside of the base directory");
        }

        String json = Files.readString(readPath);

        LifestealerUser[] lifestealerUsers = gson.fromJson(json, LifestealerUser[].class);
        userPersistentStorage.saveAllUsers(List.of(lifestealerUsers));

        return lifestealerUsers.length;
    }

    /**
     * Exports every user to a file.
     *
     * @param path The path to the file to export to
     * @return The number of users exported
     * @throws Exception If an error occurs while exporting
     */
    public int exportToFile(String path) throws Exception {
        Path writePath = basePath.resolve(path).normalize();

        if (!writePath.startsWith(basePath)) {
            throw new IllegalArgumentException("Cannot write outside of the base directory");
        }

        List<LifestealerUser> lifestealerUsers = userPersistentStorage.loadAllUsers();
        String json = gson.toJson(lifestealerUsers);

        Files.createDirectories(writePath.getParent());
        Files.writeString(writePath, json);

        return lifestealerUsers.size();
    }

    public List<Path> listFiles() {
        try (Stream<Path> stream = Files.walk(basePath)) {
            return stream.filter(path -> path.toFile().isFile()).map(basePath::relativize).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        public record SimpleInstant(long epochSecond, long nano) {
        }

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SimpleInstant instant = context.deserialize(json, SimpleInstant.class);
            return Instant.ofEpochSecond(instant.epochSecond(), instant.nano());
        }

        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(new SimpleInstant(src.getEpochSecond(), src.getNano()));
        }
    }

    public static class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        public record SimpleDuration(long seconds, int nano) {
        }

        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SimpleDuration duration = context.deserialize(json, SimpleDuration.class);
            return Duration.ofSeconds(duration.seconds(), duration.nano());
        }

        @Override
        public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(new SimpleDuration(src.getSeconds(), src.getNano()));
        }
    }
}
