package core.OrderSysteem;

import bittrex.BittrexProtocall;
import http.Http;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mysql.MysqlSql;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class OrderStatus {

    //array list voor lijst order status
    private final int ORDERLIST[];

    //objecten
    MysqlSql mysqlSql = new MysqlSql();
    Http http = new Http();
    BittrexProtocall bittrexProtocall = new BittrexProtocall();
    OrderDBSql orderDBSql = new OrderDBSql();

    //constructor
    public OrderStatus() {
        this.ORDERLIST = new int[]{1, 2, 3};
    }

    //methoden die orderStatus aanstuurt
    public void run() {

        //loop diie de prio lijsten heen
        for (int i = 0; i < ORDERLIST.length; i++) {

            int prio = ORDERLIST[i];

            try {
                //kijk of er iets van bittrex order staat
                /*try {
                ResultSet rs = mysqlSql.mysqlSelectStament("SELECT COUNT(*) AS total FROM openOrders WHERE prioritijd='" + prio + "'");
                while (rs.next()) {
                if (rs.getInt("total") != 0) {
                
                //call bittrex methoden
                try {
                bittrex();
                } catch (SQLException ex) {
                System.err.println("Probleem in de bittrex methoden in OrderStatus.");
                }
                } else {
                System.out.println("Er zijn geen open order. Met prioriteit "+ prio+".");
                }
                
                }
                } catch (SQLException ex) {
                System.err.println("Probleem bij orderID gevens op te vragen bij markt bittrex");
                }*/

                //maak jsonObject
                JSONObject object = new JSONObject(bittrexProtocall.getOpenOrders());
                System.out.println(object);

                //maak json array
                JSONArray array = object.getJSONArray("result");

                bittrex(array);
            } catch (SQLException ex) {
                System.err.println("Probleem in de bittrex methoden in OrderStatus.");
            }
        }
    }

    /**
     * Deze methoden laat alles in voor bittrex
     *
     * @throws SQLException als er een fout optreed ergens onderweg
     */
    private void bittrex(JSONArray array) throws SQLException {

        //exchange
        String exchange = "bittrex";

        //loop
        for (int i = 0; i < array.length(); i++) {

            //order
            JSONObject order = array.getJSONObject(i);
            System.out.println(order);
            //krijg alle data er uit
            String uuid = order.getString("OrderUuid");
            
            //string markt word gevuld met bijvoorbeeld BTC-LTC
            String markt = order.getString("Exchange");
            double total = order.getDouble("Quantity");
            double remaning = order.getDouble("QuantityRemaining");
            double prijs = order.getDouble("Price");

            //kijk of de order is aangepast
            String sqlSelect1 = "SELECT COUNT(*) AS total FROM openOrders"
                    + " WHERE uuid='" + uuid + "' AND total='" + total + " AND remaning='" + remaning + "' AND exchange='bittrex' AND status='pending'";
            ResultSet rs = mysqlSql.mysqlSelectStament(sqlSelect1);

            //kijk of het 0 of 1 is
            int count;
            while (rs.next()) {
                count = rs.getInt("total");
                if (count == 1) {
                    System.out.println("Order " + uuid + " op Bittrex is nog up to date.");

                    //update timestamp
                    orderDBSql.updateTimeStamp(uuid, exchange);
                } else if (count == 0) {

                    //hier word gekeken of de order in de lijst staat en zo ja dan worden database velden bijgewerkt
                    String sqlSelect2 = "SELECT COUNT(*) AS total FROM openOrders WHERE uuid='" + uuid + "' AND exchange='bittrex' AND status='pending'";
                    ResultSet rs1 = mysqlSql.mysqlSelectStament(sqlSelect2);

                    int count1;
                    while (rs1.next()) {
                        count1 = rs1.getInt("total");

                        //als het 0 is
                        if (count1 == 0) {

                            //voeg order to in het database
                            orderDBSql.bittrexAddOrder(uuid, markt, total, remaning, prijs, i);

                        } else if (count1 == 1) {

                            //update order
                            orderDBSql.bittrexUpdateOrder(uuid, remaning);

                        } else {
                            System.err.println("Er us een probleem bij OrderStatus. De uuid nummer van bittrex komt meedere keren voor in het database.");
                        }
                    }

                } else {
                    System.err.println("Er is een bug in de if loop (core/OrderSysteem/OrderStatus.java).");
                }
            }
        }

    }
}
