package com.github.jenya705.cubicore.minestom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Cleanup;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * @author Jenya705
 */
@Data
public class CubicoreConfig {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private boolean disableGlobalChat = false;

    private int localRadius = 25;

    @SneakyThrows
    public static CubicoreConfig load(File file) {
        if (!file.exists()) {
            CubicoreConfig config = new CubicoreConfig();
            file.createNewFile();
            Files.writeString(
                    file.toPath(),
                    gson.toJson(config),
                    StandardOpenOption.CREATE
            );
            return config;
        }
        @Cleanup Reader reader = new FileReader(file);
        return gson.fromJson(reader, CubicoreConfig.class);
    }


}
