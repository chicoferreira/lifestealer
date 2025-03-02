package dev.chicoferreira.lifestealer.events;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Event that is called before a user is banned when he dies.
 * You can change the duration of the ban in this event.
 * <p>
 * This event is NOT called when the user is banned by the ban command, only when the user dies with less than the minimum amount of hearts.
 * If you wish to listen to the command as well, you should use {@link dev.chicoferreira.lifestealer.events.LifestealerPostUserBanEvent}.
 * <p>
 * This event is called before the user is kicked and the ban commands are executed.
 * This event will still be called even if the external flag is set to true in the {@link dev.chicoferreira.lifestealer.user.LifestealerUserController.BanSettings}
 * <p>
 * <b>Thread-safety (WARNING):</b> This event is called with a write lock active on the user.
 */
public class LifestealerPreUserBanEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final @NotNull Player player;
    private final @NotNull LifestealerUser user;
    private @NotNull Duration banDuration;
    private boolean cancelled;

    /**
     * Constructs a new {@link LifestealerPreUserBanEvent}.
     *
     * @param player      The {@link Player} who is going to be banned.
     * @param user        The {@link LifestealerUser} associated with the player.
     * @param banDuration The duration of the ban.
     */
    public LifestealerPreUserBanEvent(@NotNull Player player, @NotNull LifestealerUser user, @NotNull Duration banDuration) {
        this.player = player;
        this.user = user;
        this.banDuration = banDuration;
    }

    /**
     * Gets the player going to be banned.
     *
     * @return The banned player.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link LifestealerUser} associated with the player.
     *
     * @return The {@link LifestealerUser} associated with the player.
     */
    public @NotNull LifestealerUser getUser() {
        return user;
    }

    /**
     * Gets the duration of the ban.
     *
     * @return The duration of the ban.
     */
    public @NotNull Duration getBanDuration() {
        return banDuration;
    }

    /**
     * Sets the duration of the ban.
     *
     * @param banDuration The duration of the ban.
     */
    public void setBanDuration(@NotNull Duration banDuration) {
        this.banDuration = banDuration;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
