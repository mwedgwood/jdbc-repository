package com.github.mwedgwood.db;

import com.github.mwedgwood.util.PropertyReader;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    private final Map<String, DataSource> dataSources;
    private final PropertyReader reader;


    private static class SingletonHolder {
        private static final DataSourceFactory INSTANCE = new DataSourceFactory();
    }

    private DataSourceFactory() {
        dataSources = new ConcurrentHashMap<>();
        reader = PropertyReader.getInstance();
    }

    public static DataSourceFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public DataSource getDataSource() {
        return getDataSource(reader.getProperty("db.host"), reader.getProperty("db.dbName"), reader.getProperty("db.user"), reader.getProperty("db.password"));
    }

    public DataSource getDataSource(String jdbcHost, String dbName, String dbUser, String dbPassword) {
        LOGGER.debug("Using datasource: " + dbUser + "@" + jdbcHost + "/" + dbName);
        return getDataSource(jdbcHost + "/" + dbName, dbUser, dbPassword);
    }

    DataSource getDataSource(String jdbcUrl, String dbUser, String dbPassword) {
        String dataSourceKey = createKey(jdbcUrl, dbUser);
        if (dataSources.get(dataSourceKey) == null) {
            dataSources.put(dataSourceKey, createDataSource(jdbcUrl, dbUser, dbPassword));
        }
        return dataSources.get(dataSourceKey);
    }

    String createKey(String jdbcUrl, String dbUser) {
        return dbUser + "@" + jdbcUrl;
    }

    DataSource createDataSource(String jdbcUrl, String dbUser, String dbPassword) {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setNumHelperThreads(10);
        dataSource.setInitialPoolSize(2);
        dataSource.setMinPoolSize(2);
        dataSource.setMaxPoolSize(10);
        dataSource.setMaxStatements(50);

        return dataSource;
    }

}
