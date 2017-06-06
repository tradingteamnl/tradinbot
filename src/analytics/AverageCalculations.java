/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analytics;

import java.sql.Array;
import java.sql.Connection;
import java.util.Calendar;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import mysql.MysqlSql;
import mysql.Mysqlconnector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Jaros
 */
public class AverageCalculations {

    
    MysqlSql mysqlSql = new MysqlSql();

    public void invullenGemiddeldenDagen(String markt, String exchange) {

        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);

        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);

        try {
            String query = "SELECT COUNT(*) AS total FROM maandid where maand = '" + month + "' and jaar = ' " + year + "';";    //controle of maand al bestaat
            ResultSet rs1 = mysqlSql.mysqlSelectStament(query);
            int count = 0;
            while (rs1.next()) {
                count = rs1.getInt("total");
            }

            if (count == 0) {                                                                                                      //maken  nieuwe maand in maandid
                String query1 = "insert into maandid (maand, jaar) Values ('" + month + "', '" + year + "')";
                mysqlSql.mysqlStament(query1);

            }
            String query2 = "SELECT id FROM maandid where = '" + month + "' and jaar = ' " + year + "';";                           //selecteren maandid uit database
            ResultSet rs2 = mysqlSql.mysqlSelectStament(query2);
            int id = 0;
            while (rs2.next()) {
                id = rs2.getInt("id");
            }
            if (count == 0) {                                                                                                         //aanmaken maand in db maand als maand nog niet heeft bestaan
                String query3 = "insert into maand(maandid, makrt, exchange, volledigIngevuld) Valujes"
                        + "('" + id + "', " + markt + "', " + exchange + "', 0";
                mysqlSql.mysqlStament(query3);
            }

            if (id != 0) {                                                                                                          //invullen dagen
                dagen(id);
            } else {
                System.out.println("probleem met het vinden van de maand in averageCalqulations\n");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dagen(int id) {
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        try {

            String query = "SELECT idDag FROM dagen Where dag = '" + day + "' and idMaand = '" + id + "';";
            ResultSet rs1 = mysqlSql.mysqlSelectStament(query);

            int idDag = 0;
            while (rs1.next()) {
                idDag = rs1.getInt("idDag");
            }

            if (idDag != 0) {
                double[] gemiddelden = new double[3];                   //opvragen gemiddelden van dag
                gemiddelden = gemiddideldeOpvragen();
                double gemiddeldedag = gemiddelden[0];
                double gBidPrijs = gemiddelden[1];
                double gAskPrijs = gemiddelden[2];

                String query1 = "Insert into dagen (idMaand, gemiddeldedag, gBidPrijs, gAskPrijs, dag)"
                        + " Values ( '" + id + "', " + gemiddeldedag + "', '" + gBidPrijs + "', '" + gAskPrijs + "', " + day + "', '" + day + "');";

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    
}
