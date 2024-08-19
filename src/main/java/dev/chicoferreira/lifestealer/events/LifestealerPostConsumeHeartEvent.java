package dev.chicoferreira.lifestealer.events;

import dev.chicoferreira.lifestealer.LifestealerController;
import dev.chicoferreira.lifestealer.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a player has consumed a heart.
 * This event is called after the hearts have been added to the player and the heart item has been removed.
 * This event is called even if the player's hearts have not changed (e.g. the player is already at maximum hearts).
 * This event is not cancellable.
 *
 * @see LifestealerPreConsumeHeartEvent
 * @see LifestealerController#addHearts(Player, LifestealerUser, int)
 */
public class LifestealerPostConsumeHeartEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final LifestealerUser user;
    private final int amount;
    private final ItemStack itemStack;
    private final LifestealerController.ChangeHeartsResult result;

    /**
     * Constructs a new LifestealerPostConsumeHeartEvent.
     *
     * @param player    the player that consumed the heart
     * @param user      the user related to the player
     * @param amount    the amount of hearts contained in the item
     * @param itemStack the item that was consumed
     * @param result    the result of the change of hearts
     */
    public LifestealerPostConsumeHeartEvent(@NotNull Player player, @NotNull LifestealerUser user, int amount, @NotNull ItemStack itemStack, @NotNull LifestealerController.ChangeHeartsResult result) {
        this.player = player;
        this.user = user;
        this.amount = amount;
        this.itemStack = itemStack;
        this.result = result;
    }

    /**
     * Gets the player that consumed the heart.
     *
     * @return the player that consumed the heart
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
     * Gets the amount of hearts contained in the item.
     *
     * @return the amount of hearts contained in the item (guaranteed to be higher than 0)
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets the itemStack that was involved in the event.
     * The itemStack amount has already been decremented by 1 due to the consumption.
     *
     * @return the item that was consumed
     */
    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Gets the result of the change of hearts.
     *
     * @return the result of the change of hearts
     */
    public @NotNull LifestealerController.ChangeHeartsResult getChangeResult() {
        return result;
    }

    /**
     * Checks if the amount of hearts of the player has changed.
     * This will return {@code true} if the player's hearts have changed, or {@code false} if the player's hearts
     * have not changed (e.g. the player is already at maximum hearts).
     * Equivalent to {@code event.getResult().hasChanged()}.
     *
     * @return if the amount of hearts of the player has changed
     */
    public boolean hasChanged() {
        return result.hasChanged();
    }

    /**
     * Gets the difference between the new and previous amount of hearts.
     * This will return 0 if the player's hearts have not changed (e.g. the player is already at maximum hearts).
     * Equivalent to {@code event.getChangeResult().difference()}.
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
