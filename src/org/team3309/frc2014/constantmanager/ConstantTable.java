/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.team3309.frc2014.constantmanager;

import com.sun.squawk.microedition.io.FileConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import org.team3309.friarlib.util.Util;

/**
 *
 * @author Ben
 */
public class ConstantTable {
    
    private Hashtable box = new Hashtable();
    
    private static ConstantTable instance;
    
    private ConstantTable () {
        
    instance = this;
    
    }
    private void loadTable() throws IOException{ 
        
        String fileLocation = "file:///Constants.txt";
        FileConnection fileConnection = (FileConnection) Connector.open(fileLocation, Connector.READ);
        loadFromFile(fileConnection.openInputStream());
        
    }
    
    private void loadFromFile (InputStream inputStream){
        byte[] buffer = new byte[255];
        String content = "";

        try {
            // Read everything from the file into one string.
            while (inputStream.read(buffer) != -1) {
                content += new String(buffer);
            }
            inputStream.close();
            String [] lines = split(content, "\n");
            
            for (int i = 0; i < lines.length; i++){
                
                String line = lines[i];
                line = line.trim();
                line = Util.remove(line, ' ');
                line = Util.remove(line, '\t');
                if (line.startsWith("#") || line.startsWith("//") || line.equals("")) {
                    continue;
                }
                if (!Util.contains(line, "=")) {
                    throw new IOException("Invalid format, line <" + line + "> does not contain equals sign");
                }
                String key = line.substring(0, line.indexOf("=")).trim();
                String value = line.substring(line.indexOf("=") + 1);
                
            }      
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    
    public ConstantTable getConstantTable() {
    if (instance == null){
        return new ConstantTable();
    }
    
    return instance;
       } 
    
    /**
     * Returns the array of substrings obtained by dividing the given input
     * string at each occurrence of the given delimiter.
     */
    private static String[] split(String input, String delimiter) {
        Vector node = new Vector();
        int index = input.indexOf(delimiter);
        while (index >= 0) {
            node.addElement(input.substring(0, index));
            input = input.substring(index + delimiter.length());
            index = input.indexOf(delimiter);
        }
        node.addElement(input);

        String[] retString = new String[node.size()];
        for (int i = 0; i < node.size(); ++i) {
            retString[i] = (String) node.elementAt(i);
        }

        return retString;
    }
    
}
