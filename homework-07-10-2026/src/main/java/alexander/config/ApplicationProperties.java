package alexander.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class ApplicationProperties {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ApplicationProperties.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException("Файл application.properties не найден");
            }

            PROPERTIES.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать application.properties", e);
        }
    }

    private ApplicationProperties() {
    }

    public static String getRequired(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Не задано свойство: " + key);
        }

        return value.trim();
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        return value == null ? null : value.trim();
    }
}
