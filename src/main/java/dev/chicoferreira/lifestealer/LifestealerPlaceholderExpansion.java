package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.rules.LifestealerUserRules;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LifestealerPlaceholderExpansion extends PlaceholderExpansion {

    private final Lifestealer plugin;
    private final LifestealerUserController userController;

    public LifestealerPlaceholderExpansion(Lifestealer plugin, LifestealerUserController userController) {
        this.plugin = plugin;
        this.userController = userController;
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
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        LifestealerUser user = plugin.getUserManager().getUser(player.getUniqueId());

        String result = switch (params) {
            case "hearts" -> String.valueOf(user.getHearts());
            case "ban" -> {
                LifestealerUser.Ban ban = this.userController.getBan(user);
                yield ban != null ? Long.toString(ban.remaining().toSeconds()) : null;
            }
            default -> null;
        };

        if (result == null && player instanceof Player onlinePlayer) {
            return onPlaceholderRequest(onlinePlayer, params);
        }

        return result;
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
