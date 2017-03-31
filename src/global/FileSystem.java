/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author michel
 */
public class FileSystem {
    
    //maakt objecten aan
    JFileChooser fr = new JFileChooser();
    GetOsPlatfrom getosplatform = new GetOsPlatfrom();
    
    private String fileLocation(){
        FileSystemView fw = fr.getFileSystemView();
        
        if("windows".equals(getosplatform.getOS())){
            return Paths.get("C:\\trading").toString()+"\\";
        }
        
        if("mac".equals(getosplatform.getOS())){
            return Paths.get(fw.getDefaultDirectory() + "//Documents//trading").toString()+"//";
        }
        
        return Paths.get(fw.getDefaultDirectory() + "//Documents//trading").toString()+"//";
    }
    
    //methoden om de kijken of de map bestaat
    public void folderExist (){
        
        //kijk of de folder bestaat
        if(!Files.exists(Paths.get(fileLocation()))){
            System.out.println("No Folder");
            
            //folder word aangemaakt.
            File file = new File(fileLocation());
            file.mkdir();
            System.out.println("Folder created");
         } else {
            System.out.println("Folder exists");
        }
    } 
    
    //systeem om bestanden op te slaan
    public String saveFile (String fileName, String FileData){
        try{
            
            if ("know".equals(fileName)){            
                try (PrintWriter writer = new PrintWriter(fileLocation()+countFileDirectory()+".txt", "UTF-8")) {
                    writer.println(FileData);
                }
            } else {
                try (PrintWriter writer = new PrintWriter(fileLocation()+fileName, "UTF-8")) {
                    writer.println(FileData);
                }
            }
            return "true";
        } catch (IOException e) {
           return "false";
        }
    }
    
    /**
     * Deze methode zorgt er voor dat niet succesvol uitgevoerd query worden opgeslagen.
     * @param fileName
     * @param FileData
     * @return of het gelukt is ja of nee
     */
    public String saveFileSQL (String fileName, String FileData){
        try{
            PrintWriter writer = new PrintWriter(fileLocation()+"sqlfile.sql", "UTF-8");
            writer.println(FileData);
            writer.close();
            return "true";
        } catch (IOException e) {
           return "false";
        }
    }
    
    /**
     * Leest bestanden in
     * @param file
     * @return 
     */
    public String readFile (String file){
        try {
            FileSystemView fw = fr.getFileSystemView();
            return new String(Files.readAllBytes(Paths.get(fileLocation()+file)));
        } catch (IOException ex) {
            return "false";
        }
    }
    
    /**
     * Leest de config
     * @return 
     */
    public String readConfig (){
        return readFile("config.json");
    }
    
    /**
     * Teld hoeveel bestanden er in de map zijn.
     * @return
     * @throws IOException 
     */
    private int countFileDirectory() throws IOException{
        return (int) Files.list(Paths.get(fileLocation())).count();
    }
}
