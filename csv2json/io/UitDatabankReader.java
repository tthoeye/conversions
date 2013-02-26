
/**
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye
 */

package io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class serves as a Reader that pulls some information directly from the UitDatabank API
 * This information is returned in XML, which is subsequently parsed and stored in a typical 
 * Map of Strings to Objects
 * 
 * @author thoeyeth
 */
public class UitDatabankReader implements RecordReader {

    private String testkey = "E886F2BE-1BC2-41F7-B981-EA4C41E5BD66";
    private String key = "e4cb927a-6463-4d95-9472-08614c425d16";
    private String baseurl = "http://build.uitdatabank.be/api/events/search?key=";
    private String cachefile = "uitdb.cache.xml";
  
    private NodeList items;
    private int index;
    
    public UitDatabankReader() {
        this.items = null;
        this.index = 0;
        try {
            String urlPath = baseurl + key + "&datetype=next30days";
            URL url = new URL(urlPath);
            
            // Quickly read and dump to file using NIO
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(cachefile);
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
            fos.close();

        } catch (MalformedURLException ex) {
            Logger.getLogger(UitDatabankReader.class.getName()).log(Level.SEVERE, "Could not connect to " + baseurl + key, ex);
        } catch (IOException ioex) {
            Logger.getLogger(UitDatabankReader.class.getName()).log(Level.SEVERE, "Could not connect to " + baseurl + key, ioex);
        }
    
    }
    
    
    @Override
    public Map<String, Object> readRecord() throws IOException {
        
        // If the DOM has not yet been initialized
        if (items == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
               //Using factory get an instance of document builder
               DocumentBuilder db = dbf.newDocumentBuilder();
                //parse using builder to get DOM representation of the XML file
                Document dom = db.parse(this.cachefile);
                 //get the root element
                Element docEle = dom.getDocumentElement();
                NodeList listElList = docEle.getElementsByTagName("list");
                if (listElList.getLength() > 0) {
                    Element listEl = (Element)listElList.item(0);
                     items = listEl.getElementsByTagName("item");
                } else {
                    System.out.println("Could not find Element \"list\" in XML document");
                }

            } catch(ParserConfigurationException pce) {
                pce.printStackTrace();
                return null;
            } catch(SAXException se) {
                se.printStackTrace();
                return null;
            }
        } 
        
        // We assert that the items field has been set to a NodeList
        if (this.index < items.getLength()) {
            Element itemEl = (Element) items.item(index);
            String title = itemEl.getAttribute("title");
            System.out.println(title);
            index++;
            
             // Construct the record using the "default" fields
             // id;title;shop_category;category;type;description;address_value;address_postal;address_city;tel;web;email;latitude;longitude
             Map<String, Object> record = new HashMap<>();
             record.put("id", index);
             record.put("title", itemEl.getAttribute("title"));
             record.put("description", itemEl.getAttribute("shortdescription"));
             record.put("category", itemEl.getAttribute("heading"));
             record.put("event_place", itemEl.getAttribute("location"));
             String date = itemEl.getAttribute("calendarsummary");
             
             // We need to parse this horrific "date" string to something that makes sense to computers
             // Case: The string contains abbreviated dutch day names:
             String days[] = {"ma", "di", "woe", "vrij", "za", "zo"};
             
             record.put("event_start", date);
             record.put("event_end", date);
             String latlng = itemEl.getAttribute("latlng");
             String[] components = latlng.split(";");
             if (components.length == 2) {
                 record.put("latitude", components[0]);
                 record.put("longitude", components[1]);
             }
             
             // Add the address
             record.put("address_postal", itemEl.getAttribute("zip"));
             record.put("address_city", itemEl.getAttribute("city"));
             String address =  itemEl.getAttribute("address");
             String street = address.substring(0, address.indexOf(","));
             record.put("address_value", street);
             return record; 
             
        } else {
            return null;
        }
    }
}

    


