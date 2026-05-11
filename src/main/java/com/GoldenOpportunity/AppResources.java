package com.GoldenOpportunity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class AppResources {
    private static final String APP_DIR_NAME = "GoldenOpportunity";
    private static final String DATA_DIR_PROPERTY = "goldenopportunity.data.dir";

    private AppResources() {
    }

    public static BufferedImage readImage(String path) throws IOException {
        Path filePath = Path.of(path);
        if (Files.exists(filePath)) {
            return ImageIO.read(filePath.toFile());
        }

        try (InputStream stream = openResource(path)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IOException("Unsupported image resource: " + path);
            }
            return image;
        }
    }

    public static String readText(String path) throws IOException {
        Path filePath = Path.of(path);
        if (Files.exists(filePath)) {
            return Files.readString(filePath);
        }

        try (InputStream stream = openResource(path)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static Path getAppDataDirectory() {
        String override = System.getProperty(DATA_DIR_PROPERTY);
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        Path home = Path.of(System.getProperty("user.home"));

        if (osName.contains("mac")) {
            return home.resolve("." + APP_DIR_NAME.toLowerCase(Locale.ROOT));
        }
        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return Path.of(appData).resolve(APP_DIR_NAME);
            }
            return home.resolve("AppData/Roaming").resolve(APP_DIR_NAME);
        }
        return home.resolve("." + APP_DIR_NAME.toLowerCase(Locale.ROOT));
    }

    public static Path resolveAppDataPath(String fileName) {
        return getAppDataDirectory().resolve(fileName);
    }

    public static String toJdbcUrl(String fileName) {
        return "jdbc:sqlite:" + resolveAppDataPath(fileName);
    }

    private static InputStream openResource(String path) throws IOException {
        String resourcePath = normalizeResourcePath(path);
        InputStream stream = AppResources.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IOException("Resource not found: " + path);
        }
        return stream;
    }

    private static String normalizeResourcePath(String path) {
        if (path.startsWith("src/main/resources/")) {
            return "/" + path.substring("src/main/resources/".length());
        }
        if (path.startsWith("src/main/java/")) {
            return "/" + path.substring("src/main/java/".length());
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
