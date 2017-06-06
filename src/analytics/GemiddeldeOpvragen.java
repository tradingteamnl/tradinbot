/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analytics;

import java.sql.Array;
import java.sql.ResultSet;
import mysql.MysqlSql;
import mysql.Mysqlconnector;

/**
 *
 * @author Jaros
 */
public class GemiddeldeOpvragen {

    MysqlSql mysqlSql = new MysqlSql();

    public double[] gemiddideldeOpvragenDag() {
        double gegevens[] = new double[3];

        try {
            String query = "";
            ResultSet rs1 = mysqlSql.mysqlSelectStament(query);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return gegevens;
    }

}
