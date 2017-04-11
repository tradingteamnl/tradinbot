/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import bittrex.BittrexMarketRequest;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

/**
 *
 * @author michel
 */
public class BittrexDrivers {
    //private
    private final String CONFIG, BITTREX_BASIS_URL;
    private final BittrexMarketRequest MARKT_REQUEST;

    /**
     * Deze constructor vult de private variable
     *
     * @param CONFIG
     */
    public BittrexDrivers(String CONFIG) {
        this.CONFIG = CONFIG;
        this.BITTREX_BASIS_URL = new JSONObject(CONFIG).getJSONObject("bittrex").getString("basisUrl");
        this.MARKT_REQUEST= new BittrexMarketRequest(CONFIG, BITTREX_BASIS_URL);
    }
    
    /**
     * Als deze methoden word gestart word om iedere minut alles methodes gerunt
     */
    public void start() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                MARKT_REQUEST.marketRequest();
            }
        };

        Timer timer = new Timer();

        timer.schedule(task, new Date(), 60000);

    }
}
