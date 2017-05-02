package tradingbot;

//import
import bittrex.BittrexProtocall;
import http.Http;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mysql.MysqlSql;
import mysql.Mysqlconnector;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class SetOrder {

    //great object
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    BittrexProtocall bittrexprotocall = new BittrexProtocall();
    MysqlSql mysqlSql = new MysqlSql();
    Http http = new Http();
    BestePrijs bestePrijs = new BestePrijs();

    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();

    public void orderSystem() {
        System.out.println("order systeem opgestart");

        try {

            //Haalt de order balance op
            JSONArray arrayList = orderRoom();
            System.out.println(arrayList);
            /*for (int i = 0; i < arrayList.length(); i++) {

                String exchange = arrayList.getJSONObject(i).getString("exchange");
                String markt = arrayList.getJSONObject(i).getString("markt");
                String type = arrayList.getJSONObject(i).getString("type");
                JSONObject maincoin = new JSONObject(balanceDB(exchange, arrayList.getJSONObject(i).getString("maincoin")));
                JSONObject seccoin = new JSONObject(balanceDB(exchange, arrayList.getJSONObject(i).getString("seccoin")));
                double maxprijs = arrayList.getJSONObject(i).getDouble("maxprijs");

                System.out.println(maincoin);
                System.out.println(seccoin);
                System.out.println(arrayList);

                //software geeft opdracht op de order ruimte uit te reken
                if ("buy".equals(type)) {
                    double maxammount = arrayList.getJSONObject(i).getDouble("maxammount");
                    double prijs = bestePrijs.getBestePrijs(exchange, maxprijs, markt, type);
                    double quantity = orderRuimteBuy(prijs, maxammount, seccoin.getDouble("balance"), maincoin.getDouble("available"));
                    
                    bittrexprotocall.buyLimit(markt, String.valueOf(quantity), String.valueOf(prijs));
                    
                } else if("sell".equals(type)){
                    double minHoldAmmount = arrayList.getJSONObject(i).getDouble("minhold");
                    double prijs = bestePrijs.getBestePrijs(exchange, maxprijs, markt, type);
                    
                    //set order
                    bittrexprotocall.sellLimit(markt, String.valueOf(minHoldAmmount), String.valueOf(prijs));


                } else {
                    System.err.println("De verkoop optie is niet bekend");
                }
            }*/
            //software plaats de order
            //software save het uuid
        } catch (SQLException ex) {
            System.err.println("Probleem bij order setting op te vragen.");
        }
    }

    /**
     * Deze methoden verzameld order settings data
     *
     * @return return order setting
     * @throws SQLException als mysql een error geeft
     */
    private JSONArray orderRoom() throws SQLException {

        //zorgd voor mysql verbinding
        Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();

        //maak JSONArray
        JSONArray array = new JSONArray();

        //sql
        String beginSQL = "SELECT * FROM ordersetting";
        System.out.println(beginSQL);
        ResultSet rs = stmt.executeQuery(beginSQL);
        while (rs.next()) {

            //maak object
            JSONObject object = new JSONObject();
            System.out.println(rs.getString("exchange"));

            //vul object
            object.put("exchange", rs.getString("exchange"));
            object.put("markt", rs.getString("markt"));
            object.put("type", rs.getString("type"));
            //object.put("minprijs", rs.getDouble("minprijs"));
            object.put("maxprijs", rs.getDouble("maxprijs"));
            //object.put("maxvolueperorder", rs.getDouble("maxvolueperorder"));
            object.put("maincoin", rs.getString("maincoin"));            
            object.put("seccoin", rs.getString("seccoin"));

            //kijk welke markt optie er is
            if ("buy".equals(rs.getString("type"))) {
                object.put("maxammount", rs.getDouble("maxammount"));
            } else if ("sell".equals(rs.getString("type"))) {
                object.put("minhold", rs.getDouble("minholding"));
            } else {
                System.err.println("Er is een probleem bij setOrder dat het type niet bekend is.");
            }
            //vol array
            array.put(object);
        }

        System.out.println("Alle orders opgehaald uit het systeem.");
        System.out.println(array);
        return array;
    }

    /**
     * Vraagd balance data op uit mysql
     *
     * @param exchange
     * @param coinTag
     * @return jsonobject met alle data er in
     * @throws SQLException
     */
    private JSONObject balanceDB(String exchange, String coinTag) throws SQLException {

        //maak JSONObject aan
        JSONObject jsonObject = new JSONObject();

        //connenctie met mysql
        Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();

        //SQL string
        String beginSQL = "SELECT * FROM balance WHERE exchange='" + exchange + "' AND markt='" + coinTag + "'";
        ResultSet rs = stmt.executeQuery(beginSQL);
        while (rs.next()) {
            jsonObject.put("balance", rs.getDouble("balance"));
            jsonObject.put("available", rs.getDouble("available"));
            jsonObject.put("pending", rs.getDouble("pending"));
        }

        return jsonObject;
    }

    /**
     *
     * @param prijs de prijs
     * @param maxAmmount hoeveel er maximaal op de balance mag staan (in BTC)
     * @param mainBalance hoeveelheid balance van de main coin
     * @param secBalance hoeveel er sec balance staat
     * @return
     */
    private double orderRuimteBuy(double prijs, double maxAmmount, double mainBalance, double secBalance) {

        //bereken hoeveel coins er gekockht kan worden
        double maxCoinsKopen = (maxAmmount / (prijs * 1.0025)) - secBalance;

        //bereken of balance toerijkend is
        if(maxCoinsKopen * prijs * 1.0025 > mainBalance){
            //geef hoeveel coins er gekockt kunnen worden
            return mainBalance / (prijs * 1.0025);
        } else {
            return maxCoinsKopen;
        }
    }

    /**
     *
     * @param minhold hoeveel er minimaal moet worden gewaard
     * @param secBalance hoeveel er op de balance staat
     * @return hoeveelheid coins wat verkockt kan worden
     */
    private double orderRuimteSell(double minhold, double secBalance) {

        //bereken balance wat verkockt kan worden
        double sellBalanceAvailable = secBalance - minhold;

        //kijk of sell balance mogelijke heid negative is
        if (sellBalanceAvailable <= 0) {
            return 00.00;
        } else {
            return sellBalanceAvailable;
        }
    }

    /**
     * Plaats order
     *
     * @param hoeveelheid
     * @param prijs
     * @param markt
     * @return geeft uuid terug
     */
    private String bittrexBuy(double hoeveelheid, double prijs, String markt) {
        return bittrexprotocall.buyLimit(markt, Double.toString(hoeveelheid), Double.toString(prijs));
    }

    /**
     * Plaats order
     *
     * @param hoeveelheid
     * @param prijs
     * @param markt
     * @return geeft uuid terug
     */
    private String bittrexSell(double hoeveelheid, double prijs, String markt) {
        return bittrexprotocall.sellLimit(markt, Double.toString(hoeveelheid), Double.toString(prijs));
    }
}
