package clandestino.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * File related useful methods.
 */
@SuppressWarnings({"unused"})
public final class FileUtil {

    /**
     * Private constructor to hide implicit public one.
     */
    private FileUtil() {
        // Should be empty.
    }

    public static String contentFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\Z");
            return scanner.next();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads the entire content from a file.
     *
     * @return the content read from the file.
     */
    public static String contentFromFile(URI uri) {
        try {
            return contentFromFile(Paths.get(uri));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads the entire content from a file.
     *
     * @return the content read from the file.
     */
    public static String contentFromFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    public static String withoutExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        }

        return fileName.substring(0, index);
    }

    public static void saveConfig(FileConfiguration fileConfiguration, File file) {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save the configuration file " + file.getName(), e);
        }
    }

    public static void loadConfig(FileConfiguration fileConfiguration, File file) {
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Could not load the configuration file " + file.getName(), e);
        }
    }
}
