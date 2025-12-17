package com.voting.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv().getOrDefault("DATABASE_URL", "jdbc:mysql://localhost:3306/voting_db"));
        config.setUsername(System.getenv().getOrDefault("DB_USER", "root"));
        config.setPassword(System.getenv().getOrDefault("DB_PASSWORD", "password"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
