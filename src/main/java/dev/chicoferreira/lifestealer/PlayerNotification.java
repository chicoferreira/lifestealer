package dev.chicoferreira.lifestealer;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a notification that can be sent to a player.
 * A notification is sent when a command is executed, when the player consumes a heart, etc.
 * <p>
 * The message components are stored as string and are parsed using the adventure-api mini-message format
 * when the message is sent, so it's possible to use the native placeholders of the adventure-api.
 *
 * @param textMessage      the text message to send to the player
 * @param actionBarMessage the action bar message to send to the player
 * @param titleMessage     the title message to send to the player
 * @param subtitleMessage  the subtitle message to send to the player
 * @param titleTimes       the title timings to send to the player
 * @param sound            the sound to play to the player
 */
public record PlayerNotification(@NotNull Optional<String> textMessage,
                                 @NotNull Optional<String> actionBarMessage,
                                 @NotNull Optional<String> titleMessage,
                                 @NotNull Optional<String> subtitleMessage,
                                 @NotNull Optional<Title.Times> titleTimes,
                                 @NotNull Optional<Sound> sound
) {

    public static MiniMessage MINI_MESSAGE = MiniMessage
            .builder()
            .editTags(tag -> tag
                    .tag("heart", Tag.selfClosingInserting(Component.text("❤").color(TextColor.color(255, 0, 0))))
            )
            .build();

    /**
     * Creates player notification with a text message.
     *
     * @param textMessage the text message to send to the player
     */
    public PlayerNotification(String textMessage) {
        this(Optional.of(textMessage), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Sends the notification to the player.
     *
     * @param sender    the player or console to send the notification to
     * @param resolvers the adventure-api resolvers to apply to the messages (they act basically as placeholders)
     */
    public void sendTo(CommandSender sender, TagResolver... resolvers) {
        Function<String, Component> parseComponent = string -> MINI_MESSAGE.deserialize(string, resolvers);

        textMessage.map(parseComponent).ifPresent(sender::sendMessage);
        actionBarMessage.map(parseComponent).ifPresent(sender::sendActionBar);
        titleMessage.map(parseComponent).ifPresent(titleMessage -> sender.sendTitlePart(TitlePart.TITLE, titleMessage));
        subtitleMessage.map(parseComponent).ifPresent(subtitleMessage -> sender.sendTitlePart(TitlePart.SUBTITLE, subtitleMessage));
        titleTimes.ifPresent(times -> sender.sendTitlePart(TitlePart.TIMES, times));
        sound.ifPresent(sender::playSound);
    }
}
