package com.github.jenya705.cubicore.minestom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.tag.Tag;

/**
 * @author Jenya705
 */
public class ChatImplementation {

    private static final Tag<Integer> colorTag = Tag.Integer("color");

    private final Cubicore cubicore;

    public ChatImplementation(Cubicore cubicore) {
        this.cubicore = cubicore;
        MinecraftServer.getGlobalEventHandler().addListener(
                PlayerPluginMessageEvent.class,
                event -> {
                    if (!event.getIdentifier().equals("cubicore:color")) return;
                    event.getPlayer().setTag(
                            colorTag,
                            Integer.parseInt(
                                    event.getMessageString(), 16
                            )
                    );
                }
        );
        MinecraftServer.getGlobalEventHandler().addListener(
                PlayerChatEvent.class,
                this::chat
        );
    }

    private void chat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        boolean isLocal = cubicore.getConfig().isDisableGlobalChat() ||
                !event.getMessage().startsWith("!");
        if (isLocal) {
            event
                    .getRecipients()
                    .removeIf(another -> {
                        if (another.getInstance() != player.getInstance()) return true;
                        Pos delta = another
                                .getPosition()
                                .sub(player.getPosition());
                        return Math.abs(delta.x()) > cubicore.getConfig().getLocalRadius() &&
                                Math.abs(delta.z()) > cubicore.getConfig().getLocalRadius();
                    });
        }
        else {
            event.setMessage(event.getMessage().substring(1));
            int index = 0;
            for (char c: event.getMessage().toCharArray()) {
                if (c != ' ') break;
                index++;
            }
            if (index == event.getMessage().length()) {
                event.setCancelled(true);
                return;
            }
            event.setMessage(event.getMessage().substring(index));
        }
        Component builtMessage = Component.empty()
                .append(player
                        .getName()
                        .hoverEvent(player)
                )
                .append(Component
                        .text(" > ")
                        .decorate(TextDecoration.BOLD)
                        .color(player.hasTag(colorTag) ?
                                TextColor.color(player.getTag(colorTag)) :
                                NamedTextColor.DARK_GRAY
                        )
                )
                .append(Component.text(event.getMessage()));
        ;
        event.setChatFormat(it -> builtMessage);
    }
}
