package dev.chicoferreira.lifestealer.configuration;

import com.destroystokyo.paper.ParticleBuilder;
import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.PlayerNotification;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropAction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestrictionAction;
import dev.chicoferreira.lifestealer.user.LifestealerUserController.BanSettings;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageProperties;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageType;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesGroup;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

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
        if (this.configLoadedNode == null) {
            reloadConfig();
        }
        return this.configLoadedNode;
    }

    public void reloadConfig() {
        try {
            this.configLoadedNode = getConfigLoader().load();
        } catch (ConfigurateException e) {
            main.getLogger().severe("Error reloading configuration file: " + e.getMessage());
        }
    }

    /**
     * Holds every configuratable data
     */
    public record Values(
            Integer startingHearts,
            LifestealerUserRules defaultUserRules,
            List<LifestealerUserRulesGroup> userGroupRules,
            LifestealerHeartItemManager.Settings heartItemSettings,
            List<LifestealerHeartDropRestrictionAction> heartDropRestrictionActions,
            BanSettings banSettings,
            UserPersistentStorageProperties storageProperties,
            Component errorKickMessage,
            Map<String, DurationUtils.DurationFormatSettings> durationFormats
    ) {
    }

    public Values loadConfig() throws SerializationException {
        return new Values(
                getStartingHearts(),
                getDefaultUserRules(),
                getUserGroupRules(),
                getHeartItemSettings(),
                getHeartDropRestrictionActions(),
                getBanSettings(),
                getStorageProperties(),
                getErrorKickMessage(),
                getDurationFormatsSettings()
        );
    }

    private Component getErrorKickMessage() throws SerializationException {
        return getConfig().node("storage").node("error kick message").require(Component.class);
    }

    private List<LifestealerHeartDropRestrictionAction> getHeartDropRestrictionActions() throws SerializationException {
        return getConfig().node("heart drop restrictions").getList(LifestealerHeartDropRestrictionAction.class);
    }

    private BanSettings getBanSettings() throws SerializationException {
        return require(getConfig().node("ban settings"), BanSettings.class);
    }

    private LifestealerHeartItemManager.Settings getHeartItemSettings() throws SerializationException {
        return new LifestealerHeartItemManager.Settings(
                getConfig().node("items").getList(LifestealerHeartItem.class),
                require(getConfig().node("item to drop when player dies"), String.class)
        );
    }

    private int getStartingHearts() throws SerializationException {
        return require(getConfig().node("starting hearts"), Integer.class);
    }

    private LifestealerUserRules getDefaultUserRules() throws SerializationException {
        return require(getConfig().node("rules").node("default"), LifestealerUserRules.class);
    }

    private List<LifestealerUserRulesGroup> getUserGroupRules() throws SerializationException {
        return getConfig().node("rules").node("groups").getList(LifestealerUserRulesGroup.class);
    }

    public PlayerNotification getPlayerNotification(String messagePath) throws SerializationException {
        return require(getConfig().node("messages").node(messagePath), PlayerNotification.class);
    }

    private UserPersistentStorageProperties getStorageProperties() throws SerializationException {
        return require(getConfig().node("storage"), UserPersistentStorageProperties.class);
    }

    private Map<String, DurationUtils.DurationFormatSettings> getDurationFormatsSettings() throws SerializationException {
        return getConfig().node("duration format").get(new TypeToken<>() {
        });
    }

    private YamlConfigurationLoader createLoader() {
        if (!this.configFilePath.toFile().exists()) {
            this.main.saveResource(this.configFileName, false);
        }

        final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
                .defaultNamingScheme((input) -> NamingSchemes.LOWER_CASE_DASHED.coerce(input).replace("-", " "))
                .build();

        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build
                        .registerAnnotatedObjects(customFactory)
                        .register(Duration.class, new DurationSerializer())
                        .register(Component.class, new BukkitSerializers.MiniMessageComponents())
                        .register(NamespacedKey.class, new BukkitSerializers.NamespacedKeys())
                        .register(ItemFlag.class, new BukkitSerializers.ItemFlags())
                        .register(Sound.class, new BukkitSerializers.Sounds())
                        .register(Title.Times.class, new BukkitSerializers.TitleTimes())
                        .register(LeveledEnchantment.class, new LeveledEnchantment.Serializer())
                        .register(ItemStack.class, new ItemStackSerializer())
                        .register(PlayerNotification.class, new PlayerNotificationSerializer())
                        .register(EntityDamageEvent.DamageCause.class, new EnumSerializer<>(EntityDamageEvent.DamageCause.class))
                        .register(LifestealerHeartDropAction.class, new EnumSerializer<>(LifestealerHeartDropAction.class))
                        .register(LifestealerHeartDropRestrictionAction.class, new LifestealerHeartDropRestrictionActionSerializer())
                        .register(LifestealerHeartDropRestriction.class, new LifestealerHeartDropRestrictionSerializer())
                        .register(UserPersistentStorageProperties.class, new StoragePropertiesSerializer(main.getDataFolder().toPath()))
                        .register(UserPersistentStorageType.class, new EnumSerializer<>(UserPersistentStorageType.class))
                        .register(ParticleBuilder.class, new BukkitSerializers.Particles())
                ))
                .path(this.configFilePath)
                .build();
    }

}
