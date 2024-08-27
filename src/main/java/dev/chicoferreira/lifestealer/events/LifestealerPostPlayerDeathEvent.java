package dev.chicoferreira.lifestealer.events;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropAction;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player dies, after the hearts have been removed from the player and before the
 * player gets banned if they have less than the minimum amount of hearts.
 * <p>
 * This event won't be called if the heart drop was restricted by a {@link dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction}
 * that produced {@link LifestealerHeartDropAction#shouldRemoveHearts()} true.
 */
public class LifestealerPostPlayerDeathEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PlayerDeathEvent deathEvent;
    private final Player player;
    private final LifestealerUser user;
    private final ItemStack itemStackToDrop;
    private final int heartsRemoved;
    private final LifestealerUserController.ChangeHeartsResult result;

    /**
     * Constructs a new LifestealerPostPlayerDeathEvent.
     *
     * @param deathEvent      the death event that originated this event
     * @param player          the player that died
     * @param user            the user related to the player
     * @param itemStackToDrop the heart item to drop when the player dies
     * @param heartsRemoved   the amount of hearts removed from the player
     * @param result          the result of the change of hearts
     */
    public LifestealerPostPlayerDeathEvent(@NotNull PlayerDeathEvent deathEvent, @NotNull Player player, @NotNull LifestealerUser user, @Nullable ItemStack itemStackToDrop, int heartsRemoved, @NotNull LifestealerUserController.ChangeHeartsResult result) {
        this.deathEvent = deathEvent;
        this.player = player;
        this.user = user;
        this.itemStackToDrop = itemStackToDrop;
        this.heartsRemoved = heartsRemoved;
        this.result = result;
    }

    /**
     * Gets the death event that originated this event.
     * Changes to this event are not recommended. Please make sure to be sure of what you are doing.
     *
     * @return the death event that originated this event
     */
    public @NotNull PlayerDeathEvent getDeathEvent() {
        return deathEvent;
    }

    /**
     * Gets the player that died.
     *
     * @return the player that died
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the user related to the player.
     *
     * @return the user related to the player
     */
    public @NotNull LifestealerUser getUser() {
        return user;
    }

    /**
     * Returns the {@link ItemStack} to drop when the player dies.
     * Unless changed, the heart item is valid for consumption and contains the amount of hearts that {@link #getRemovedHearts()} returns.
     * <p>
     * This can return null if a plugin has set the heart item to drop to null.
     *
     * @return the heart {@link ItemStack} to drop when the player dies
     */
    public @Nullable ItemStack getItemStackToDrop() {
        return itemStackToDrop;
    }

    /**
     * Gets the amount of hearts that had been removed from the player.
     * Unless changed, this is the amount of hearts contained in the {@link ItemStack} returned by {@link #getItemStackToDrop()}.
     *
     * @return the amount of hearts to remove from the player when they die
     */
    public int getRemovedHearts() {
        return heartsRemoved;
    }

    /**
     * Gets the result of the change of hearts.
     *
     * @return the result of the change of hearts
     */
    public LifestealerUserController.ChangeHeartsResult getChangeResult() {
        return result;
    }

    /**
     * Checks if the amount of hearts of the player has changed.
     * This will return {@code true} if the player's hearts have changed, or {@code false} if the player's hearts
     * have not changed (e.g. the player is already at maximum hearts).
     * Equivalent to {@code event.getChangeResult().hasChanged()}.
     *
     * @return if the amount of hearts of the player has changed
     */
    public boolean hasChanged() {
        return result.hasChanged();
    }

    /**
     * Gets the difference between the new and previous amount of hearts.
     * This will return 0 if the player's hearts have not changed (e.g. the player is already at maximum hearts).
     * Equivalent to {@code event.getResult().difference()}.
     *
     * @return the difference between the new and previous amount of hearts (newHearts - previousHearts)
     */
    public int getDifference() {
        return result.difference();
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
