package bittrex;

import global.Time;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
            String coinTag = balanceArray.getJSONObject(i).getString("Currency");
            double pending = balanceArray.getJSONObject(i).getDouble("Pending");
            double balance = balanceArray.getJSONObject(i).getDouble("Balance");
            double available = balanceArray.getJSONObject(i).getDouble("Available");
            
            BalanceMysql(coinTag, pending, available, balance);
        }
    }
    
    /**
     * 
     * @param coinTag
     * @param pending
     * @param available
     * @param balance
     * @throws SQLException 
     */
    private void BalanceMysql(String coinTag, double pending, double available, double balance) throws SQLException {
        
        //zorgd voor mysql verbinding
        Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();

        //SQL count
        String SQLCount = "SELECT COUNT(*) AS total FROM balance WHERE handelsplaats='bittrex' AND cointag='" + coinTag + "' AND "
                + "balance=" + balance + " AND pending=" + pending + " AND available=" + available + "";

        ResultSet rs = stmt.executeQuery(SQLCount);

        int count = 0;
        while (rs.next()) {
            count = rs.getInt("total");
        }

        if (count == 0) {
            String SQLCount2 = "SELECT COUNT(*) AS total FROM balance WHERE handelsplaats='bittrex' AND cointag='" + coinTag + "';";
            ResultSet rs1 = stmt.executeQuery(SQLCount2);
            int count2 = 0;
            while (rs1.next()) {
                count2 = rs1.getInt("total");
            }
            
            if (count2 == 1) {

                String SQLUpdate = "UPDATE balance SET balance=" + balance + ", pending=" + pending + ", available=" + available + ""
                        + " WHERE handelsplaats='bittrex' AND cointag='" + coinTag + "';";
                System.out.println(SQLUpdate);
                stmt.execute(SQLUpdate);
            } else {
                String SQLQuery = "INSERT INTO balance (handelsplaats, cointag, balance, available, pending)"
                        + " VALUES ('bittrex', '" + coinTag + "', " + balance + ", " + available + "," + pending + ");";
                stmt.execute(SQLQuery);
                System.out.println(SQLQuery);
            }
        } else {
        }

    }
}
