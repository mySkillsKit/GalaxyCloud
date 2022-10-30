package ru.netology.galaxycloud.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {

    public static final String IMAGE_VERSION = "postgres:14";
    public static final String DATABASE_NAME = "postgres";
    public static PostgreSQLContainer container;

    public PostgresTestContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSQLContainer getInstance() {
        if (container == null) {
            container = new PostgresTestContainer().withDatabaseName(DATABASE_NAME);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("jdbc:postgresql://postgres:5432/postgres", container.getJdbcUrl());
        System.setProperty("postgres", container.getUsername());
        System.setProperty("postgres", container.getPassword());
    }

    @Override
    public void stop() {
    }
}