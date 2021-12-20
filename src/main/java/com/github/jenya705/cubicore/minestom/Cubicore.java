package com.github.jenya705.cubicore.minestom;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import java.io.File;

/**
 * @author Jenya705
 */
public class Cubicore extends Extension {

    @Getter
    private CubicoreConfig config;

    private ChatImplementation chatImplementation;

    @Override
    public void initialize() {
        getDataDirectory().toFile().mkdirs();
        config = CubicoreConfig.load(new File(getDataDirectory().toFile(), "config.json"));
        MinecraftServer.getCommandManager().register(new GamemodeCommand());
        chatImplementation = new ChatImplementation(this);
    }

    @Override
    public void terminate() {

    }
}
