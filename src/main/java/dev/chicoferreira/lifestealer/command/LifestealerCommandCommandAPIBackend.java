package dev.chicoferreira.lifestealer.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class LifestealerCommandCommandAPIBackend {

    private final LifestealerCommand command;

    public LifestealerCommandCommandAPIBackend(LifestealerCommand command) {
        this.command = command;
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
                ).register(plugin);
    }

}
