/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author michel
 */
public class Mysqlsql {
    
    //maak object aan
    Mysqlconnector mysqlconnector = new Mysqlconnector();
    MysqlError mysqlerror = new MysqlError();
    
    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();
    
    
    public void mysqlExecute(String sqlString){
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Statement stmt = (Statement) conn.createStatement();
            stmt.executeQuery(sqlString);
        } catch (SQLException ed) {
            
            //maak van de error een string
            String message = ""+ed;
            
            //stuur de info door naar error afhandel systeem
            mysqlerror.mysqlError(sqlString, message, "*");
            System.err.println(ed);
        } 
    }
        
}
