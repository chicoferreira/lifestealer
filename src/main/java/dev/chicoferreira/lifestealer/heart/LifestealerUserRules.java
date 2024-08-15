package dev.chicoferreira.lifestealer.heart;


import java.time.Duration;

/**
 * Represents the rules the user has. This is calculated through the {@link LifestealerUserRulesController}.
 * The rules are used to determine the maximum and minimum amount of hearts a player can have,
 * as well as the ban time if they have less than the minimum amount of hearts.
 *
 * @param maxHearts The maximum amount of hearts a player can have. The player cannot consume more than this amount.
 * @param minHearts The minimum amount of hearts a player can have. If a player has less than this amount, they will be banned temporarily.
 * @param banTime   The duration that a player will be banned if they have less than the minimum amount of hearts.
 */
public record LifestealerUserRules(int maxHearts, int minHearts, Duration banTime) {
}
