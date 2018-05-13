package com.jilfond.bot;

import java.sql.*;
import java.util.LinkedList;

public class Database {
    private Connection connection;
    private final String databaseFileName = "apartments.db";


    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFileName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public LinkedList<Apartment> getApartments(Integer id) throws SQLException {
        String sql = "SELECT * FROM apartments"+
                     "WHERE id = "+Integer.toString(id);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        LinkedList<Apartment> apartmentLinkedList = new LinkedList<>();
        while (resultSet.next()){
            String street = resultSet.getString("street");
            Integer number = resultSet.getInt("number");
            Integer price = resultSet.getInt("price");
            apartmentLinkedList.add(new Apartment(street,number,price,new Seller()));//TODO:replace new Seller with my logic
        }
        return apartmentLinkedList;
    }

    public void addApartment(Apartment apartment){
        Statement statement = null;
        try {
            statement = connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addSeller(){

    }
}
