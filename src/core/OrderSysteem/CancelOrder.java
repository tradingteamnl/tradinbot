package core.OrderSysteem;

import global.FileSystem;
import org.json.JSONObject;

public class CancelOrder {
    
    //private
    private String bittrexKey;
    private String bittrexSecretKey;
    
    //Objecten
    FileSystem fileSystem = new FileSystem();
    
    //constructor
    public CancelOrder(){
        
        //vraag de keys op
        JSONObject config = new JSONObject(fileSystem.readConfig());
        
        this.bittrexKey = config.getJSONObject("bittrex").getString("apikey");
        this.bittrexSecretKey = config.getJSONObject("bittrex").getString("apisecretkey");
    }
    
    
    public void cancelOrder(String uuid){
        
        //zoek uuid op in het database
        
    }
}
