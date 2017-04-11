/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

//import
import analytics.OrderBook;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author michel
 */
public class AnalytisDrivers {

    //private
    private final String BITTREX_BASIS_URL;
    private final OrderBook ORDER_BOOK;

    public AnalytisDrivers(String config, String BITTREX_BASIS_URL) {
        this.BITTREX_BASIS_URL = BITTREX_BASIS_URL;
        this.ORDER_BOOK = new OrderBook(BITTREX_BASIS_URL);
    }
    
    /**
     * Deze methoden herlaat om de minut
     */
    public void start() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                ORDER_BOOK.orderBookAnalytics();
            }
        };

        Timer timer = new Timer();

        timer.schedule(task, new Date(), 60000);
    }
}
