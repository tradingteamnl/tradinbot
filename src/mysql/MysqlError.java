/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysql;

import global.FileSystem;
import global.Time;

/**
 *
 * @author michel
 */
public class MysqlError {
    
    FileSystem filesystem = new FileSystem();
    Time time = new Time();
    
    public void mysqlError(String sql, String message, String typeQuery){
        
        //kijk of sql moet worden opgeslagen
        if ("SELECT".equals(typeQuery)){
            
        } else {
            saveSQL(sql);
        }
        
        //stuur message door naar de save methode
        filesystem.saveFile("know", message);
    }
    
    private void saveSQL (String sql){
        
        //verzamel alle data
        String fileName = "sqlfile.sql";
        String readFile = filesystem.readFile(fileName);
        String fileData = readFile +"\n\n"+ sql;
        
        //stuur het nieuwe sql bestand naar de opslaan method
        filesystem.saveFile(fileName, fileData);
    }
}
