package com.github.jenya705.cubicore.minestom;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author Jenya705
 */
public class GamemodeCommand extends Command {

    enum Gamemode {
        survival,
        creative,
        adventure,
        spectator
    }

    private static final String permission = "minecraft.command.gamemode.%s";

    public GamemodeCommand(@NotNull String name, String... aliases) {
        super(name, aliases);
        ArgumentEnum<Gamemode> gamemodeArgument = ArgumentType.Enum("gamemode", Gamemode.class);
        ArgumentEntity entityArgument = ArgumentType
                .Entity("entity")
                .onlyPlayers(true)
                .singleEntity(false);
        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Specify entities you want to change gamemode or send this command as the player");
                return;
            }
            GameMode gameMode = castGamemode(context.get(gamemodeArgument));
            String loweredGameMode = gameMode.name().toLowerCase(Locale.ROOT);
            if (!hasPermission(player, loweredGameMode)) {
                return;
            }
            player.setGameMode(gameMode);
            player.sendMessage(successSelf(loweredGameMode));
        }, gamemodeArgument);

        addSyntax((sender, context) -> {
            EntityFinder entityFinder = context.get(entityArgument);
            GameMode gameMode = castGamemode(context.get(gamemodeArgument));
            String loweredGameMode = gameMode.name().toLowerCase(Locale.ROOT);
            if (!hasPermission(sender, loweredGameMode)) {
                return;
            }
            entityFinder
                    .find(sender)
                    .stream()
                    .filter(it -> it instanceof Player)
                    .forEach(it -> {
                        Player player = (Player) it;
                        player.setGameMode(gameMode);
                        if (sender == player) {
                            sender.sendMessage(successSelf(loweredGameMode));
                        }
                        else {
                            sender.sendMessage(successOther(loweredGameMode, player.getName()));
                        }
                    })
            ;
        }, gamemodeArgument, entityArgument);
    }

    private boolean hasPermission(CommandSender sender, String gamemode) {
        return (!(sender instanceof Player) || ((Player) sender).getPermissionLevel() > 2) ||
                sender.hasPermission(permission.formatted(gamemode));
    }

    private GameMode castGamemode(Gamemode gamemode) {
        return GameMode.valueOf(gamemode.name().toUpperCase(Locale.ROOT));
    }

    private Component successSelf(String gamemode) {
        return Component.translatable(
                "commands.gamemode.success.self",
                gamemodeComponent(gamemode)
        );
    }

    private Component successOther(String gamemode, Component playerName) {
        return Component.translatable(
                "commands.gamemode.success.other",
                playerName, gamemodeComponent(gamemode)
        );
    }

    private Component gamemodeComponent(String gamemode) {
        return Component.translatable("gameMode." + gamemode);
    }

    public GamemodeCommand() {
        this("gamemode", "gm");
    }
}
