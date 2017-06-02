/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import bittrex.BittrexBalance;
import bittrex.BittrexMarketRequest;
import ExchangeConstroller.BittrexProtocall;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author michel
 */
public class BittrexDrivers {

    //maak object
    BittrexBalance bittrexbalance = new BittrexBalance();
    BittrexProtocall bittrexProtocall = new BittrexProtocall();
    
    //private
    private final String CONFIG, BITTREX_BASIS_URL;
    private final BittrexMarketRequest MARKT_REQUEST;

    /**
     * Deze constructor vult de private variable
     *
     * @param CONFIG
     */
    public BittrexDrivers(String config, String BittrexBasisUrl ) {
        this.CONFIG = config;
        this.BITTREX_BASIS_URL = BittrexBasisUrl;
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
                try {
                    //System.out.println(bittrexProtocall.getBalances());
                    bittrexbalance.balance();
                } catch (SQLException ex) {
                    System.err.println("Bittrex balance kan niet worden opgevraagd.");
                }

            }
        };

        Timer timer = new Timer();

        timer.schedule(task, new Date(), 60000);

    }
}
