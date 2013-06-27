/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author thoeyeth
 */
public class VisitGentReader implements RecordReader {

    
    private File inputfile;
    private JSONParser parser;
    private JSONArray records;
    private int index;
    
    public VisitGentReader(File inputfile) {
        this.inputfile = inputfile;
        this.parser = new JSONParser();
        initialize();
    }
    
    public String getName() {
        return "Visit Gent Data";
    }
    
    public String getSourceFormat() {
        return "JSON";
    }
    
    @Override
    public Map<String, Object> readRecord() throws IOException {
        if (index >= records.size()) {
            return null;
        }
        Map record = new HashMap<String, Object>();
        JSONObject poi = (JSONObject) records.get(index);
        record.put("id", poi.get("id"));
        record.put("title", poi.get("title"));
        record.put("description", poi.get("description"));
        // Categories
        JSONArray categories = (JSONArray) poi.get("category");
        if (categories.size() > 0) {
            // Only adding the first one for now
            record.put("category", categories.get(0));
        }
        // Contact
        JSONArray contacts = (JSONArray) poi.get("contact");
        if (contacts != null && contacts.size() > 0 && contacts.get(0) != null) {
            JSONObject contact = (JSONObject) contacts.get(0);

            
            // Address
            String street = (String) contact.get("street");
            String number = (String) contact.get("number");
            record.put("address_value", street + " " + number);
            String city = (String) contact.get("city");
            String[] cityparts = city.split(" ");
            if (cityparts.length == 2) {
                record.put("address_postal", cityparts[0]);
                record.put("address_city", cityparts[1]);
            }
            // Coordinates
            JSONArray coords = (JSONArray) contact.get("coords");
            if (coords != null && coords.size() > 0 && coords.get(0) != null) {
                JSONObject coord = (JSONObject) coords.get(0);
                String latitude = (String) coord.get("latitude");
                String longitude = (String) coord.get("longitude");
                record.put("latitude", latitude);
                record.put("longitude", longitude);
            }
            // Phones
            JSONArray phones = (JSONArray) contact.get("phone");
            if (phones != null && phones.size() > 0 && phones.get(0) != null) {
                JSONObject phone = (JSONObject) phones.get(0);
                String phonenumber = (String) phone.get("number");
                String phonecountry = (String) phone.get("country_codes");
                record.put("tel", phonenumber);
            }
            // Web
            JSONArray webs = (JSONArray) contact.get("website");
            if (webs != null && webs.size() > 0 && webs.get(0) != null) {
                JSONObject web = (JSONObject) webs.get(0);
                String url = (String) web.get("url");
                record.put("web", web);
            }
            // Email
            
            
        }
        index++;
        return record;

    }
    
    private void initialize() {
        try {
            JSONArray all = (JSONArray)parser.parse(new FileReader(inputfile));
            index = 0;
            System.out.println(all.size() + " Element found in source JSON");
            this.records = all;
        } catch (ClassCastException ccex) {
            System.err.println(inputfile.getName() + "does not contain a JSON array");
        } catch (IOException ex) {
            System.err.println("IO Exception while reading " + inputfile.getName());
            Logger.getLogger(VisitGentReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            System.err.println("Parse Exception while reading " + inputfile.getName());
            Logger.getLogger(VisitGentReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
