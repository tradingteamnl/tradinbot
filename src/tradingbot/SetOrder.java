package tradingbot;

import bittrex.BittrexProtocall;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mysql.Mysqlconnector;
import org.json.JSONArray;

/**
 *
 * @author michel
 */
public class SetOrder {

	//great object
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    BittrexProtocall bittrexprotocall = new BittrexProtocall();

    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();

    public void orderSystem (){





    }

    private void/*JSONArray*/ orderRoom () throws SQLException{

    	//zorgd voor mysql verbinding
    	Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();

        //sql
        String beginSQL = "SELECT * FROM ordersetting";
        ResultSet rs = stmt.executeQuery(beginSQL);
        while (rs.next()) {
        }
    }

    private void balanceDB (){
        
                //variable die straks gevult worden als er door de response query data
        String exchange;
        String markt;
        double balance;
        double available;
        double pending;
/*
        //sql
        String beginSQL = "SELECT * FROM balance";
        ResultSet rs = stmt.executeQuery(beginSQL);
        while (rs.next()) {
            exchange.getString("exchange");
            markt.getString("markt");
            balance.getDouble("balance");
            available.getDouble("available");
            pending.getDouble("pending");   
        }*/
    };
}