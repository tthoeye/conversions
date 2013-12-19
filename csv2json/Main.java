/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 

import io.CSVReader;
import io.Geocoder;
import io.JSONWriter;
import io.UitDatabankReader;
import io.VisitGentReader;
import mapping.POIMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

public class Main {

   
    
    public void buildUI() {
         try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
           // handle exception
        }
        catch (ClassNotFoundException e) {
           // handle exception
        }
        catch (InstantiationException e) {
           // handle exception
        }
        catch (IllegalAccessException e) {
           // handle exception
        }
        
        JFrame frame = new ui.GeocoderFrame();
        frame.setVisible(true);
        frame.pack();
    }
    
    public void parseCSV(String filename, String output) {
        try {
            // Initialize source
            FileReader input = new FileReader(filename);
            CSVReader reader = new CSVReader(input);
            POIMapper mapper = new POIMapper(reader);            
            Geocoder coder = new Geocoder(new ArrayList<String>());
            mapper.map(coder);
            Map<String, Object> document = mapper.getDocument();
            File outputfile = new File(output);
            JSONWriter writer = new JSONWriter(outputfile);
            
            writer.write(document, outputfile);          
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException iox) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, iox.getMessage(), iox);
        }        
        System.out.println("Done...");
    }
    
    public void decodeVisitGent(String input, String output) {
        try {
            // Initialize source
            File inputfile = new File(input);
            VisitGentReader vgr = new VisitGentReader(inputfile);
            POIMapper mapper = new POIMapper(vgr);   
            Geocoder coder = new Geocoder(new ArrayList<String>());
            mapper.map(coder);
            Map<String, Object> document = mapper.getDocument();
            File outputfile = new File(output);
            JSONWriter writer = new JSONWriter(outputfile);
            //File outputfile = new File(output);
            writer.write(document, outputfile);          
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException iox) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, iox.getMessage(), iox);
        }        
        System.out.println("Done...");
        
    }
    
    public void testUitdatabank(String output) {
       
        try {
            // Initialize source
            UitDatabankReader udbr = new UitDatabankReader();
            POIMapper mapper = new POIMapper(udbr); 
            Geocoder coder = new Geocoder(new ArrayList<String>());
            mapper.map(coder);
            Map<String, Object> document = mapper.getDocument();
            
            File outputfile = new File(output);
            JSONWriter writer = new JSONWriter(outputfile);
            writer.write(document, outputfile);          
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException iox) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, iox.getMessage(), iox);
        }        
        System.out.println("Done...");
    }

    /**
     * Main
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Running...");
        Main main = new Main();
        if (args.length < 1) {
            main.buildUI();
            //System.out.println("Usage: java -jar CSV2JSON.jar inpput.csv output.json");
        }
        
        //String filename = args[0];
        //String output = args[1];
        
        //main.parseCSV(filename, output);
        //main.parseCSV("POI_issy.csv", "POI_issy.json");
        //main.decodeVisitGent(filename, output);
        
    }
  
}
