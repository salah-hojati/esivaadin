package com.example.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;

public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public static void main(String[] args) {
        // Database connection details
        String jdbcUrl = "jdbc:mysql://localhost:3306/productdb?useSSL=false&allowPublicKeyRetrieval=true";
        String username = "salah";
        String password = "1234";

        // Create a DataSource
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Ensure you have the correct driver class name
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        ResourceLoader resourceLoader = new org.springframework.core.io.DefaultResourceLoader();
        for (int i = 0; i < 5; i++) {
            seedFromSqlFiles(dataSource, resourceLoader);
        }
    }
    private static void seedFromSqlFiles(DriverManagerDataSource dataSource, ResourceLoader resourceLoader) {
        logger.info("Scanning for SQL seed files...");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            Resource[] resources = resolver.getResources("classpath:*.sql");

            // Sort resources by filename to control execution order
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename));

            if (resources.length == 0) {
                logger.info("No SQL seed files found.");
                return;
            }

            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) { // Create a statement here

                for (Resource resource : resources) {
                    if (resource.getFilename() != null && resource.getFilename().equalsIgnoreCase("readme.sql")) {
                        logger.info("Skipping non-executable file: {}", resource.getFilename());
                        continue;
                    }
                    logger.info("Executing SQL script: {}", resource.getFilename());

                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(resource.getInputStream()))) {
                        String line;
                        StringBuilder sqlStatement = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty() || line.startsWith("--")) {
                                continue; // Skip comments and empty lines
                            }
                            sqlStatement.append(line);
                            if (line.endsWith(";")) {
                                String executableStatement = sqlStatement.toString();
                                try {
                                    statement.execute(executableStatement);
                                    logger.info("Executed SQL: " + executableStatement);
                                } catch (SQLException e) {
                                    logger.error("Error executing SQL: " + executableStatement, e);
                                } finally {
                                    sqlStatement.setLength(0); // Clear the StringBuilder
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Error reading SQL script: {}", resource.getFilename(), e);
                    }
                }
                logger.info("Finished executing all SQL seed files.");

            } catch (SQLException e) {
                logger.error("Failed to get SQL connection for seeding.", e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void seedFromSqlFiles1(DriverManagerDataSource dataSource, ResourceLoader resourceLoader) {
        logger.info("Scanning for SQL seed files...");
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            Resource[] resources = resolver.getResources("classpath:*.sql");

            // Sort resources by filename to control execution order
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename));

            if (resources.length == 0) {
                logger.info("No SQL seed files found.");
                return;
            }

            try (Connection connection = dataSource.getConnection()) {
                for (Resource resource : resources) {
                    // Your readme.sql file contains text, not SQL. We must skip it.
                    if (resource.getFilename() != null && resource.getFilename().equalsIgnoreCase("readme.sql")) {
                        logger.info("Skipping non-executable file: {}", resource.getFilename());
                        continue;
                    }
                    logger.info("Executing SQL script: {}", resource.getFilename());
                    try {
                        ScriptUtils.executeSqlScript(connection, resource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            logger.info("Finished executing all SQL seed files.");

        } catch (IOException e) {
            logger.error("Failed to find or read SQL seed files.", e);
        } catch (SQLException e) {
            logger.error("Failed to get SQL connection for seeding.", e);
        }
    }
}