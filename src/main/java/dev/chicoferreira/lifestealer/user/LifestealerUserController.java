package dev.chicoferreira.lifestealer.user;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.events.LifestealerPostUserBanEvent;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Handling of the logic of the Lifestealer plugin.
 */
public class LifestealerUserController {

    private final LifestealerUserManager userManager;
    private final LifestealerUserRulesController userRulesController;
    private @NotNull BanSettings banSettings;

    public LifestealerUserController(LifestealerUserManager userManager, LifestealerUserRulesController userRulesController, @NotNull BanSettings banSettings) {
        this.userManager = userManager;
        this.userRulesController = userRulesController;
        this.banSettings = banSettings;
    }

    /**
     * Sets new ban settings.
     * Used when the configuration is reloaded.
     * <p>
     * <b>Thread-safety:</b> This method is not thread-safe.
     *
     * @param banSettings the new ban settings
     */
    public void setBanSettings(@NotNull BanSettings banSettings) {
        this.banSettings = banSettings;
    }

    /**
     * A result of a change for hearts of a player.
     *
     * @param previousHearts the previous amount of hearts
     * @param newHearts      the new amount of hearts
     */
    public record ChangeHeartsResult(int previousHearts, int newHearts) {

        /**
         * Checks if the amount of hearts has changed (previousHearts != newHearts).
         *
         * @return if the amount of hearts has changed
         */
        public boolean hasChanged() {
            return previousHearts != newHearts;
        }

        /**
         * Gets the difference between the new and previous amount of hearts (newHearts - previousHearts).
         *
         * @return the difference between the new and previous amount of hearts
         */
        public int difference() {
            return newHearts - previousHearts;
        }
    }

    /**
     * Sets the amount of hearts a player health has. This also saves the new amount in the database asynchronously.
     * The amount of hearts is clamped between the minimum and maximum amount of hearts the player can have, based on
     * their {@link LifestealerUserRules}.
     * <p>
     * You need to call {@link #updatePlayerHearts(Player, LifestealerUser)} to update the player health bar.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     *
     * @param user   the user related to the player
     * @param hearts the amount hearts to set
     * @param rules  the precomputed rules of the user
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult setHearts(@NotNull LifestealerUser user, @NotNull LifestealerUserRules rules, int hearts) {
        hearts = Math.clamp(hearts, rules.minHearts(), rules.maxHearts());

        int currentHearts = user.getHearts();
        user.setHearts(hearts);

        this.userManager.saveUserAsync(user);

        return new ChangeHeartsResult(currentHearts, hearts);
    }

    /**
     * Checks if adding the additional hearts to the user will make them overflow over the maximum
     * amount of hearts they can have in their {@link LifestealerUserRules}.
     * <p>
     * <b>Thread-safety:</b> Requires a read lock on the user.
     *
     * @param user             the user to check
     * @param rules            the rules of the user
     * @param additionalHearts the amount of hearts to add
     * @return if the user is overflowing with the additional hearts
     */
    public boolean isOverflowing(@NotNull LifestealerUser user, @NotNull LifestealerUserRules rules, int additionalHearts) {
        return user.getHearts() + additionalHearts > rules.maxHearts() && user.getHearts() < rules.maxHearts();
    }

    /**
     * Gets the amount of hearts that will overflow if the additional hearts are added
     * to the user based on their {@link LifestealerUserRules}.
     * If the additional hearts won't overflow, it will return 0.
     * <p>
     * <b>Thread-safety:</b> Requires a read lock on the user.
     *
     * @param user             the user to check
     * @param rules            the rules of the user
     * @param additionalHearts the amount of hearts to add
     * @return the amount of hearts that will overflow if the additional hearts are added
     */
    public int getOverflowAmount(@NotNull LifestealerUser user, @NotNull LifestealerUserRules rules, int additionalHearts) {
        return Math.max(0, user.getHearts() + additionalHearts - rules.maxHearts());
    }

    /**
     * Updates the player health bar to match the amount of hearts the user has.
     * <p>
     * <b>Thread-safety:</b> Requires a read lock on the user and should be called on the player's region thread.
     *
     * @param player the player to update the hearts
     * @param user   the user related to the player
     */
    public void updatePlayerHearts(@NotNull Player player, @NotNull LifestealerUser user) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(user.getHearts() * 2);
        }
    }

    /**
     * Adds hearts to the player health bar and saves the new amount in the database asynchronously.
     * If the new amount exceeds the maximum amount of hearts the player can have, it will be clamped to the maximum value.
     * <p>
     * You need to call {@link #updatePlayerHearts(Player, LifestealerUser)} to update the player health bar.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     *
     * @param user   the user related to the player
     * @param hearts the amount of hearts to add
     * @param rules  the precomputed rules of the user
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult addHearts(@NotNull LifestealerUser user, @NotNull LifestealerUserRules rules, int hearts) {
        return setHearts(user, rules, user.getHearts() + hearts);
    }

    /**
     * Removes hearts from the player health bar and saves the new amount in the database asynchronously.
     * If the new amount is less than the minimum amount of hearts the player can have, it will be clamped the minimum value.
     * <p>
     * You need to call {@link #updatePlayerHearts(Player, LifestealerUser)} to update the player health bar.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     *
     * @param user   the user related to the player
     * @param hearts the amount of hearts to remove
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult removeHearts(@NotNull LifestealerUser user, LifestealerUserRules rules, int hearts) {
        return setHearts(user, rules, user.getHearts() - hearts);
    }

    /**
     * The settings for the ban feature.
     * <p>
     * If the external flag is enabled, the player won't be kicked, and he can still join the server even if he is banned.
     * The plugin still registers the ban internally and executes the ban commands.
     *
     * @param kickMessage the message in {@link MiniMessage} format to send to the player when they are kicked
     * @param joinMessage the message in {@link MiniMessage} format to send to the player when they join
     * @param commands    the commands to execute when the player is banned
     * @param external    if the player should be kicked and not let be joined when they are banned
     */
    @ConfigSerializable
    public record BanSettings(@Required String kickMessage,
                              @Required String joinMessage,
                              List<String> commands,
                              boolean external) {
    }

    /**
     * Gets the current ban settings.
     * <p>
     * <b>Thread-safety:</b> This method is not thread-safe.
     *
     * @return the current ban settings
     */
    public @NotNull BanSettings getBanSettings() {
        return banSettings;
    }

    /**
     * Bans a user for the default ban time depending on its {@link LifestealerUserRules}.
     * <p>
     * You need to call {@link #postBanOperations(Player, LifestealerUser, LifestealerUser.Ban)} to kick the player
     * and {@link #executeBanCommands(Player, Duration)} to execute the ban commands.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     *
     * @param player the player to ban
     * @param user   the user related to the player
     * @return the created ban information
     */
    public @NotNull LifestealerUser.Ban setUserBanned(@NotNull Player player, @NotNull LifestealerUser user) {
        LifestealerUserRules rules = this.computeUserRules(player, user);
        Duration banDuration = rules.banTime();

        return setUserBanned(user, banDuration);
    }

    /**
     * Bans a user for a specific duration.
     * <p>
     * You need to call {@link #postBanOperations(Player, LifestealerUser, LifestealerUser.Ban)} to kick the player
     * and {@link #executeBanCommands(Player, Duration)} to execute the ban commands.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     */
    public @NotNull LifestealerUser.Ban setUserBanned(@NotNull LifestealerUser user, @NotNull Duration banDuration) {
        LifestealerUser.Ban ban = new LifestealerUser.Ban(Instant.now(), banDuration);
        user.setBan(ban);
        userManager.saveUserAsync(user);
        return ban;
    }

    /**
     * Executes the ban commands and kicks the player.
     * You should call this method after calling {@link #setUserBanned(Player, LifestealerUser)}.
     * <p>
     * <b>Thread-safety:</b> Thread-safe but requires to run on the player's region thread.
     *
     * @param player the player to ban
     * @param user   the user related to the player
     * @param ban    the ban information of the user
     */
    public void postBanOperations(@NotNull Player player, @NotNull LifestealerUser user, @NotNull LifestealerUser.Ban ban) {
        Duration banDuration = ban.duration();

        if (!getBanSettings().external()) {
            if (player.isDead()) {
                player.spigot().respawn();
            }

            Component kickMessageComponent = MiniMessage.builder().build().deserialize(
                    getBanSettings().kickMessage(),
                    Placeholder.component("player", player.name()),
                    Formatter.date("date", ban.endZoned()),
                    DurationUtils.formatDurationTag("duration", banDuration));

            player.kick(kickMessageComponent);
        }

        LifestealerPostUserBanEvent postBanEvent = new LifestealerPostUserBanEvent(player, user, ban);
        postBanEvent.callEvent();
    }

    /**
     * Executes the ban commands.
     * You should call this method after calling {@link #setUserBanned(Player, LifestealerUser)}.
     * <p>
     * <b>Thread-safety:</b> Thread-safe but requires to run on the global thread.
     *
     * @param player      the player to ban
     * @param banDuration the duration of the ban
     */
    public void executeBanCommands(@NotNull Player player, Duration banDuration) {
        for (String command : getBanSettings().commands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("<player>", player.getName())
                    .replace("<duration>", Long.toString(banDuration.toSeconds())));
        }
    }

    /**
     * Gets the ban information of a user.
     * If the user is not banned, it will return null.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user (if the ban is expired, it will be removed from the user).
     *
     * @param user the user to get the ban information
     * @return the ban information of the user, or null if the user is not banned
     */
    public @Nullable LifestealerUser.Ban getBan(@NotNull LifestealerUser user) {
        LifestealerUser.Ban ban = user.getInternalBan();
        if (ban == null) {
            return null;
        }

        if (!ban.isBanActive()) {
            unbanUser(user);
            return null;
        }

        return ban;
    }

    /**
     * Gets the ban information of a user if the external flag is disabled.
     * If the user is not banned or the external flag is enabled, it will return null.
     * <p>
     * Use this to test if the player is being kicked by the plugin.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user (if the ban is expired, it will be removed from the user).
     *
     * @param user the user to check if they are banned
     * @return the ban information of the user, or null if the user is not banned or the external flag is enabled
     */
    public @Nullable LifestealerUser.Ban getBanIfNotExternalSettingEnabled(@NotNull LifestealerUser user) {
        return !getBanSettings().external() ? getBan(user) : null;
    }

    /**
     * Checks if a user is banned.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user (if the ban is expired, it will be removed from the user).
     *
     * @param user the user to check if they are banned
     * @return true if the user is banned
     */
    public boolean isBanned(@NotNull LifestealerUser user) {
        return getBan(user) != null;
    }

    /**
     * Unbans a user.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     *
     * @param user the user to unban
     */
    public void unbanUser(@NotNull LifestealerUser user) {
        user.setBan(null);
    }

    /**
     * Computes the {@link LifestealerUserRules} of a player based on their permissions and the user's rules modifier.
     * <p>
     * <b>Thread-safety:</b> Requires a read lock on the user.
     *
     * @param player the player to compute the rules
     * @param user   the user related to the player
     * @return the computed {@link LifestealerUserRules} of the player
     */
    public LifestealerUserRules computeUserRules(Player player, LifestealerUser user) {
        LifestealerUserRules permissionRules = userRulesController.computeRulesByPermission(player::hasPermission);
        return permissionRules.sum(user.getRulesModifier());
    }

    /**
     * Sets the rules modifier of a user.
     * <p>
     * <b>Thread-safety:</b> Requires a write lock on the user.
     *
     * @param user          the user to set the rules modifier
     * @param modifierRules the rules modifier to set
     */
    public void setRulesModifier(LifestealerUser user, LifestealerUserRules modifierRules) {
        user.setRulesModifier(modifierRules);
        userManager.saveUserAsync(user);
    }
}
