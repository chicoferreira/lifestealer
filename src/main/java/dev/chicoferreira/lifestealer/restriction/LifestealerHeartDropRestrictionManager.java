package dev.chicoferreira.lifestealer.restriction;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the restrictions on the heart drop that should be applied when a player dies.
 */
public class LifestealerHeartDropRestrictionManager {

    private final List<LifestealerHeartDropRestrictionAction> heartDropRestrictionActions;

    public LifestealerHeartDropRestrictionManager(List<LifestealerHeartDropRestrictionAction> actions) {
        this.heartDropRestrictionActions = new ArrayList<>();
        this.heartDropRestrictionActions.addAll(actions);
    }

    /**
     * Evaluates the action to take when a player dies (drop the heart item or not and remove hearts from the player or not).
     *
     * @param player the player to test
     * @param user   the user related to the player
     * @param event  the event to test
     * @return the action to take
     */
    public @NotNull LifestealerHeartDropAction evaluateHeartDropAction(@NotNull Player player, @NotNull LifestealerUser user, @NotNull PlayerDeathEvent event) {
        for (LifestealerHeartDropRestrictionAction heartDropRestrictionAction : heartDropRestrictionActions) {
            if (heartDropRestrictionAction.heartDropRestriction().shouldRestrictHeartDrop(player, user, event)) {
                return heartDropRestrictionAction.actionIfRestricted();
            }
        }
        return LifestealerHeartDropAction.DROP;
    }

    /**
     * Sets a new list of restriction actions.
     * Used when the configuration is reloaded.
     *
     * @param actions the new actions list
     */
    public void setActions(List<LifestealerHeartDropRestrictionAction> actions) {
        this.heartDropRestrictionActions.clear();
        this.heartDropRestrictionActions.addAll(actions);
    }
}
