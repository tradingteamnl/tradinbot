/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import mysql.Mysqlconnector;

/**
 *
 * @author michel
 */
public class DatumCheck {

    //maak object aan
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    Time time = new Time();

    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();
    private int datumInDatabase;
    private int datumIddatum;

    public int datumCheck() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Statement stmt = (Statement) conn.createStatement();
            String datumCheck = "SELECT COUNT(*) AS total FROM datum WHERE Datum='" + time.getData() + "'";
            ResultSet rs2 = stmt.executeQuery(datumCheck);
            while (rs2.next()) {
                this.datumInDatabase = rs2.getInt("total");
            }

            /**
             * Deze funtie kijkt of er een datum beschikbaar is in het database
             */
            if (datumInDatabase == 0) {
                String sql = "INSERT INTO datum (datum)VALUES('" + time.getData() + "')";

                //voer sql uit
                stmt.execute(sql);

                ResultSet rs4 = stmt.executeQuery("SELECT * FROM datum WHERE datum='" + time.getData() + "'");
                while (rs4.next()) {
                    this.datumIddatum = rs4.getInt("Iddatum");
                }
                return datumIddatum;
            } else if (datumInDatabase == 1) {
                ResultSet rs3 = stmt.executeQuery("SELECT * FROM datum WHERE datum='" + time.getData() + "'");
                while (rs3.next()) {
                    this.datumIddatum = rs3.getInt("Iddatum");
                }
                return datumIddatum;
            } else {
                System.out.println("er is ene probleem bij datumCheck request.");
                return 00;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 00;
        }
    }
}
