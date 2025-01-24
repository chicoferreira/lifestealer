package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

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

    private int getMaxHearts(Player player, LifestealerUser user) {
        LifestealerUserRules rules = plugin.getUserController().computeUserRules(player, user);
        return rules.maxHearts();
    }

    private int getMinHearts(Player player, LifestealerUser user) {
        LifestealerUserRules rules = plugin.getUserController().computeUserRules(player, user);
        return rules.minHearts();
    }

    private int getReturnHearts(Player player, LifestealerUser user) {
        LifestealerUserRules rules = plugin.getUserController().computeUserRules(player, user);
        return rules.returnHearts();
    }

    private Duration getBanTime(Player player, LifestealerUser user) {
        LifestealerUserRules rules = plugin.getUserController().computeUserRules(player, user);
        return rules.banTime();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        LifestealerUser user;
        try {
            user = plugin.getUserManager().getOrLoadUser(player.getUniqueId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String result = switch (params) {
            case "max_hearts_modifier" -> String.valueOf(user.getRulesModifier().maxHearts());
            case "min_hearts_modifier" -> String.valueOf(user.getRulesModifier().minHearts());
            case "ban_time_modifier" -> String.valueOf(user.getRulesModifier().banTime().toSeconds());
            case "return_hearts_modifier" -> String.valueOf(user.getRulesModifier().returnHearts());
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

        LifestealerUser user;
        try {
            user = plugin.getUserManager().getOrLoadUser(player.getUniqueId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return switch (params) {
            case "health" -> String.valueOf(player.getHealth());
            case "hearts" -> String.valueOf(user.getHearts());
            case "max_hearts" -> String.valueOf(getMaxHearts(player, user));
            case "min_hearts" -> String.valueOf(getMinHearts(player, user));
            case "return_hearts" -> String.valueOf(getReturnHearts(player, user));
            case "ban_time" -> String.valueOf(getBanTime(player, user).toSeconds());
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

    public static @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');
            final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }
}
