package com.github.mwedgwood.util;

import java.io.FileReader;
import java.net.URL;
import java.util.Properties;

public class PropertyReader {

    private final Properties properties = new Properties();

    private static class SingletonHolder {
        private static final PropertyReader INSTANCE = new PropertyReader();
    }

    private PropertyReader() {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("db.properties");

        try (FileReader fileReader = new FileReader(resource.getFile())) {
            properties.load(fileReader);
        } catch (Exception e) {
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    public static PropertyReader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }
}
