package dev.chicoferreira.lifestealer.restriction;

/**
 * A record that represents a restriction and the action to take if the restriction is met.
 *
 * @param heartDropRestriction the restriction
 * @param actionIfRestricted   the action to take if the restriction is met
 */
public record LifestealerHeartDropRestrictionAction(LifestealerHeartDropRestriction heartDropRestriction,
                                                    LifestealerHeartDropAction actionIfRestricted) {
}
