package tradingbot;

import ExchangeConstroller.Bittrex;
import bittrex.BittrexProtocall;
import global.FileSystem;
import global.Time;
import java.sql.SQLException;
import java.sql.ResultSet;
import mysql.MysqlSql;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class SetOrder2_0 {

    //object
    MysqlSql mysqlSql = new MysqlSql();
    BestePrijs2_0 bestePrijs = new BestePrijs2_0();
    BittrexProtocall bittrexProtocall = new BittrexProtocall();
    Time time = new Time();
    Bittrex bittrexConstroller = new Bittrex();

    //private
    private double bittrexFee;
    private double minimaalOrder;

    //constructor
    public SetOrder2_0() {
        //fileSystem object word gemaakt
        FileSystem fileSystem = new FileSystem();

        //vul object met bittrex config
        JSONObject bittrex = new JSONObject(fileSystem.readConfig()).getJSONObject("bittrex");
        this.bittrexFee = bittrex.getDouble("fee");
        this.minimaalOrder = bittrex.getDouble("minimaalOrder");
    }

    public void run() {
        try {
            ResultSet rs = mysqlSql.mysqlSelectStament("SELECT * FROM ordersetting WHERE type='buy'");
            while (rs.next()) {

                JSONObject object = new JSONObject();
                System.out.println(rs.getString("type"));
                object.put("handelsplaats", rs.getString("handelsplaats"));
                object.put("type", rs.getString("type"));
                object.put("markt", rs.getString("markt"));
                System.out.println(rs.getString("type"));
                object.put("maincoin", rs.getString("maincoin"));
                object.put("seccoin", rs.getString("seccoin"));
                switch (rs.getString("type")) {
                    case "buy":
                        //vul het object met deze data als het buy is
                        object.put("maxammount", rs.getDouble("maxammount"));
                        object.put("maxprijs", rs.getDouble("maxprijs"));
                        System.out.println("test");

                        //laat buytype
                        buyType(object);
                        break;
                    case "sell":
                        //vul het object met deze data als het sell is
                        object.put("minhold", rs.getDouble("minholding"));
                        object.put("minprijs", rs.getDouble("minprijs"));

                        //laat sellType
                        //sellType(object);
                        System.out.println(object);
                        break;
                    default:
                        System.err.println("Order type niet herkend. Probleem is bij setOrder.");
                        break;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Probleem met setOrder.");
        }
    }

    /**
     *
     * @param rs hierin staat alle data die belangrijk is voor de buy kant van
     * de software
     * @throws SQLException als er een probleem is om de gevens op tr vragen uit
     * het database
     */
    public void buyType(JSONObject rs) throws SQLException {
        //maak object
        JSONObject balanceObject = new JSONObject();

        //data verzamelen
        String handelsplaats = rs.getString("handelsplaats");
        String type = rs.getString("type");
        String markt = rs.getString("markt");
        String mainCoin = rs.getString("maincoin");
        String secCoin = rs.getString("seccoin");
        double maxBuy = rs.getDouble("maxammount");
        double maxPrijs = rs.getDouble("maxprijs");

        String sqlStament = "SELECT * FROM balance"
                + " WHERE handelsplaats='" + handelsplaats + "' AND cointag='" + mainCoin + "'"
                + " OR handelsplaats='" + handelsplaats + "' AND cointag='" + secCoin + "';";

        ResultSet rsBalanceRaw = mysqlSql.mysqlSelectStament(sqlStament);
        while (rsBalanceRaw.next()) {

            //maak een object om je balance data in op te slaan
            JSONObject object1 = new JSONObject();
            object1.put("balance", rsBalanceRaw.getDouble("balance"));
            object1.put("available", rsBalanceRaw.getDouble("available"));
            object1.put("pending", rsBalanceRaw.getDouble("pending"));

            //maak een object van de balance
            balanceObject.put(rsBalanceRaw.getString("cointag"), object1);
        }

        System.out.println("object " + balanceObject);
        System.out.println(balanceObject.getJSONObject("LTC"));
        System.out.println(secCoin);

        //kijken of er ruimte is om coins bij de kopen
        double coinRuimte;
        double secCoinBalance = balanceObject.getJSONObject(secCoin).getDouble("balance");
        if (secCoinBalance < maxBuy) {
            coinRuimte = maxBuy - secCoinBalance;
        } else {
            coinRuimte = 0;
        }
        System.out.println(coinRuimte);

        //naar de prijs kijken
        double prijs = bestePrijs.getBestPrijs(handelsplaats, markt, type, maxPrijs);

        //kijk of er genoeg btc geschikbaar is
        double totaleKosten = coinRuimte * prijs * bittrexFee;

        //balance maincoin beschikbaar
        double mainCoinAvailable = balanceObject.getJSONObject(mainCoin).getDouble("available");

        if (totaleKosten <= mainCoinAvailable) {

            //kijk of de order meer is dan voor af ingesteld bedrag
            if (minimaalOrderMethoden(totaleKosten) == true) {

                //set order bij bittrex
                String orderID = bittrexProtocall.buyLimitv2(markt, totaleKosten, prijs);

                //laat de methoden die kijkt of de order succesvol is geplaats
                boolean succes = bittrexConstroller.succesVolUitgevoerd(orderID);

                //als het succes vol is 
                if (succes == true) {
                    //bereken hoeveel coins er gekockt gaan worden
                    double totaleHoeveelheidCoins = totaleKosten / (prijs / 1.25);

                    //send de data naar de methoden die het opslaat in het database
                    voegOrderInDatabase(orderID, handelsplaats, markt, totaleHoeveelheidCoins, totaleHoeveelheidCoins, prijs, '1');
                } else {
                    System.out.println("De order is niet succes vol geplaats.");
                }

            }
        } else if (totaleKosten > mainCoinAvailable) {

            if (minimaalOrderMethoden(totaleKosten) == true) {
                //set order bij bittrex
                String orderID = bittrexProtocall.buyLimitv2(markt, totaleKosten, prijs);

                //laat de methoden die kijkt of de order succesvol is geplaats
                boolean succes = bittrexConstroller.succesVolUitgevoerd(orderID);

                //als het succes vol is 
                if (succes == true) {
                    //bereken hoeveel coins er gekockt gaan worden
                    double totaleHoeveelheidCoins = totaleKosten / (prijs / 1.25);

                    //send de data naar de methoden die het opslaat in het database
                    voegOrderInDatabase(orderID, handelsplaats, markt, totaleHoeveelheidCoins, totaleHoeveelheidCoins, prijs, '1');
                } else {
                    System.out.println("De order is niet succes vol geplaats.");
                }

            }
        } else {
            System.err.println("SetOrder2_0 is er een probleem dat er geen optie beschikbaar is.");
        }
    }

    public void sellType(JSONObject rs) {
        String handelsplaats = rs.getString("handelsplaats");
        String type = rs.getString("type");
        String markt = rs.getString("markt");
        double minHold = rs.getDouble("minhold");
        double mijnPrijs = rs.getDouble("minprijs");
    }

    /**
     *
     * @param totaleKosten Totale kosten
     * @param minimaalOrder Deze word geladen uit de constructor
     * @return true of false
     */
    private boolean minimaalOrderMethoden(double totaleKosten) {
        //kijk of de order meer is dan voor af ingesteld bedrag
        if (totaleKosten >= minimaalOrder) {
            return true;
        } else {
            System.err.println("De order is niet groot genoeg op te plaats.");
            return false;
        }
    }

    /**
     * @see Deze methoden slaat de orderID op waar auto cancel bij zit
     * @param orderID unieke order code terug komt van de exchange
     * @param handelsplaats Exchange.
     * @param markt Markt.
     * @param total Totale bedrag.
     * @param remaning Hoeveel coins er nog moeten worden aangeschaft.
     * @param prijs De prijs.
     * @param autoCancel Of de order cancel moet worden 1 is ja 0 is nee.
     * @param setTime Hoelang de order open mag staan.
     * @param prioritijd Hoe belangrijk de order is.
     *
     * private void voegOrderInDatabase(String orderID, String handelsplaats,
     * String markt, double total, double remaning, double prijs, double
     * autoCancel, double setTime, double prioritijd ){
     *
     * //get timestamp int timeStamp = time.getTimeStamp();
     *
     * //creat sql insert string String insertString = "INSERT INTO
     * bigData.openOrders (handelsplaats, markt, status, totaal, remaning,
     * prijs, autoCancel, setTime, prioritijd, updateTime, begintijd)" +" VALUES
     * ('b', 'b', '1', '1', '1', '1', '1', '1', '1', '1', );";
     *
     * //run sql code mysqlSql.mysqlStament(insertString); }
     */
    
    /**
     * @see Deze methoden slaat de orderID op waar geen auto cancel bij zit
     * @param orderID unieke order code terug komt van de exchange
     * @param handelsplaats Exchange.
     * @param markt Markt.
     * @param total Totale bedrag.
     * @param remaning Hoeveel coins er nog moeten worden aangeschaft.
     * @param prijs De prijs.
     * @param prioritijd Hoe belangrijk de order is.
     */
    private void voegOrderInDatabase(String orderID, String handelsplaats, String markt, double total, double remaning, double prijs, double prioritijd) {

        //get timestamp
        int timeStamp = time.getTimeStamp();

        //creat sql insert string
        String insertString = "INSERT INTO bigData.openOrders (orderID, handelsplaats, markt, status, totaal, remaning, prijs, prioritijd, updateTime, begintijd)"
                + " VALUES ('" + orderID + "', '" + handelsplaats + "', '" + markt + "', 'pending', '" + total + "', '" + remaning + "', '" + prijs + "', '" + prioritijd + "', '" + timeStamp + "', '" + timeStamp + "');";

        //run sql code
        mysqlSql.mysqlStament(insertString);
    }
}
