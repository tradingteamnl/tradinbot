package tradingbot;

import http.Http;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class BestePrijs {

    Http http = new Http();

    public double getBestePrijs(String exchange, double maxPrijs, String markt, String type) {

        if (exchange == "bittrex") {
            String url = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=" + markt;

            //vul de array met de markt data van bittrex
            JSONArray marktData = new JSONObject(http.GetHttp(url)).getJSONArray("result");

            if (type == "buy") {
                return bestPrijsBittrexBuy(maxPrijs, markt, marktData);
            } else if (type == "sell") {
                return bestPrijsBittrexSell(maxPrijs, markt, marktData);
            } else {
                System.err.println("Het markt type is onbekend in de software!");
                return 00.00;
            }
        } else {
            return 00.00;
        }
    }

    /**
     *
     * @param maxPrijs de prijs
     * @param markt markt
     * @param marktData marktdata
     * @return geef de prijs terug
     */
    private double bestPrijsBittrexBuy(double maxPrijs, String markt, JSONArray marktData) {

        double requestBid = marktData.getJSONObject(0).getDouble("Bid");

        //als maxPrijs kleiner is dan requestBid return dan maxPrijs
        if (maxPrijs <= requestBid) {
            return maxPrijs;
        } else if (maxPrijs > requestBid) {
            return requestBid + 0.00000001;
        } else {
            System.out.println("Er is geen geldige data bij bestPrijsBittrexBuy");
            return 00.00;
        }
    }

    /**
     *
     * @param maxPrijs prijs voor wat het minimaal verkockt mag worden
     * @param markt de markt
     * @param marktData data van de mark
     * @return geef de prijs terug
     */
    private double bestPrijsBittrexSell(double maxPrijs, String markt, JSONArray marktData) {

        double requestAsk = marktData.getJSONObject(0).getDouble("Ask");

        //als maxPrijs kleiner is dan requestBid return dan maxPrijs
        if (maxPrijs < requestAsk) {
            return requestAsk - 0.00000001;
        } else if (maxPrijs >= requestAsk) {
            return maxPrijs;
        } else {
            System.out.println("Er is geen geldige data bij bestPrijsBittrexSell");
            return 00.00;
        }
    }
}
