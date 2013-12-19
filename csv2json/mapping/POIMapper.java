/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package mapping;

import exceptions.CSVColumnCountException;
import io.Geocoder;
import io.RecordReader;
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

public class POIMapper extends AbstractMapper {

    
    public POIMapper(RecordReader rrdr) {
        super(rrdr);
    }
    
    /**
     * This method maps the content contained in the RecordReader to a structure
     * that complies with the JSON format expected by the Citadel templates.
     * 
     * This structure is intentionally kept as abstract as possible (Map<String, Object>)
     * to accommodate for serialization to other formats then JSON in the future.
     */
    public void map(Geocoder coder) {
        System.out.println("MAPPING POI");
        clear();
        Map<String, Object> dataset = new HashMap<String, Object>();
        // Add Individuals POIs
        List<Map<String, Object>> pois = new ArrayList<Map<String, Object>>();
        Map<String, Object> record;
        int i = 0;
        try {
            while ((record = rrdr.readRecord()) != null)   {
                System.out.println("Reading record");
                Map<String, Object> poi = readPOI(record, coder);
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
        dataset.put("id", "http://www.gentmatinees.be/");
        dataset.put("updated", now);
        dataset.put("created", now);
        dataset.put("lang", "nl-NL");
        Map<String, String> author = new HashMap<String, String>();
        author.put("id", "http://www.gent.be/");
        author.put("value", "Thimo Thoeye for Stad Gent");
        dataset.put("author", author);
        dataset.put("license", new HashMap<String, String>());
        Map<String, String> link = new HashMap<String, String>();
        dataset.put("link", link);
        dataset.put("updatefrequency", "");
                      
        document.put("dataset", dataset);
         
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
    private Map<String, Object> readPOI(Map<String, Object> record, Geocoder coder) {
        
        // List used column names 
        String[] reservedcolumns = {
            "id",
            "title",
            "description",
            "category",
            "latitude",
            "longitude",
            "address_value",
            "address_postal",
            "address_city",
            "tel",
            "web",
            "email"
        };

        // Construct a Map<String, Object> which will be the top node of this POI tree
        Map<String, Object> poi = new HashMap<String, Object>();
        
        // General POI description
        poi.put("id", record.get("id"));
        poi.put("title", record.get("title"));
        String title = (String)record.get("title");
        System.out.println("MAPPING: " + title);
        // Description when available
        Object description = (record.get("description") != null) ? record.get("description") : "";
        poi.put("description", description);
        List<String> categories = new ArrayList<String>();
        categories.add((String) record.get("category"));
        poi.put("category", categories);
        
        // Initialize the position
        Map<String, Object> location = new HashMap<String, Object>();
        Map<String, Object> point = new HashMap<String, Object>();
        Map<String, Object> pos = new HashMap<String, Object>();
        Map<String, Object> address = new HashMap<String, Object>();
        // Configure coordinates
        pos.put("srsName", "http://www.opengis.net/def/crs/EPSG/0/4326");
        
        if (coder != null) {
            System.out.println("Geocoding");
            record = coder.geocodeRecord(record);
        }
        String latitude = String.valueOf(record.get("lat"));
        String longitude = String.valueOf(record.get("lng"));
        
        if (latitude != null && latitude.contains(",")) {
            latitude = latitude.replaceAll(",", ".");
        }
        if (longitude != null && longitude.contains(",")) {
            longitude = longitude.replaceAll(",",".");
        }
        pos.put("posList", latitude + " " + longitude);
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
        List<Map<String, Object>> attributes = new ArrayList<Map<String, Object>>();
        // Telephone
        if (record.get("tel") != null) {
            Map<String, Object> tel = new HashMap<String, Object>();
            tel.put("term", "Tel");
            tel.put("type", "tel");
            tel.put("text", record.get("tel"));
            tel.put("tplIdentifier", "#Citadel_telephone");
            attributes.add(tel);
        }
        // Website
        if (record.get("web") != null) {
            Map<String, Object> web = new HashMap<String, Object>();
            web.put("term", "url");
            web.put("type", "url");
            web.put("text", record.get("web"));
            web.put("tplIdentifier", "#Citadel_website");
            attributes.add(web);
        }
        // Email
        if (record.get("email") != null) {
        Map<String, Object> email = new HashMap<String, Object>();
            email.put("term", "E-mail");
            email.put("type", "email");
            email.put("text", record.get("email"));
            email.put("tplIdentifier", "#Citadel_email");
            attributes.add(email);
        }
        // Event Start Date
        if (record.get("event_start") != null) {
            Map<String, Object> email = new HashMap<String, Object>();
            email.put("term", "Start Date");
            email.put("type", "date");
            email.put("text", record.get("event_start"));
            email.put("tplIdentifier", "#Citadel_eventStart");
            attributes.add(email);
        }
        
        // Event End Date
        if (record.get("event_end") != null) {
            Map<String, Object> email = new HashMap<String, Object>();
            email.put("term", "End Date");
            email.put("type", "date");
            email.put("text", record.get("event_end"));
            email.put("tplIdentifier", "#Citadel_eventEnd");
            attributes.add(email);
        }
        
        // Event Place
        if (record.get("event_place") != null) {
            Map<String, Object> email = new HashMap<String, Object>();
            email.put("term", "Event Place");
            email.put("type", "string");
            email.put("text", record.get("event_place"));
            email.put("tplIdentifier", "#Citadel_eventPlace");
            attributes.add(email);
        }
        
         // Event Schedule
        if (record.get("event_schedule") != null) {
            Map<String, Object> email = new HashMap<String, Object>();
            email.put("term", "Schedule");
            email.put("type", "string");
            email.put("text", record.get("event_schedule"));
            email.put("tplIdentifier", "#Citadel_eventSchedule");
            attributes.add(email);
        }
        poi.put("attribute", attributes);
        System.out.println(poi.size());
        return poi;
    }
    
}