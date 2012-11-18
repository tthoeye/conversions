/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package csv2json.mapping;

import csv2json.exceptions.CSVColumnCountException;
import csv2json.io.RecordReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class POIMapper {
    
    private RecordReader rrdr;
    private Map<String, Object> document;
    
    public POIMapper(RecordReader rdr) {
        this.rrdr = rdr;
        this.document = new HashMap<>();
    } 
    
    public void clear() {
        getDocument().clear();
    }
    
    public void map() {
        clear();
        Map<String, Object> dataset = new HashMap<>();
        // Add Individuals POIs
        List<Map<String, Object>> pois = new ArrayList<>();
        Map<String, Object> record;
        int i = 0;
        try {
            while ((record = rrdr.readRecord()) != null)   {
                Map<String, Object> poi = readPOI(record);
                pois.add(poi);
                i++;
            //"poi": [
            }
        } catch (IOException ex) {
            Logger.getLogger(POIMapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CSVColumnCountException ccex) {
             Logger.getLogger(POIMapper.class.getName()).log(Level.SEVERE, "Column Count Error on line " + i + ". Skipping record.", ccex);
        }
        dataset.put("poi", pois);
        
        // Add Dataset Metadata
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        String now = df.format(new Date());
        dataset.put("id", "http://www.example.com/");
        dataset.put("updated", now);
        dataset.put("created", now);
        dataset.put("lang", "fr-FR");
        Map<String, String> author = new HashMap<>();
        author.put("id", "http://www.ville-issy.fr/");
        author.put("value", "City of Athens");
        dataset.put("author", author);
        dataset.put("license", new HashMap<String, String>());
        Map<String, String> link = new HashMap<>();
        dataset.put("link", link);
        dataset.put("updatefrequency", "");
                      
        document.put("dataset", dataset);
         
    }

    /**
     * @return the document
     */
    public Map<String, Object> getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Map<String, Object> document) {
        this.document = document;
    }

    /**
     * This method will interpret a given record (a map) as a POI.
     * The resulting structure (a map) will be in a form compliant to the one
     * used by the templates.
     * 
     * This structure still needs to be serialized in the desirable encoding,
     * for example by using JSONWriter.
     * 
     * The correct translation largely depends on the presence of a number
     * of pre-defined "header" strings, as listed below:
     * 
     * - id
     * - title
     * - description
     * - category
     * - latitude
     * - longitude
     * - address_value
     * - address_postal
     * - address_city
     * - web
     * - email
     * 
     * @param record
     * @return 
     */
    private Map<String, Object> readPOI(Map<String, Object> record) {

        // Construct a Map<String, Object> which will be the top node of this POI tree
        Map<String, Object> poi = new HashMap<>();
        poi.put("id", record.get("id"));
        poi.put("title", record.get("title"));
        poi.put("description", record.get("description"));
        List<String> categories = new ArrayList<>();
        categories.add((String) record.get("category"));
        poi.put("category", categories);
        
        // Initialize the position
        Map<String, Object> location = new HashMap<>();
        Map<String, Object> point = new HashMap<>();
        Map<String, Object> pos = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        // Configure coordinates
        pos.put("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
        pos.put("posList", record.get("latitude") + " " + record.get("longitude"));
        point.put("pos", pos);
        point.put("term", "centroid");
        // Configure Address
        address.put("value", record.get("address_value"));
        address.put("postal", record.get("address_postal"));
        address.put("city", record.get("address_city"));
        location.put("address", address);
        location.put("point", point);
        poi.put("location", location);
        
                // Configure Attributes
        List<Map<String, Object>> attributes = new ArrayList<>();
        // Telephone
        Map<String, Object> tel = new HashMap<>();
        tel.put("term", "Tel");
        tel.put("type", "tel");
        tel.put("text", record.get("tel"));
        tel.put("tplIdentifier", "#Citadel_telephone");
        attributes.add(tel);
        // Website
        Map<String, Object> web = new HashMap<>();
        web.put("term", "url");
        web.put("type", "url");
        web.put("text", record.get("web"));
        web.put("tplIdentifier", "#Citadel_website");
        attributes.add(web);
        // Email
        Map<String, Object> email = new HashMap<>();
        email.put("term", "E-mail");
        email.put("type", "email");
        email.put("text", record.get("email"));
        email.put("tplIdentifier", "#Citadel_email");
        attributes.add(email); 
        
        poi.put("attribute", attributes);
        return poi;
    }
    
}
