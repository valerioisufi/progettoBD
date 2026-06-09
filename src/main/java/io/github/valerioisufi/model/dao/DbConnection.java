package io.github.valerioisufi.model.dao;

import io.github.valerioisufi.exception.ConfigException;
import io.github.valerioisufi.utils.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private final String url;
    private Connection connection;

    private static class Wrapper {
        static final DbConnection INSTANCE = new DbConnection(
                ConfigurationProperties.getInstance().getDbUrl(),
                ConfigurationProperties.getInstance().getDbUser("login"),
                ConfigurationProperties.getInstance().getDbPass("login")
        );
    }

    private DbConnection(String url, String user, String password) {
        this.url = url;

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            logger.error("Errore durante il tentativo di stabilire la connessione", ex);
            System.exit(-1);
        }
    }

    public static DbConnection getInstance() {
        return Wrapper.INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }

    public void changeUser(String user) {
        try {
            connection.close();

            connection = DriverManager.getConnection(
                    url,
                    ConfigurationProperties.getInstance().getDbUser(user),
                    ConfigurationProperties.getInstance().getDbPass(user)
            );

        } catch (SQLException ex) {
            logger.error("Errore durante il tentativo di stabilire la nuova connessione", ex);
            System.exit(-1);
        }
    }

}
