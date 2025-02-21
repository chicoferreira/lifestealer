package dev.chicoferreira.lifestealer.command;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.Lifestealer;
import dev.chicoferreira.lifestealer.command.LifestealerCommand.LifestealerRuleModifier;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import static dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;

public class LifestealerCommandBackend {

    private final LifestealerCommand command;
    private final Lifestealer lifestealer;

    public LifestealerCommandBackend(LifestealerCommand command, Lifestealer lifestealer) {
        this.command = command;
        this.lifestealer = lifestealer;
    }

    public <T> @NotNull T getArgument(CommandArguments args, String argument) throws WrapperCommandSyntaxException {
        T t = args.getUnchecked(argument);
        if (t == null) {
            throw CommandAPI.failWithString("Argument " + argument + " is required");
        }
        return t;
    }

    public void registerCommand(JavaPlugin plugin) {
        new CommandTree("lifestealer")
                .withPermission("lifestealer.admin")
                .then(new LiteralArgument("item")
                        .then(new LiteralArgument("give")
                                .then(generateItemTypeArgument("item")
                                        .then(new IntegerArgument("amount")
                                                .then(new EntitySelectorArgument.OnePlayer("player")
                                                        .executes((sender, args) -> {
                                                            command.subcommandItemGive(sender,
                                                                    getArgument(args, "item"),
                                                                    getArgument(args, "amount"),
                                                                    getArgument(args, "player"));
                                                        })
                                                ).executesPlayer((sender, args) -> {
                                                    command.subcommandItemGive(sender,
                                                            getArgument(args, "item"),
                                                            getArgument(args, "amount"),
                                                            sender);
                                                })
                                        ).executesPlayer((sender, args) -> {
                                            command.subcommandItemGive(sender,
                                                    getArgument(args, "item"),
                                                    1,
                                                    sender);
                                        })
                                )
                        ).then(new LiteralArgument("take")
                                .then(generateItemTypeNameSuggestion("item")
                                        .then(new IntegerArgument("amount")
                                                .then(new EntitySelectorArgument.OnePlayer("player")
                                                        .executes((sender, args) -> {
                                                            command.subcommandItemTake(sender,
                                                                    getArgument(args, "item"),
                                                                    getArgument(args, "amount"),
                                                                    getArgument(args, "player"));
                                                        })
                                                ).executesPlayer((sender, args) -> {
                                                    command.subcommandItemTake(sender,
                                                            getArgument(args, "item"),
                                                            getArgument(args, "amount"),
                                                            sender);
                                                })
                                        ).executesPlayer((sender, args) -> {
                                            command.subcommandItemTake(sender,
                                                    getArgument(args, "item"),
                                                    1,
                                                    sender);
                                        })
                                )
                        ).then(new LiteralArgument("list")
                                .executes((sender, args) -> {
                                    command.subcommandItemList(sender);
                                })
                        )
                ).then(new LiteralArgument("user")
                        .then(new LiteralArgument("hearts")
                                .then(new LiteralArgument("set")
                                        .then(new IntegerArgument("amount")
                                                .executesPlayer((sender, args) -> {
                                                    command.subcommandHeartsSet(sender, getArgument(args, "amount"), sender);
                                                })
                                                .then(new EntitySelectorArgument.OnePlayer("player")
                                                        .executes((sender, args) -> {
                                                            command.subcommandHeartsSet(sender, getArgument(args, "amount"), getArgument(args, "player"));
                                                        })
                                                )
                                        )
                                ).then(new LiteralArgument("add")
                                        .then(new IntegerArgument("amount")
                                                .executesPlayer((sender, args) -> {
                                                    command.subcommandHeartsAdd(sender, getArgument(args, "amount"), sender);
                                                })
                                                .then(new EntitySelectorArgument.OnePlayer("player")
                                                        .executes((sender, args) -> {
                                                            command.subcommandHeartsAdd(sender, getArgument(args, "amount"), getArgument(args, "player"));
                                                        })
                                                )
                                        )
                                ).then(new LiteralArgument("remove")
                                        .then(new IntegerArgument("amount")
                                                .executesPlayer((sender, args) -> {
                                                    command.subcommandHeartsRemove(sender, getArgument(args, "amount"), sender);
                                                })
                                                .then(new EntitySelectorArgument.OnePlayer("player")
                                                        .executes((sender, args) -> {
                                                            command.subcommandHeartsRemove(sender, getArgument(args, "amount"), getArgument(args, "player"));
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(new LiteralArgument("ban")
                                .then(new EntitySelectorArgument.OnePlayer("player")
                                        .executes((sender, args) -> {
                                            command.subcommandBanUser(sender, getArgument(args, "player"));
                                        })
                                        .then(generateDurationArgument("duration")
                                                .executes((sender, args) -> {
                                                    command.subcommandBanUserDuration(sender, getArgument(args, "player"), getArgument(args, "duration"));
                                                })
                                        )
                                )
                        ).then(new LiteralArgument("unban")
                                .then(new OfflinePlayerArgument("player")
                                        .executes((sender, args) -> {
                                            command.subcommandUnbanUser(sender, getArgument(args, "player"));
                                        })
                                )
                        ).then(new LiteralArgument("info")
                                .then(new OfflinePlayerArgument("player")
                                        .executes((sender, args) -> {
                                            command.subcommandUserInfo(sender, getArgument(args, "player"));
                                        })
                                ).executesPlayer((sender, args) -> {
                                    command.subcommandUserInfo(sender, sender);
                                })
                        ).then(new LiteralArgument("rulemodifier")
                                .then(new LiteralArgument("set")
                                        .then(generateRuleModifierArgument("rule")
                                                .then(new IntegerArgument("value")
                                                        .then(new EntitySelectorArgument.OnePlayer("player")
                                                                .executes((sender, args) -> {
                                                                    command.subcommandUserSetRuleModifier(sender,
                                                                            getArgument(args, "player"),
                                                                            getArgument(args, "rule"),
                                                                            getArgument(args, "value"));
                                                                })
                                                        ).executesPlayer((sender, args) -> {
                                                            command.subcommandUserSetRuleModifier(sender,
                                                                    sender,
                                                                    getArgument(args, "rule"),
                                                                    getArgument(args, "value"));
                                                        })
                                                )
                                        )
                                ).then(new LiteralArgument("adjust")
                                        .then(generateRuleModifierArgument("rule")
                                                .then(new IntegerArgument("adjustment")
                                                        .then(new EntitySelectorArgument.OnePlayer("player")
                                                                .executes((sender, args) -> {
                                                                    command.subcommandUserAdjustRuleModifier(sender,
                                                                            getArgument(args, "player"),
                                                                            getArgument(args, "rule"),
                                                                            getArgument(args, "adjustment"));
                                                                })
                                                        ).executesPlayer((sender, args) -> {
                                                            command.subcommandUserAdjustRuleModifier(sender,
                                                                    sender,
                                                                    getArgument(args, "rule"),
                                                                    getArgument(args, "adjustment"));
                                                        })
                                                )
                                        )
                                ).then(new LiteralArgument("reset")
                                        .then(new EntitySelectorArgument.OnePlayer("player")
                                                .executes((sender, args) -> {
                                                    command.subcommandUserResetRuleModifiers(sender, getArgument(args, "player"));
                                                })
                                        ).executesPlayer((sender, args) -> {
                                            command.subcommandUserResetRuleModifiers(sender, sender);
                                        })
                                )
                        )
                ).then(new LiteralArgument("reload")
                        .executes((sender, args) -> {
                            command.subcommandReload(sender);
                        })
                ).then(new LiteralArgument("storage")
                        .then(new LiteralArgument("import")
                                .then(generateFileImportArgument("file")
                                        .executes((sender, args) -> {
                                            command.subcommandStorageImport(sender, getArgument(args, "file"));
                                        })
                                )
                        )
                        .then(new LiteralArgument("export")
                                .executes((sender, args) -> {
                                    String fileName = "lifestealer_users_" + DEFAULT_EXPORT_FILENAME_FORMAT.format(LocalDateTime.now()) + ".json";
                                    command.subcommandStorageExport(sender, fileName);
                                })
                                .then(new GreedyStringArgument("file")
                                        .executes((sender, args) -> {
                                            command.subcommandStorageExport(sender, getArgument(args, "file"));
                                        })
                                )
                        )
                )
                .register(plugin);
    }

    private static final DateTimeFormatter DEFAULT_EXPORT_FILENAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public Argument<@NotNull LifestealerRuleModifier> generateRuleModifierArgument(String name) {
        return new CustomArgument<>(new StringArgument(name), info -> {
            String modifierName = info.input();
            LifestealerRuleModifier modifier = LifestealerRuleModifier.fromName(modifierName);
            if (modifier == null) {
                throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Unknown rule modifier: ").appendArgInput());
            }
            return modifier;
        }).replaceSuggestions(ArgumentSuggestions.strings(
                Arrays.stream(LifestealerRuleModifier.values())
                        .map(LifestealerRuleModifier::getRule)
                        .toArray(String[]::new))
        );
    }

    public Argument<@NotNull LifestealerHeartItem> generateItemTypeArgument(String name) {
        return new CustomArgument<>(new StringArgument(name), info -> {
            String itemName = info.input();
            LifestealerHeartItem item = lifestealer.getItemManager().getItem(itemName);
            if (item == null) {
                throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Unknown item: ").appendArgInput());
            }
            return item;
        }).replaceSuggestions(itemTypeNameSuggestions());
    }

    public Argument<@NotNull Duration> generateDurationArgument(String name) {
        return new CustomArgument<>(new GreedyStringArgument(name), info -> {
            String input = info.input();
            try {
                return DurationUtils.parse(input);
            } catch (Exception e) {
                throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Invalid duration: ").appendArgInput());
            }
        }).replaceSuggestions(ArgumentSuggestions.strings("1d", "1h", "1m", "1s"));
    }

    public Argument<@NotNull String> generateFileImportArgument(String name) {
        return new GreedyStringArgument(name).replaceSuggestions(ArgumentSuggestions.strings((_s) ->
                lifestealer.getImportExportStorage().listFiles().stream().map(Path::toString).toArray(String[]::new)
        ));
    }

    public Argument<String> generateItemTypeNameSuggestion(String name) {
        return new StringArgument(name).replaceSuggestions(itemTypeNameSuggestions());
    }

    private @NotNull ArgumentSuggestions<CommandSender> itemTypeNameSuggestions() {
        return ArgumentSuggestions.strings((_s) -> lifestealer.getItemManager().getItemTypeNames().toArray(String[]::new));
    }
}
