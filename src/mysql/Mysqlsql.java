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
public class MysqlSql {

    //maak object aan
    Mysqlconnector mysqlconnector = new Mysqlconnector();

    private final String USERNAME = mysqlconnector.getUsername();
    private final String PASSWORD = mysqlconnector.getPassword();
    private final String CONN_STRING = mysqlconnector.getUrlmysql();

    /**
     * 
     * @param sqlString
     * @return data uit het database
     * @throws SQLException 
     */
    public ResultSet mysqlSelectStament(String sqlString) throws SQLException {

        //connecntie mysql
        Connection conn;
        conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        Statement stmt = (Statement) conn.createStatement();

        //return
        return stmt.executeQuery(sqlString);
    }
    
    /**
     * 
     * @param sqlString
     * @return of het gelukt is of niet 
     */
    public boolean mysqlStament(String sqlString){
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            Statement stmt = (Statement) conn.createStatement();
            
            stmt.executeQuery(sqlString);
            
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

}
