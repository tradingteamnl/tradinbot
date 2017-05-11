package core.OrderSysteem;

import global.Time;
import mysql.MysqlSql;

/**
 *
 * @author michel
 */
public class OrderDBSql {

    //maak object aan
    MysqlSql mysqlSql = new MysqlSql();
    Time timeModule = new Time();

    /**
     *
     * @param orderID unieke code
     * @param markt bijvoorbeeld BTC-LTC
     * @param totaal hoeveel je er aan koopt
     * @param remaning hoeveel je er nog moet doen
     * @param prijs de prijs
     * @param prio de snelheid
     */
    public void bittrexAddOrder(String orderID, String markt, double totaal, double remaning, double prijs, int prio) {

        //variable
        String status;
        int timeStamp = timeModule.getTimeStamp();

        //om de juiste status in het db te zetten
        if (remaning != 0) {
            status = "pending";
        } else {
            status = "compleet";
        }

        //kijk of het goed is gegaan
        String addSql = "INSERT INTO orderID (markt, orderID, totaal, remaning, prijs, exchange, prioritijd, status, updateTime)"
                + "VALUES ('" + markt + "', '" + orderID + "', '" + totaal + "', '" + remaning + "', '" + prijs + "', 'bittrex', '" + prio + "', '" + status + "', " + timeStamp + "');";
        boolean mysqlResponse = mysqlSql.mysqlStament(addSql);

        //kijk op het goed is gegaan
        if (mysqlResponse == true) {
            System.out.println("Order " + orderID + " op exchange is de timestamp bijgewerkt.");
        } else {
            System.err.println("Er is een error bij timestamp updaten bij bittrex. Order id is " + orderID + ".");
        }
    }

    /**
     *
     * @param orderID unieke order id
     * @param remaning hoeveel coins er nog moet worden
     */
    public void bittrexUpdateOrder(String orderID, double remaning) {

        //timestamp
        int timeStamp = timeModule.getTimeStamp();

        //exchange
        String exchange = "bittrex";

        //Update String
        String updateString = "UPDATE set remaning='" + remaning + "', updateTime='" + timeStamp + "'";

        if (remaning != 0) {
            updateString += ", status='pending'";
        } else {
            updateString += ", status='compleet'";
        }

        //add WHERE in de sql string
        updateString += " WHERE exchange='" + exchange + "' AND orderID='" + orderID + "';";

        //run sql command
        boolean mysqlResponse = mysqlSql.mysqlStament(updateString);

        //kijk op het goed is gegaan
        if (mysqlResponse == true) {
            System.out.println("Order " + orderID + " op " + exchange + " is de timestamp bijgewerkt.");
        } else {
            System.err.println("Er is een error bij timestamp updaten bij " + exchange + ". Order id is " + orderID);
        }
    }

    /**
     * Update timstamp methoden
     *
     * @param orderID het id nummer van de exchange
     * @param exchange exchange naam
     */
    public void updateTimeStamp(String orderID, String exchange) {

        //krijg de timestamp
        int timeStamp = timeModule.getTimeStamp();

        //mysql string en de response
        String updateSql = "UPDATE set updateTime='" + timeStamp + "' WHERE orderID='" + orderID + "' AND exchange='" + exchange + "'";
        boolean mysqlResponse = mysqlSql.mysqlStament(updateSql);

        //kijk op het goed is gegaan
        if (mysqlResponse == true) {
            System.out.println("Order " + orderID + " op " + exchange + " is de timestamp bijgewerkt.");
        } else {
            System.err.println("Er is een error bij timestamp updaten bij " + exchange + ". Order id is " + orderID);
        }
    }
}
