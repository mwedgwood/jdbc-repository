package com.github.mwedgwood.util;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyReader {

    private static final String PROPERTIES_FILE_PATH = "DB_PROPERTIES";

    private final Properties _properties = new Properties();

    private static class SingletonHolder {
        private static final PropertyReader INSANCE = new PropertyReader();
    }

    private PropertyReader() {
        String propertyFilePath = System.getProperty(PROPERTIES_FILE_PATH) != null ?
                System.getProperty(PROPERTIES_FILE_PATH) :
                "/Users/mwedgwood/Ares/repos/prototype/jdbc_repository/src/main/resources/db.properties";

        File file = Paths.get(propertyFilePath).toFile();

        try (FileReader fileReader = new FileReader(file)) {
            _properties.load(fileReader);
        } catch (Exception e) {
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    public static PropertyReader getInstance() {
        return SingletonHolder.INSANCE;
    }

    public String getProperty(String name) {
        return _properties.getProperty(name);
    }
}
