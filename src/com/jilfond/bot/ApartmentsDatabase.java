package com.jilfond.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ApartmentsDatabase {
    private Connection connection;
    private final String databaseFileName = "database.s3db";

    private void createMainTableIfNotExist() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS apartments (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	street text NOT NULL\n"
                + ");";
        Statement stmt = connection.createStatement();
        // create a new table
        stmt.execute(sql);
    }


    public ApartmentsDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFileName);
            String catalog = connection.getCatalog();
            createMainTableIfNotExist();
            System.out.println(catalog);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("ApartmentsDatabase was created");
    }


}
