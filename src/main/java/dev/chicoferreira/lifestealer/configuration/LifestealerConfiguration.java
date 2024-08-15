package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.heart.LifestealerUserRules;
import dev.chicoferreira.lifestealer.heart.LifestealerUserRulesGroup;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class LifestealerConfiguration {

    private final Path configFilePath;
    private final String configFileName;
    private final Plugin main;

    private YamlConfigurationLoader configLoader;
    private CommentedConfigurationNode configLoadedNode;

    public LifestealerConfiguration(Plugin main, String configFileName) {
        this.main = main;
        this.configFileName = configFileName;
        this.configFilePath = main.getDataFolder().toPath().resolve(configFileName);
    }

    private YamlConfigurationLoader getConfigLoader() {
        if (this.configLoader == null) {
            this.configLoader = createLoader();
        }
        return this.configLoader;
    }

    public CommentedConfigurationNode getConfig() {
        try {
            if (this.configLoadedNode == null) {
                this.configLoadedNode = getConfigLoader().load();
            }
            return this.configLoadedNode;
        } catch (ConfigurateException e) {
            main.getLogger().severe("Error loading configuration file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Holds every configuratable data
     */
    public record Values(
            int startingHearts,
            LifestealerUserRules defaultUserRules,
            List<LifestealerHeartItem> heartItems
    ) {
    }

    public Values loadConfig() throws SerializationException {
        return new Values(
                getStartingHearts(),
                getDefaultUserRules(),
                getHeartItems()
        );
    }

    private List<LifestealerHeartItem> getHeartItems() throws SerializationException {
        return getConfig().node("items").getList(LifestealerHeartItem.class);
    }

    private int getStartingHearts() throws SerializationException {
        return require(getConfig().node("starting hearts"), Integer.class);
    }

    private LifestealerUserRules getDefaultUserRules() throws SerializationException {
        return require(getConfig().node("rules").node("default"), LifestealerUserRules.class);
    }

    private YamlConfigurationLoader createLoader() {
        if (!this.configFilePath.toFile().exists()) {
            this.main.saveResource(this.configFileName, false);
        }
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build ->
                        build
                                .register(Duration.class, new DurationSerializer())
                                .register(Component.class, new BukkitSerializers.MiniMessageComponents())
                                .register(NamespacedKey.class, new BukkitSerializers.NamespacedKeys())
                                .register(ItemFlag.class, new BukkitSerializers.ItemFlags())
                                .register(LeveledEnchantment.class, new LeveledEnchantment.Serializer())
                                .register(ItemStack.class, new ItemStackSerializer())
                                .register(LifestealerHeartItem.class, new LifestealerHeartItemSerializer())
                                .register(LifestealerUserRules.class, new LifestealerUserRulesSerializer())
                                .register(LifestealerUserRulesGroup.class, new LifestealerUserRulesGroupSerializer())
                ))
                .path(this.configFilePath)
                .build();
    }

}
