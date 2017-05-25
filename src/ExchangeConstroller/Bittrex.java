package ExchangeConstroller;

import org.json.JSONObject;


public class Bittrex {
    
    /**
     * 
     * @param orderID stuur dat reponse van bittrex terug om het te verwerken
     * @return of het gelukt is 
     */
    public boolean succesVolUitgevoerd(String orderID){
        
        JSONObject object = new JSONObject(orderID);
        
        boolean status = object.getBoolean("success");
        
        //kijken of het true is als het fout is print het error bericht op in de terminal
        if(status == true){
            return true;
        } else {
            System.err.println("[BITTREX] Error bericht. "+object.getString("message"));
            return false;
        }  
    };    
}