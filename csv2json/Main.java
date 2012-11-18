/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package csv2json;

import csv2json.mapping.POIMapper;
import csv2json.io.CSVReader;
import csv2json.io.JSONWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thoeyeth
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Running...");
        
        if (args.length < 1) {
            System.out.println("Usage: java -jar CSV2JSON.jar inpput.csv output.json");
        }
        
        String filename = args[0];
        String output = args[1];
        
        try {
            // Initialize source
            FileReader input = new FileReader(filename);
            CSVReader reader = new CSVReader(input);
            POIMapper mapper = new POIMapper(reader);
            mapper.map();
            Map<String, Object> document = mapper.getDocument();
            JSONWriter writer = new JSONWriter();
            File outputfile = new File(output);
            writer.write(document, outputfile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException iox) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, iox.getMessage(), iox);
        }
        System.out.println("Done...");
    }
}
