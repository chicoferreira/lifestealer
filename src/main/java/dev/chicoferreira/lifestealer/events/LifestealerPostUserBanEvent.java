package dev.chicoferreira.lifestealer.events;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is called after a user has been banned and the ban commands have been executed.
 * This event will still be called even if the external flag is set to true in the {@link dev.chicoferreira.lifestealer.user.LifestealerUserController.BanSettings}
 * <p>
 * <b>Thread-safety (WARNING):</b> This event is called with no active locks on the user.
 */
public class LifestealerPostUserBanEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final @NotNull Player player;
    private final @NotNull LifestealerUser user;
    private final @NotNull LifestealerUser.Ban ban;

    /**
     * Constructs a new {@link LifestealerPostUserBanEvent}.
     *
     * @param player The {@link Player} who has been banned.
     * @param user   The {@link LifestealerUser} associated with the banned player.
     * @param ban    The ban details.
     */
    public LifestealerPostUserBanEvent(@NotNull Player player, @NotNull LifestealerUser user, @NotNull LifestealerUser.Ban ban) {
        this.player = player;
        this.user = user;
        this.ban = ban;
    }

    /**
     * Gets the banned player.
     *
     * @return The banned player.
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link LifestealerUser} associated with the banned player.
     *
     * @return The {@link LifestealerUser} associated with the banned player.
     */
    public @NotNull LifestealerUser getUser() {
        return user;
    }

    /**
     * Gets the {@link LifestealerUser.Ban} details.
     *
     * @return The ban details.
     */
    public @NotNull LifestealerUser.Ban getBan() {
        return ban;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return The list of handlers.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets the static handler list for this event.
     *
     * @return The static handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}