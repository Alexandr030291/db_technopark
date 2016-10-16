package org.ebitbucket.sqlConnection;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Simple {

    private static Connection getConnection(){
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:mysql://").            //db type
                    append("localhost:").               //host name
                    append("3306/").                    //port
                    append("db_technopark").            //db name
                    append("user=root&").               //login
                    append("password=DontSmoke030291"); //password

            System.out.append("Url: ").append(url).append("\n");

            return DriverManager.getConnection(url.toString());
        }catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void connect(){
        Connection connection = getConnection();
        System.out.append("Connected!\n");
        try {
            assert connection != null;
            System.out.append("Autocommit: ").append(String.valueOf(connection.getAutoCommit())).append('\n');
            System.out.append("DB name: ").append(connection.getMetaData().getDatabaseProductName()).append('\n');
            System.out.append("DB version: ").append(connection.getMetaData().getDatabaseProductVersion()).append('\n');
            System.out.append("Driver name: ").append(connection.getMetaData().getDriverName()).append('\n');
            System.out.append("Driver version: ").append(connection.getMetaData().getDriverVersion()).append('\n');
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
}
