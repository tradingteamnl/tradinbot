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
     * @param DBprijs de prijs die in het db staat
     * @return De beste prijs.
     */
    public double getBestPrijs(String handelsplaats, String markt, String type, double DBprijs) {
        try {
            switch (type) {
                case "buy":
                    //roep methoden aan en return het stament
                    return bestBuyPrijs(handelsplaats, markt, DBprijs);
                case "sell":
                    return bestSellPrijs(handelsplaats, markt, DBprijs);
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
    private double bestBuyPrijs(String handelsplaats, String markt, double DBprijs) throws SQLException {

        //request data uit het database
        ResultSet rs = mysqlSql.mysqlSelectStament("SELECT Bid FROM marktdata WHERE handelsplaats='" + handelsplaats + "'");
        while (rs.next()) {
            double prijs = rs.getDouble("Bid") + 0.00000001;
            
            if(prijs < DBprijs){
                return prijs;
            } else {
                return DBprijs;
            }
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
    private double bestSellPrijs(String handelsplaats, String markt, double DBprijs) throws SQLException {

        ResultSet rs = mysqlSql.mysqlSelectStament("SELECT Ask FROM marktdata WHERE handelsplaats='" + handelsplaats + "'");
        while (rs.next()) {
            double prijs = rs.getDouble("Ask") - 0.00000001;
            
            if(prijs > DBprijs){
                return prijs;
            } else {
                return DBprijs;
            }
        }
        return 00.00;
    }
}
