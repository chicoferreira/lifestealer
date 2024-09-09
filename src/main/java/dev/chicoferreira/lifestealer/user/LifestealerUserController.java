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
     *
     * @param player the player to set the hearts
     * @param user   the user related to the player
     * @param hearts the amount hearts to set
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult setHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        LifestealerUserRules rules = this.computeUserRules(player, user);
        hearts = Math.clamp(hearts, rules.minHearts(), rules.maxHearts());

        int currentHearts = user.getHearts();

        user.setHearts(hearts);
        updatePlayerHearts(player, user);

        this.userManager.saveUserAsync(user);

        return new ChangeHeartsResult(currentHearts, hearts);
    }

    /**
     * Updates the player health bar to match the amount of hearts the user has.
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
     *
     * @param player the player to add hearts
     * @param user   the user related to the player
     * @param hearts the amount of hearts to add
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult addHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        return setHearts(player, user, user.getHearts() + hearts);
    }

    /**
     * Removes hearts from the player health bar and saves the new amount in the database asynchronously.
     * If the new amount is less than the minimum amount of hearts the player can have, it will be clamped the minimum value.
     *
     * @param player the player to add hearts
     * @param user   the user related to the player
     * @param hearts the amount of hearts to remove
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult removeHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        return setHearts(player, user, user.getHearts() - hearts);
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
    public record BanSettings(String kickMessage, String joinMessage, List<String> commands, boolean external) {
    }

    /**
     * Gets the current ban settings.
     *
     * @return the current ban settings
     */
    public @NotNull BanSettings getBanSettings() {
        return banSettings;
    }

    /**
     * Sets the ban settings.
     * Used when the config is reloaded.
     *
     * @param banSettings the new ban settings
     */
    public void setBanSettings(@NotNull BanSettings banSettings) {
        this.banSettings = banSettings;
    }

    /**
     * Bans a user for the default ban time depending on its {@link LifestealerUserRules}.
     * <p>
     * The ban commands will be executed. The player will be kicked if the external flag is disabled.
     * The player can't join the server until the ban is lifted, or the external flag is enabled.
     *
     * @param player the player to ban
     * @param user   the user related to the player
     * @return the created ban information
     */
    public @NotNull LifestealerUser.Ban banUser(@NotNull Player player, @NotNull LifestealerUser user) {
        LifestealerUserRules rules = this.computeUserRules(player, user);
        Duration banDuration = rules.banTime();

        return banUser(player, user, banDuration);
    }

    /**
     * Bans a user for a specific duration.
     * <p>
     * The ban commands will be executed. The player will be kicked if the external flag is disabled.
     * The player can't join the server until the ban is lifted, or the external flag is enabled.
     *
     * @param player      the player to ban
     * @param user        the user related to the player
     * @param banDuration the duration of the ban
     * @return the created ban information
     */
    public @NotNull LifestealerUser.Ban banUser(@NotNull Player player, @NotNull LifestealerUser user, @NotNull Duration banDuration) {
        LifestealerUser.Ban ban = new LifestealerUser.Ban(Instant.now(), banDuration);
        user.setBan(ban);

        userManager.saveUserAsync(user);

        for (String command : banSettings.commands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replace("<player>", player.getName())
                    .replace("<duration>", Long.toString(banDuration.toSeconds())));
        }

        if (!banSettings.external()) {
            if (player.isDead()) {
                player.spigot().respawn();
            }

            Component kickMessageComponent = MiniMessage.builder().build().deserialize(
                    banSettings.kickMessage(),
                    Placeholder.component("player", player.name()),
                    Formatter.date("date", ban.endZoned()),
                    DurationUtils.formatDurationTag("duration", banDuration));

            player.kick(kickMessageComponent);
        }

        LifestealerPostUserBanEvent postBanEvent = new LifestealerPostUserBanEvent(player, user, ban);
        postBanEvent.callEvent();

        return ban;
    }

    /**
     * Gets the ban information of a user.
     * If the user is not banned, it will return null.
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
     * Checks if a user is banned.
     *
     * @param user the user to check if they are banned
     * @return true if the user is banned
     */
    public boolean isBanned(@NotNull LifestealerUser user) {
        return getBan(user) != null;
    }

    /**
     * Unbans a user.
     *
     * @param user the user to unban
     */
    public void unbanUser(@NotNull LifestealerUser user) {
        user.setBan(null);
    }

    public LifestealerUserRules computeUserRules(Player player, LifestealerUser user) {
        LifestealerUserRules permissionRules = userRulesController.computeRulesByPermission(player::hasPermission);
        return permissionRules.sum(user.getRulesModifier());
    }

    public void setRulesModifier(LifestealerUser user, LifestealerUserRules modifierRules) {
        user.setRulesModifier(modifierRules);
        userManager.saveUserAsync(user);
    }
}
