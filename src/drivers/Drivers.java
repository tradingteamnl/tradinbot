package drivers;

//import
import ExchangeConstroller.BittrexProtocall;
import global.FileSystem;
import org.json.JSONObject;
import tradingbot.SetOrder2_0;

/**
 *
 * @author michel
 */
public class Drivers {

    public static void main(String[] args) {

        //great object filesystem
        FileSystem filesystem = new FileSystem();
        SetOrder2_0 setOrder = new SetOrder2_0();
        OrderSysteem orderSysteem = new OrderSysteem();
        
        
        //laat config bestand;
        String config = filesystem.readConfig();

        //request url
        final String BITTREX_BASIS_URL = new JSONObject(config).getJSONObject("bittrex").getString("basisUrl");

        //object maken
        BittrexDrivers bittrexdrivers = new BittrexDrivers(config, BITTREX_BASIS_URL);
        AnalytisDrivers analyticsDrivers = new AnalytisDrivers(config, BITTREX_BASIS_URL);

        //reload
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                setOrder.orderSystem();
                bittrexdrivers.start();
                //analyticsDrivers.start();
                setOrder.orderSystem();
            }
        };

        Timer timer = new Timer();

        timer.schedule(task, new Date(), 60000);

        System.out.println("Reload code is gestart.");
    }
}
