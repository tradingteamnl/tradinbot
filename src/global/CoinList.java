package global;

//import
import mysql.MysqlSql;
import http.Http;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mysql.MysqlError;
import mysql.Mysqlconnector;
import org.json.JSONArray;
import org.json.JSONObject;

public class CoinList {

    //great object
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    MysqlError mysqlerror = new MysqlError();
    Http httprequest = new Http();
    FileSystem filesystem = new FileSystem();

    //private
    private String config;
    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();
    private int countt = 0;

    /**
     *
     */
    public void GetCoinList() {
        this.config = filesystem.readFile("coinlist.txt");
        System.out.println(httprequest.GetHttp(config));
        JSONArray response = new JSONArray(httprequest.GetHttp(config));
        for (int i = 0; i < response.length(); i++) {
            
            /*String coinTag;
            coinTag = response[i].getString("symbol");*/
            JSONObject jsonObject = response.getJSONObject(i);
            String coinTag = jsonObject.getString("symbol");
            System.out.println(jsonObject.getString("symbol"));
            
            //geef coinTag door aan de methode die na kijkt of de coin in de trade database lijst staat
            DBCheck(coinTag);
        }
    }

    private void DBCheck(String coinTag) {

        //Sql String
        String sqlString = "SELECT COUNT(*) AS total FROM coinlijst WHERE cointag='" + coinTag + "';";
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Statement stmt = (Statement) conn.createStatement();

            ResultSet rs1 = stmt.executeQuery(sqlString);
            int count = 0;
            while (rs1.next()) {
                count = rs1.getInt("total");
            }

            switch (count) {
                case 0:
                    String insetSql = "INSERT INTO coinlijst (cointag) VALUES('" + coinTag + "')";
                    stmt.execute(insetSql);
                    System.out.println("Cointag is aan de lijst toegevoegd.");
                    this.countt++;
                    System.out.println(countt);
                    break;
                case 1:
                    System.out.println("Cointag staat al in de lijst");
                    this.countt++;
                    System.out.println(countt);
                    break;
                default:
                    System.err.println("Er is een optie bij DBCheck wat niet kan.");
                    break;
            }

        } catch (SQLException ed) {

            //maak van de error een string
            String message = "" + ed;

            //stuur de info door naar error afhandel systeem
            mysqlerror.mysqlError(sqlString, message, "*");
            System.err.println(ed);
        }
    }
}
