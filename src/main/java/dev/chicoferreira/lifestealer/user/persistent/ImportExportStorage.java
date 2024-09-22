package dev.chicoferreira.lifestealer.user.persistent;

import com.google.gson.Gson;
import dev.chicoferreira.lifestealer.user.LifestealerUser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is responsible for importing and exporting user data to and from files.
 */
public class ImportExportStorage {

    private final Path basePath;
    private final UserPersistentStorage userPersistentStorage;

    private final Gson gson = new Gson();

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
        Path readPath = basePath.resolve(path);
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
        Path writePath = basePath.resolve(path);

        List<LifestealerUser> lifestealerUsers = userPersistentStorage.loadAllUsers();
        String json = gson.toJson(lifestealerUsers);

        Files.writeString(writePath, json);

        return lifestealerUsers.size();
    }

    /**
     * Returns a list of all files in the plugin's folder that end with ".json".
     *
     * @return A list of all files in the plugin's folder that end with ".json"
     */
    public List<Path> listFiles() {
        try (Stream<Path> stream = Files.list(basePath)) {
            return stream.filter(path -> path.toString().endsWith(".json")).toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
