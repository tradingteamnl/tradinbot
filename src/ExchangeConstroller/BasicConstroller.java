/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ExchangeConstroller;

/**
 *
 * @author michel
 */
public class BasicConstroller {
    
    //great object
    Bittrex bittrexConstroller = new Bittrex();
    
    
    public boolean getSuccusVol(String exchange, String orderID){
        
        //kijk welke exchange het is
        if("bittrex".equals(exchange)){
            //stuur door naar bittrexConstroller
            return bittrexConstroller.succesVolUitgevoerd(orderID);
        } else {
            System.err.println("De exchange is niet bekend in de BasicConstroller.");
            return false;
        }
    };
    
    
    
}
