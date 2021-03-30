package com.sipp.dao.sql;

import com.sipp.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class DataSourceSupplier {

    private static DataSource ds;

    public static DataSource get() {
        if (ds == null) {
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            Properties databaseProperties = new Properties();
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(AppProperties.getProperty("dbPath")))) {
                databaseProperties.load(inputStream);
                log.info("Database properties loaded. Size: " + databaseProperties.size());
                dataSource.setUrl(databaseProperties.getProperty("connectionString"));
                dataSource.setUser(databaseProperties.getProperty("username"));
                dataSource.setPassword(databaseProperties.getProperty("password"));
            } catch (IOException e) {
                log.error("Unable to load DataSource properties file and configure the DataSource. DataSource will be configured with the default settings.");
                dataSource.setUrl(AppProperties.getProperty("defaultConnectionString"));
            }
            ds = dataSource;
        }
        return ds;
    }
}
