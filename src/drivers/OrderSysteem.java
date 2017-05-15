package drivers;

import core.OrderSysteem.OrderStatus;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author michel
 */
public class OrderSysteem {
    
    //maak objecten aan
    OrderStatus orderStatus = new OrderStatus();
    
    public void start() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                
                orderStatus.run();
                
            }
        };

        Timer timer = new Timer();

        timer.schedule(task, new Date(), 60000);

    }
}
