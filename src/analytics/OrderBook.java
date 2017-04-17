package analytics;

//import
import http.Http;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import mysql.Mysqlconnector;
import global.Time;
import java.sql.SQLException;

/**
 *
 * @author michel
 */
public class OrderBook {

    //great object
    Http http = new Http();
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    Time time = new Time();

    //private
    private final String BITTREX_URL;
    private final String TYPE = "both";
    private final int HOEVEELHEID_ORDERS = 50;
    private final String[] BUY_AND_SELL = {"buy", "sell"};
    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();

    /**
     * Construcor
     *
     * @param BITTREX_URL
     */
    public OrderBook(String BITTREX_URL) {
        this.BITTREX_URL = BITTREX_URL;
    }

    /**
     * Dit stuurt de hele zooi aan
     */
    public void orderBookAnalytics() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Statement stmt = (Statement) conn.createStatement();

            //vraagt alle markt data op waar de software is op gezet.
            ResultSet rs1 = stmt.executeQuery("SELECT * FROM marktanalytics");
            while (rs1.next()) {
                String rs1Exchange = rs1.getString("exchange");
                String rs1Markt = rs1.getString("markt");

                //roep methoden op en stuur gelijk alle data door
                try {
                    runOrderBookAnalytics(exchangeSelect(rs1Exchange, rs1Markt));
                } catch (Exception ex) {
                    System.out.println("Er is een probleem bij run order book analytics.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Deze methoden kijkt welke exchange de request moet komen en stuurt roep
     * dan de juiste methoden op
     *
     * @param exchange
     * @param markt
     * @return Geeft het orderbook terug
     */
    private JSONObject exchangeSelect(String exchange, String markt) {

        //als het bittrex is
        if ("bittrex".equals(exchange)) {
            return bittrexRequestOrderBook(markt);
        } else {
            //maak variable
            JSONObject memoryDB = new JSONObject();

            //als het geen bekende exchange is
            System.out.println("De exchange is niet bekend.");
            return memoryDB.put("succes", false);
        }
    }

    /**
     * Request orderbook
     *
     * @param markt
     * @return Geeft het orderbook terug
     */
    private JSONObject bittrexRequestOrderBook(String markt) {

        //maakt url string
        String url = BITTREX_URL + "/public/getorderbook?market=" + markt + "&type=" + TYPE + "&depth=" + HOEVEELHEID_ORDERS;

        //vraag de data op
        String responseData = http.GetHttp(url);

        //maak variable
        JSONObject memoryDB = new JSONObject();

        //kijk of er geldige data is terug gekomen
        if (!"false".equals(responseData)) {

            System.out.println(responseData);

            //great object
            JSONObject reponseDataJsonObject = new JSONObject(responseData);
            if (!"false".equals(reponseDataJsonObject.get("success"))) {

                //maak object
                JSONArray tempMemoryDBArray = new JSONArray();

                //voor het wel voor buy en sell uit
                for (int i = 0; i < BUY_AND_SELL.length; i++) {

                    //json arra
                    JSONArray resultarray = reponseDataJsonObject.getJSONObject("result").getJSONArray(BUY_AND_SELL[i]);

                    double totaleVolume = 0;

                    //loop
                    for (int x = 0; x < resultarray.length(); x++) {

                        //Maak object
                        JSONObject tempMemoryrDB = new JSONObject();

                        //vul variable met data
                        double prijs = resultarray.getJSONObject(x).getDouble("Rate");
                        double hoeveelheid = resultarray.getJSONObject(x).getDouble("Quantity");

                        //voeg prijs toe in tempMemoryDB
                        tempMemoryrDB.put("prijs", prijs);
                        tempMemoryrDB.put("hoeveelheid", hoeveelheid);

                        //update totaleVolume
                        totaleVolume = totaleVolume + berekenVolume(prijs, hoeveelheid);

                        //add object in array
                        tempMemoryDBArray.put(tempMemoryrDB);
                    }

                    //add array in object
                    memoryDB.put(BUY_AND_SELL[i], tempMemoryDBArray);
                }

                //hoogste prijs
                memoryDB.put("hoogsteBuy", reponseDataJsonObject.getJSONObject("result").getJSONArray("buy").getJSONObject(0).getDouble("Rate"));
                memoryDB.put("hoogsteSell", reponseDataJsonObject.getJSONObject("result").getJSONArray("sell").getJSONObject(0).getDouble("Rate"));

                //exchange
                memoryDB.put("exchange", "bittrex");
                System.out.println("Check " + memoryDB);
                return memoryDB;
            } else {
                System.err.println("Er is een probleem bij orderbook analytics. Bttrex Stuurt geen geldige data terug. Url: " + url);
                return memoryDB.put("succes", false);
            }
        } else {
            System.err.println("Er is een probleem bij orderbook analytics. De exchange is bittrex. Url: " + url);
            return memoryDB.put("succes", false);
        }
    }

    /**
     * Deze methoden bereken alle data en sla het op
     *
     * @param dataAnalytics
     */
    private void runOrderBookAnalytics(JSONObject dataAnalytics) throws SQLException {
        Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();

        System.out.println("Run order book analytics");

        //hier word exchange en mart + timestamp in het database gezet zodat je later via een update sql String de handel goed kan vullen
        String timestamp = time.getTimeStamp();
        String exchange = dataAnalytics.getString("bittrex");
        String markt = dataAnalytics.getString("markt");
        String beginSQL = "INSERT INTO marktanalyticshistory (timestamp, exchange, markt) VALUES (" + timestamp + "," + exchange + ", " + markt + ");";
        System.out.println(beginSQL);
        stmt.execute(beginSQL);

        //double
        double hoogsteBuyPrijs = dataAnalytics.getDouble("hoogsteBuy");
        double hoogsteSellPrijs = dataAnalytics.getDouble("hoogsteSell");

        //JSONObject
        System.out.println(dataAnalytics/*.getJSONObject("arraylist")*/);

        //loop voor dat er zowel buy als sell behandelde word
        for (int i = 0; i < BUY_AND_SELL.length; i++) {

            //krijg de lijst
            JSONObject dataAnalyticsList = new JSONObject(dataAnalytics.getJSONObject(BUY_AND_SELL[i]));

            //gebegin op 0
            double gemiddeldePrijs = 0;
            double gemiddeldeHoeveelheid = 0;
            double orderGrote = 0;

            //loop
            for (int x = 0; x < dataAnalyticsList.length(); x++) {

                //double
                double tempPrijs = dataAnalyticsList.getDouble("prijs");
                double tempHoeveelheid = dataAnalyticsList.getDouble("hoeveelheid");
                
                //kijk of er al data is gevuld
                if (gemiddeldePrijs != 0 || gemiddeldeHoeveelheid != 0) {
                    //Als er al data was opgeteld
                    gemiddeldePrijs = berekenGemiddelde(gemiddeldePrijs, tempPrijs);
                    gemiddeldeHoeveelheid = berekenGemiddelde(gemiddeldeHoeveelheid, tempHoeveelheid);
                    orderGrote = orderBookGrote(orderGrote, tempPrijs, tempHoeveelheid);
                } else {
                    //Als er voor de eerste keer data bij word opgeteld
                    gemiddeldeHoeveelheid = tempHoeveelheid;
                    gemiddeldePrijs = tempPrijs;
                    orderGrote = orderGrote = orderBookGrote(0, tempPrijs, tempHoeveelheid);;
                }
            }

            //kijk even welke van welke SQL Sring gebruikt moet worden
            if(BUY_AND_SELL[i] == "buy"){
                String updateSQL = "UPDATE marktanalyticshistory"
                + "SET  buyhoogsteprijs="+dataAnalytics.getDouble("hoogsteBuy")+", buyordergrote=" +orderGrote+", buygemiddeldeprijs="+gemiddeldePrijs
                +" WHERE timestamp = "+timestamp+" AND exchange = "+exchange+" AND markt="+markt+";";

                stmt.execute(updateSQL);
            } else if(BUY_AND_SELL[i] == "sell"){
                String updateSQL = "UPDATE marktanalyticshistory"
                + "SET  selloogsteprijs="+dataAnalytics.getDouble("hoogsteBuy")+", sellordergrote=" +orderGrote+", sellgemiddeldeprijs="+gemiddeldePrijs
                +" WHERE timestamp = "+timestamp+" AND exchange = "+exchange+" AND markt="+markt+";";

                stmt.execute(updateSQL);
            }
        }
    }

    /**
     * Deze methoden bereken Volume
     *
     * @param prijs
     * @param hoeveelheid
     * @return
     */
    private double berekenVolume(double prijs, double hoeveelheid) {
        return prijs * hoeveelheid;
    }

    /**
     * Deze methoden telt variable op en deelt het door 2
     *
     * @param x1 variable 1
     * @param x2 variable 2
     * @return variable 1 / variable 2
     */
    private double berekenGemiddelde(double x1, double x2) {
        return (x1 + x2) / 2;
    }

    /**
     * 
     * @param x1 variable 1
     * @param x2 variable 2
     * @return eerst x2* prijs zodat je het volume weet enn dan tel de 2 variable bij elkaar op
     */
    private double orderBookGrote(double x1, double x2, double prijs){
        return x1 + x2 * prijs;
    }
}
