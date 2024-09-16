package dev.chicoferreira.lifestealer.user.persistent.sql.impl.file;

import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageProperties;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageType;

import java.nio.file.Path;

public record FileConnectionProperties(UserPersistentStorageType type,
                                       Path path) implements UserPersistentStorageProperties {
}
