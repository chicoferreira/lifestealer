package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.PlayerNotification;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropAction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestrictionAction;
import dev.chicoferreira.lifestealer.restriction.restrictions.DamageCauseHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.restrictions.SameIpReasonHeartDropRestriction;
import dev.chicoferreira.lifestealer.restriction.restrictions.WorldSpecificHeartDropRestriction;
import dev.chicoferreira.lifestealer.user.LifestealerUserController.BanSettings;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesGroup;
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
            List<LifestealerUserRulesGroup> userGroupRules,
            List<LifestealerHeartItem> heartItems,
            List<LifestealerHeartDropRestrictionAction> heartDropRestrictionActions,
            String itemToDropWhenPlayerDies,
            BanSettings banSettings
    ) {
    }

    public Values loadConfig() throws SerializationException {
        return new Values(
                getStartingHearts(),
                getDefaultUserRules(),
                getUserGroupRules(),
                getHeartItems(),
                getHeartDropRestrictionActions(),
                getItemToDropWhenPlayerDies(),
                getBanSettings()
        );
    }

    private List<LifestealerHeartDropRestrictionAction> getHeartDropRestrictionActions() throws SerializationException {
        return getConfig().node("heart drop restrictions").getList(LifestealerHeartDropRestrictionAction.class);
    }

    private BanSettings getBanSettings() throws SerializationException {
        return require(getConfig().node("ban settings"), BanSettings.class);
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

    private List<LifestealerUserRulesGroup> getUserGroupRules() throws SerializationException {
        return getConfig().node("rules").node("groups").getList(LifestealerUserRulesGroup.class);
    }

    public PlayerNotification getPlayerNotification(String messagePath) throws SerializationException {
        return require(getConfig().node("messages").node(messagePath), PlayerNotification.class);
    }

    private String getItemToDropWhenPlayerDies() throws SerializationException {
        return require(getConfig().node("item to drop when player dies"), String.class);
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
                                .register(Sound.class, new BukkitSerializers.Sounds())
                                .register(Title.Times.class, new BukkitSerializers.TitleTimes())
                                .register(LeveledEnchantment.class, new LeveledEnchantment.Serializer())
                                .register(ItemStack.class, new ItemStackSerializer())
                                .register(LifestealerHeartItem.class, new LifestealerHeartItemSerializer())
                                .register(LifestealerUserRules.class, new LifestealerUserRulesSerializer())
                                .register(LifestealerUserRulesGroup.class, new LifestealerUserRulesGroupSerializer())
                                .register(PlayerNotification.class, new PlayerNotificationSerializer())
                                .register(BanSettings.class, new BanSettingsSerializer())
                                .register(EntityDamageEvent.DamageCause.class, new EnumSerializer<>(EntityDamageEvent.DamageCause.class))
                                .register(LifestealerHeartDropAction.class, new EnumSerializer<>(LifestealerHeartDropAction.class))
                                .register(LifestealerHeartDropRestrictionAction.class, new LifestealerHeartDropRestrictionActionSerializer())
                                .register(SameIpReasonHeartDropRestriction.class, new SameIpReasonHeartDropRestrictionSerializer())
                                .register(DamageCauseHeartDropRestriction.class, new DamageCauseHeartDropRestrictionSerializer())
                                .register(WorldSpecificHeartDropRestriction.class, new WorldSpecificHeartDropRestrictionSerializer())
                                .register(LifestealerHeartDropRestriction.class, new LifestealerHeartDropRestrictionSerializer())
                ))
                .path(this.configFilePath)
                .build();
    }

}
