/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author michel
 */
public class Time {
    
    private long timeStamp(){
        
        //krijg de time stamp
        Date date = new Date();
        long time = date.getTime();
        
        return time;
    }
    
    /**
     * @return timestamp in string 
     */
    public String getTimeStamp(){
        return String.valueOf(timeStamp());
    }
    
    /**
     * 
     * @return de datum
     */
    public String getData(){
        return new SimpleDateFormat("yyyy-MM-dd").format(timeStamp());
    }
    
    /**
     * @return de tijd
     */
    public String getTime(){
        return new SimpleDateFormat("HH:mm:ss").format(timeStamp());
    }
    
    /**
     * @return datum en tijd
     */
    public String getDataAndTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeStamp());
    }
}
