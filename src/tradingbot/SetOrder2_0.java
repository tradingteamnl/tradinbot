package tradingbot;

import java.sql.SQLException;
import java.sql.ResultSet;
import mysql.MysqlSql;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class SetOrder2_0 {

    MysqlSql mysqlSql = new MysqlSql();

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
                    + " WHERE handelsplaats='bittrex' AND cointag='BTC' OR handelsplaats='bittrex' AND cointag='LTC';";

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

            System.out.println("object "+balanceObject);
            System.out.println(balanceObject.getJSONObject("LTC"));
            System.out.println(secCoin);
            
            //kijken of er ruimte is om coins bij de kopen
            double coinRuimte;
            double secCoinBalance = balanceObject.getJSONObject(secCoin).getDouble("balance");
            if(secCoinBalance < maxBuy){
                coinRuimte = maxBuy - secCoinBalance;
            } else {
                coinRuimte = 0;
            }
            
            //naar de prijs kijken
            
            //kijk of er genoeg btc geschikbaar is
            
            System.out.println(coinRuimte);
    }

    public void sellType(JSONObject rs) {
        String handelsplaats = rs.getString("handelsplaats");
        String type = rs.getString("type");
        String markt = rs.getString("markt");
        double minHold = rs.getDouble("minhold");
        double mijnPrijs = rs.getDouble("minprijs");
    }
}
