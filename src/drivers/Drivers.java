/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

//import
import global.FileSystem;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author michel
 */
public class Drivers {
    
    
    public static void main(String[] args) {
        
        //great object filesystem
        FileSystem filesystem = new FileSystem();
        
        
        //laat config bestand;
        String config = filesystem.readConfig();
        
        
        //Great drivers
        BittrexDrivers bittrexdrivers = new BittrexDrivers(config);
        
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
               bittrexdrivers.start();
            }
        };
        
        Timer timer = new Timer();
        timer.schedule(task, new Date(), 60000);
        
        System.out.println("Reload code is gestart.");
    }
}
