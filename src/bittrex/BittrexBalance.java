package bittrex;

import global.Time;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import mysql.Mysqlconnector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class BittrexBalance {

    //maak object aan
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    BittrexProtocall bittrexProtocall = new BittrexProtocall();
    Time time = new Time();

    //private
    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();

    //methoden
    public void balance() throws SQLException {

        //zorgd voor mysql verbinding
        Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();
        
        //Hier word result er uit gehaald
        JSONObject obj = new JSONObject(bittrexProtocall.getBalances());
        JSONArray balanceArray = obj.getJSONArray("result");

        for (int i = 0; i < balanceArray.length(); i++) {

            //variable
            String markt = balanceArray.getJSONObject(i).getString("Currency");
            double pending = balanceArray.getJSONObject(i).getDouble("Pending");
            double balance = balanceArray.getJSONObject(i).getDouble("Balance");
            double available = balanceArray.getJSONObject(i).getDouble("Available");

            //SQL
            String SQLQuery = "INSERT INTO balance (exchange, cointag, balance, available, pending)"
                    + " VALUES ('bittrex', '" + markt + "', '" + balance + "', '" + available + "','" + pending + "');";

            stmt.execute(SQLQuery);
        }
    }
}
