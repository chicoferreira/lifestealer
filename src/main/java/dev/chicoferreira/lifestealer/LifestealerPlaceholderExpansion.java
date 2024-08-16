package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.heart.LifestealerUserRules;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LifestealerPlaceholderExpansion extends PlaceholderExpansion {

    private final Lifestealer plugin;

    public LifestealerPlaceholderExpansion(Lifestealer plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lifestealer";
    }

    @Override
    public @NotNull String getAuthor() {
        return "";
    }

    @Override
    public @NotNull String getVersion() {
        return "";
    }

    private int getMaxHearts(Player player) {
        LifestealerUserRules rules = plugin.getUserRulesController().computeRules(player::hasPermission);
        return rules.maxHearts();
    }

    private int getMinHearts(Player player) {
        LifestealerUserRules rules = plugin.getUserRulesController().computeRules(player::hasPermission);
        return rules.minHearts();
    }

    @Override
    public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
        if (player == null) {
            return null;
        }

        LifestealerUser user = plugin.getUserManager().getUser(player.getUniqueId());

        return switch (params) {
            case "health" -> String.valueOf(player.getHealth());
            case "hearts" -> String.valueOf(user.getHearts());
            case "max_hearts" -> String.valueOf(getMaxHearts(player));
            case "min_hearts" -> String.valueOf(getMinHearts(player));
            case "inventory" -> String.valueOf(plugin.getItemManager().calculateTotalHeartsInInventory(player));
            default -> {
                if (params.startsWith("inventory_")) {
                    String itemName = params.substring("inventory_".length());
                    yield String.valueOf(plugin.getItemManager().countHeartItems(player, itemName));
                }
                yield null;
            }
        };
    }
}
