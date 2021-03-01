package javapractice;

import java.sql.*;
import java.util.Enumeration;
public class DBDemo {
    public static void main(String[] args)  {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String password = "root";
        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the class path!", e);
        }
        listDrivers();

        try {
            System.out.println("Connecting to Database: "+jdbcURL);
            connection = DriverManager.getConnection(jdbcURL, userName, password);

            System.out.println("Connection is successful...." +connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements())
        {
            Driver driverClass = (Driver) driverList.nextElement();
            System.out.println(" "+driverClass.getClass().getName());
        }
    }
}
