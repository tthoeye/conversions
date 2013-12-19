/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapping;

import io.Geocoder;
import io.RecordReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thimo
 * 
 * Helper Abstract class
 * To subclass implement map() method
 * 
 */
public abstract class AbstractMapper {
     
    protected RecordReader rrdr;
    protected Map<String, Object> document;
    
    public AbstractMapper(RecordReader rdr) {
        this.rrdr = rdr;
        this.document = new HashMap<String, Object>();
    } 
    
    /**
     * Clear stuff
     */
    public void clear() {
        getDocument().clear();
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
    
    abstract void map(Geocoder coder);
}
