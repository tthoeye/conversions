/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thimo
 * 
 * This monster writes a CSV from a structured document that looks like this:
 * "0" 
 */
public class CSVFileWriter extends DataWriter {
    
    private boolean headerWritten;
    private boolean writeHeader;
    private String encapsulator;
    private String delimiter;
    
    
    public CSVFileWriter(File file, String encapsulator, String delimiter) throws IOException {
        super(file);
        this.encapsulator = encapsulator;  
        this.delimiter = delimiter;  
        this.headerWritten = false;
        this.writeHeader = true;
    }
    
    public void write(Map<String, Object> document, File file) throws IOException {
        if (document.isEmpty()) {
            return;
        }
        int i = 0;   
        Map<String, Object> record;
        
        // Loop through records
        while(document.get(String.valueOf(i)) != null) {
            
            // Try to get record with key "i"
            record = (Map<String, Object>)document.get(String.valueOf(i));
  
            String line = "";
            // Write header
            if (!headerWritten && writeHeader) {  
                System.out.print("Writing headers");
                for (String s : record.keySet()) {
                    line += encapsulator + s + encapsulator + delimiter;
                    headerWritten = true;
                }
            } 
            // Write record
            else {
                for (Object value : record.values()) {
                    String s = (String)value;
                    line += encapsulator + s + encapsulator + delimiter;
                }
                i++;
            }
            // Terminate line
            line += "\n";
            try {
                write(line);
            } catch (IOException ex) {
                Logger.getLogger(CSVFileWriter.class.getName()).log(Level.SEVERE, "Can't write CSV header", ex);
            }
        }
        flush();
    }
}
