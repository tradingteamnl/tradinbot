/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import trading.BerekenGemiddelde;
import Security.SHA512;

//bittrex import
import bittrex.BittrexDriver;
import trading.TradingRuimte;

/**
 *
 * @author michel
 */
public class Drivers {
    
    SHA512 sha512 = new SHA512();
    BerekenGemiddelde berekengemiddelde = new BerekenGemiddelde();
    BittrexDriver bittrexdriver = new BittrexDriver();
    CoinList coinlist = new CoinList();
    TradingRuimte tradingruimte = new TradingRuimte();
    
    public void main(){
        
        
        FileSystem filesystem = new FileSystem();
        String OS = System.getProperty("os.name").toLowerCase();
        
        //run code
        filesystem.folderExist();
        bittrexdriver.main();
        tradingruimte.Prioritijd();
        
        //berekengemiddelde.dbCheck();
        //coinlist.GetCoinList();
        
        System.out.println("fafafasd");
    }
}
