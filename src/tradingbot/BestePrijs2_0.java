package tradingbot;

import java.sql.SQLException;
import java.sql.ResultSet;
import mysql.MysqlSql;

/**
 *
 * @author michel
 */
public class BestePrijs2_0 {

    MysqlSql mysqlSql = new MysqlSql();

    /**
     *
     * @param handelsplaats De exchange.
     * @param markt De markt.
     * @param type buy or sell.
     * @return De beste prijs.
     */
    public double getBestPrijs(String handelsplaats, String markt, String type) {
        try {
            switch (type) {
                case "buy":
                    //roep methoden aan en return het stament
                    return bestBuyPrijs(handelsplaats, markt);
                case "sell":
                    return bestSellPrijs(handelsplaats, markt);
                default:
                    return 00.00;
            }
        } catch (SQLException ex) {
            System.err.println("Type is niet bekend. Probleem doet zich voor bij BestePrijs");
            return 00.00;
        }
    }

    /**
     *
     * @param handelsplaats De exchange.
     * @param markt De markt.
     * @return De beste prijs voor buy.
     * @throws SQLException als de software het niet doet.
     */
    private double bestBuyPrijs(String handelsplaats, String markt) throws SQLException {

        //request data uit het database
        ResultSet rs = mysqlSql.mysqlSelectStament("SELECT Bid FROM marktdata WHERE handelsplaats='" + handelsplaats + "'");
        while (rs.next()) {
            double prijs = rs.getDouble("Bid") + 0.00000001;
            return prijs;
        }
        return 00.00;
    }

    /**
     *
     * @param handelsplaats De exchange.
     * @param markt De markt.
     * @return De beste prijs voor sell.
     * @throws SQLException als de software het niet doet.
     */
    private double bestSellPrijs(String handelsplaats, String markt) throws SQLException {

        ResultSet rs = mysqlSql.mysqlSelectStament("SELECT Ask FROM marktdata WHERE handelsplaats='" + handelsplaats + "'");
        while (rs.next()) {
            double prijs = rs.getDouble("Ask") - 0.00000001;
            return prijs;
        }
        return 00.00;
    }
}
