package com.github.novskey.novabot.data;

import org.apache.commons.dbcp2.BasicDataSource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private BasicDataSource ds;


    private DataSource(String driver, String user, String pass, String url, int maxConnections) throws IOException, SQLException, PropertyVetoException {
        ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUsername(user);
        ds.setPassword(pass);
        ds.setUrl(url);
        ds.setMaxTotal(maxConnections);
    }

    public static void main(String[] args) {
        try {
            DataSource source = DataSource.getInstance("org.postgresql.Driver","postgres","mimi","jdbc:postgresql://127.0.0.1:5432/monocle",8);

            for (int i = 0; i < 1000; i++) {
                System.out.println("Total threads before: " + ManagementFactory.getThreadMXBean().getThreadCount());

                new Thread(() -> {
                    try (Connection connection = source.getConnection()){
                        System.out.println("Total threads acquired connection: " + ManagementFactory.getThreadMXBean().getThreadCount());
                        System.out.println(connection.isValid(50));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (IOException | SQLException | PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getInstance(String driver, String user, String pass, String url, int maxConnections) throws IOException, SQLException, PropertyVetoException {
        return new DataSource(driver,user,pass,url, maxConnections);
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

}