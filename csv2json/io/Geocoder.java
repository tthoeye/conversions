package io;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye
 */
import exceptions.CSVColumnCountException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import mapping.POIMapper;
import org.xml.sax.SAXException;
import ui.GeocoderFrame;

public class Geocoder {

  // URL prefix to the geocoder
  private static final String GEOCODER_REQUEST_PREFIX_FOR_XML = "http://maps.google.com/maps/api/geocode/xml";
  private float lat;
  private float lng;
  
  private List<String> reqdfields;
  
  public Geocoder(List<String> reqdfields) {
      this.reqdfields = reqdfields;
  }
  
  public Map<String, Object> geocodeRecord(Map<String, Object> record) {
      try {
        String address = "";
        for (String s : reqdfields) {
            address += record.get(s) + ", ";
        }
        address += "Gent, Belgium";
        System.out.println("Geocoding " + address);
        float[] coords = getLatLong(address);
        Thread.sleep(500);

        String lat = (coords[0] == Float.NaN) ? "" : Float.toString(coords[0]);
        String lng = (coords[1] == Float.NaN) ? "" : Float.toString(coords[1]);
        record.put("lat", lat);
        record.put("lng", lng);

      } catch (IOException ex) {
          Logger.getLogger(POIMapper.class.getName()).log(Level.SEVERE, null, ex);
      } catch (CSVColumnCountException ccex) {
          Logger.getLogger(POIMapper.class.getName()).log(Level.SEVERE, "Column Count Error. Skipping record.", ccex);
      } catch (InterruptedException ex) {
          Logger.getLogger(GeocoderFrame.class.getName()).log(Level.SEVERE, null, ex);
      } catch (XPathExpressionException ex) {
          Logger.getLogger(Geocoder.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ParserConfigurationException ex) {
          Logger.getLogger(Geocoder.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SAXException ex) {
          Logger.getLogger(Geocoder.class.getName()).log(Level.SEVERE, null, ex);
      }
    return record;
}

  public float[] getLatLong(String address)
      throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {

    // prepare a URL to the geocoder
    URL url = new URL(GEOCODER_REQUEST_PREFIX_FOR_XML + "?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=false");

    // prepare an HTTP connection to the geocoder
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    Document geocoderResultDocument = null;
    try {
      // open the connection and get results as InputSource.
      conn.connect();
      InputSource geocoderResultInputSource = new InputSource(conn.getInputStream());

      // read result and parse into XML Document
      geocoderResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(geocoderResultInputSource);
    } finally {
      conn.disconnect();
    }

    // prepare XPath
    XPath xpath = XPathFactory.newInstance().newXPath();

    // extract the result
    NodeList resultNodeList = null;

    // a) obtain the formatted_address field for every result
    resultNodeList = (NodeList) xpath.evaluate("/GeocodeResponse/result/formatted_address", geocoderResultDocument, XPathConstants.NODESET);
    for(int i=0; i<resultNodeList.getLength(); ++i) {
      System.out.println(resultNodeList.item(i).getTextContent());
    }

    // b) extract the locality for the first result
    resultNodeList = (NodeList) xpath.evaluate("/GeocodeResponse/result[1]/address_component[type/text()='locality']/long_name", geocoderResultDocument, XPathConstants.NODESET);
    for(int i=0; i<resultNodeList.getLength(); ++i) {
      System.out.println(resultNodeList.item(i).getTextContent());
    }

    // c) extract the coordinates of the first result
    resultNodeList = (NodeList) xpath.evaluate("/GeocodeResponse/result[1]/geometry/location/*", geocoderResultDocument, XPathConstants.NODESET);
    lat = Float.NaN;
    lng = Float.NaN;
    for(int i=0; i<resultNodeList.getLength(); ++i) {
      Node node = resultNodeList.item(i);
      if("lat".equals(node.getNodeName())) lat = Float.parseFloat(node.getTextContent());
      if("lng".equals(node.getNodeName())) lng = Float.parseFloat(node.getTextContent());
    }
    System.out.println("lat/lng=" + lat + "," + lng);
    
    /*
    // c) extract the coordinates of the first result
    resultNodeList = (NodeList) xpath.evaluate("/GeocodeResponse/result[1]/address_component[type/text() = 'administrative_area_level_1']/country[short_name/text() = 'BE']/*", geocoderResultDocument, XPathConstants.NODESET);
    float lat = Float.NaN;
    float lng = Float.NaN;
    for(int i=0; i<resultNodeList.getLength(); ++i) {
      Node node = resultNodeList.item(i);
      if("lat".equals(node.getNodeName())) { lat = Float.parseFloat(node.getTextContent()); }
      if("lng".equals(node.getNodeName())) { lng = Float.parseFloat(node.getTextContent()); }
    }
    */
    float[] result = new float[2];
    result[0] = lat;
    result[1] = lng;
    return result;
  }

}
