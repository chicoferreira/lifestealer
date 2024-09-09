package dev.chicoferreira.lifestealer.user.persistent.sql;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;

@ConfigSerializable
public record SQLHikariConnectionProperties(@Required @NotNull String address,
                                            @Required @NotNull String database,
                                            @NotNull String username,
                                            String password,
                                            @NotNull Map<String, String> extraProperties) {
}
