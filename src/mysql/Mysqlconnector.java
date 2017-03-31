/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysql;

/**
 *
 * @author michel
 */
public class Mysqlconnector {
    private final String USENAME = "root";
    private final String PASSWORD = "Pulsar11";
    private final String IPADDRESS = "localhost";
    private final String POORT = "3306";
    private final String DATABASENAAM = "cryptodata3_0";
    private final boolean AUTORECONNECT = true;
    private final boolean SSL = false;
  
    public String getUsername(){
        return USENAME;
    };
        
    public String getPassword(){
        return PASSWORD;
    };
    
    public String getUrlmysql(){
        return "jdbc:mysql://"+IPADDRESS+":"+POORT+"/"+DATABASENAAM+"?autoReconnect="+AUTORECONNECT+"&useSSL="+SSL;
    };

    public String getDATABASENAAM() {
        return DATABASENAAM;
    }
    
    //mogelijk om te connecten met een externe database
    private final String USENAMESECONDE = "root";
    private final String PASSWORDSECONDE = "Pulsar11";
    private final String IPADDRESSSECONDE = "localhost";
    private final String POORTSECONDE = "3306";
    private final String DATABASENAAMSECONDE = "cryptodata3_0";
    private final boolean AUTORECONNECTSECONDE = true;
    private final boolean SSLSECONDE = false;
    
    public String getUsernameSeconde(){
        return USENAMESECONDE;
    };
        
    public String getPasswordSeconde(){
        return PASSWORDSECONDE;
    };

    public String getDATABASENAAMSECONDE() {
        return DATABASENAAMSECONDE;
    }
    
    public String getUrlmysqlSeconde(){
        return "jdbc:mysql://"+IPADDRESSSECONDE+":"+POORTSECONDE+"/"+DATABASENAAMSECONDE+"?autoReconnect="+AUTORECONNECTSECONDE+"&useSSL="+SSLSECONDE;
    };

}
