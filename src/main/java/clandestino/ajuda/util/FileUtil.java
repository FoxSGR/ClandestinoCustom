package clandestino.ajuda.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    /**
     * Reads the entire content from a file.
     *
     * @return the content read from the file.
     */
    public static String contentFromFile(URI uri) throws IOException {
        return contentFromFile(Paths.get(uri));
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
}
