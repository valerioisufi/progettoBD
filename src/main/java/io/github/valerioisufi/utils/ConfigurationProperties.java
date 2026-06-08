package io.github.valerioisufi.utils;

import io.github.valerioisufi.exception.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationProperties {
    private final Properties properties = new Properties();

    private static class Wrapper {
        static final ConfigurationProperties INSTANCE = new ConfigurationProperties();
    }

    private ConfigurationProperties() {
        loadProperties();
    }

    public static ConfigurationProperties getInstance() {
        return Wrapper.INSTANCE;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new ConfigException("Impossibile trovare config.properties nel classpath");
            }

            properties.load(input);

        } catch (IOException ex) {
            throw new ConfigException("Errore durante la lettura di config.properties");
        }

    }

    public String getDbUrl() {
        String url = properties.getProperty("db.url");

        if (url == null || url.isBlank()) {
            throw new ConfigException("La proprietà 'api.url' è mancante o vuota in config.properties");
        }

        return url;
    }

    public String getDbUser(String dbUser) {
        String user = properties.getProperty(dbUser + ".user");

        if (user == null || user.isBlank()) {
            throw new ConfigException("La proprietà '" + dbUser + ".user' è mancante o vuota in config.properties");
        }

        return user;
    }

    public String getDbPass(String dbUser) {
        String pass = properties.getProperty(dbUser + ".pass");

        if (pass == null || pass.isBlank()) {
            throw new ConfigException("La proprietà '" + dbUser + ".pass' è mancante o vuota in config.properties");
        }

        return pass;
    }
}
