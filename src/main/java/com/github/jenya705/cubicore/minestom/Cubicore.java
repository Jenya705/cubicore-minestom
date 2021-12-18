package com.github.jenya705.cubicore.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

/**
 * @author Jenya705
 */
public class Cubicore extends Extension {

    @Override
    public void initialize() {
        MinecraftServer.getCommandManager().register(new GamemodeCommand());
    }

    @Override
    public void terminate() {

    }
}
