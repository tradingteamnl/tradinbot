package core.OrderSysteem;

import mysql.MysqlSql;

/**
 *
 * @author michel
 */
public class orderDBSql {
    
    //maak object aan
    MysqlSql mysqlSql = new MysqlSql();
    
    
    public void bittrexAddOrder(String uuid, String markt, double totaal, double remaning, double prijs, int prio){
        String status;
        
        //om de juiste status in het db te zetten
        if(remaning !=0){
            status = "pending";
        } else {
            status = "compleet";
        }
        
        //sql String
        String sql = "INSERT INTO orderID (markt, orderID, totaal, remaning, prijs, exchange, prioritijd, status)" +
            "VALUES ('"+markt+"', '"+uuid+"', '"+totaal+"', '"+remaning+"', '"+prijs+"', 'bittrex', '"+prio+"', );";
        
        //Stuur de sql sting naar het database
        mysqlSql.mysqlStament(sql);
    }
    
    public void bittrexUpdateOrder(){
        
    }
}
