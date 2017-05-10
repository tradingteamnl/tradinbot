package core.OrderSysteem;

import http.Http;
import java.sql.ResultSet;
import java.sql.SQLException;
import mysql.MysqlSql;

public class OrderStatus {

    //array list voor lijst order status
    private final int ORDERLIST[];

    //objecten
    MysqlSql mysqlSql = new MysqlSql();
    Http http = new Http();

    //constructor
    public OrderStatus() {
        this.ORDERLIST = new int[]{1, 2, 3};
    }

    public void orderList() {

        //loop door alle prioriteiten heen
        for (int i = 0; i < ORDERLIST.length; i++) {

            //if loop
            if (dbCheck(i) == true) {
                try {
                    //SQL string
                    String getActiveOrders = "SELECT * FROM orderID WHERE status='active'";

                    //vraag active orders op
                    ResultSet resultsetActiveOrder = mysqlSql.mysqlSelectStament(getActiveOrders);

                    //loop
                    while (resultsetActiveOrder.next()) {

                    }
                } catch (SQLException ex) {
                    System.err.println("Er is een probleem in orderStatus.java");
                }
            }
        }
    }

    /**
     *
     * @param prio
     * @return return of er regels beschikbaar zijn met die prio
     */
    private boolean dbCheck(int prio) {

        try {
            //vraag active orders op
            ResultSet resultsetActiveOrder = mysqlSql.mysqlSelectStament("SELECT COUNT(*) AS total FROM orderID WHERE status='active' AND prioritijd='" + prio + "'");
            while (resultsetActiveOrder.next()) {
                if (resultsetActiveOrder.getInt("total") == 0) {
                    //Er zijn geen orders
                    System.out.println("Er zijn geen active order met prioritijd " + prio + ".");
                    return false;
                } else {
                    //Er zijn orders die open staan
                    System.out.println("Er zijn orders die open staan.");
                    return true;
                }
            }

            //als er helemaal geen andere optie zijn (kut netbeans)
            return false;
        } catch (SQLException ex) {
            System.err.println("Er is een probleem in DBCheck.");
            return false;
        }
    }

}
