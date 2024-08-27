package dev.chicoferreira.lifestealer.command;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import static dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;

public class LifestealerCommandCommandAPIBackend {

    private final LifestealerCommand command;
    private final LifestealerHeartItemManager itemManager;

    public LifestealerCommandCommandAPIBackend(LifestealerCommand command, LifestealerHeartItemManager itemManager) {
        this.command = command;
        this.itemManager = itemManager;
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
                ).then(new LiteralArgument("item")
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
                                        )
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
                                        )
                                )
                        ).then(new LiteralArgument("list")
                                .executes((sender, args) -> {
                                    command.subcommandItemList(sender);
                                })
                        )
                ).then(new LiteralArgument("ban")
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
                )
                .register(plugin);
    }

    public Argument<@NotNull LifestealerHeartItem> generateItemTypeArgument(String name) {
        return new CustomArgument<>(new StringArgument(name), info -> {
            String itemName = info.input();
            LifestealerHeartItem item = itemManager.getItem(itemName);
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

    public Argument<String> generateItemTypeNameSuggestion(String name) {
        return new StringArgument(name).replaceSuggestions(itemTypeNameSuggestions());
    }

    private @NotNull ArgumentSuggestions<CommandSender> itemTypeNameSuggestions() {
        return ArgumentSuggestions.strings((_s) -> itemManager.getItemTypeNames().toArray(String[]::new));
    }
}
