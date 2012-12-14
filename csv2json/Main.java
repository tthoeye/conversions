/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package csv2json;

import csv2json.io.CSVReader;
import csv2json.io.JSONWriter;
import csv2json.io.UitDatabankReader;
import csv2json.mapping.POIMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

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
        Main main = new Main();
        //main.parseCSV(filename, output);
        //main.parseCSV("POI_issy.csv", "POI_issy.json");
        main.testUitdatabank("events_gent.json");
    }
    
    public void parseCSV(String filename, String output) {
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
    
    public void testGeocode(String address) {
        Geocoder geo;
        try {
            geo = new Geocoder(address);
            System.out.println("Geocoded " + address);
            System.out.println("to:");
            System.out.println(Float.toString(geo.getLat()) + " " + Float.toString(geo.getLng()));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void testUitdatabank(String output) {
       
        try {
            // Initialize source
            UitDatabankReader udbr = new UitDatabankReader();
            POIMapper mapper = new POIMapper(udbr);            
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
