/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapping;

import io.Geocoder;
import io.RecordReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thimo
 */
public class CSVMapper extends AbstractMapper {

    public CSVMapper(RecordReader rrdr) {
        super(rrdr);
    }
    
    @Override
    public void map(Geocoder coder) {
        try {
            int i = 0;
            Map<String, Object> record;
            while((record = rrdr.readRecord()) != null) {
                document.put(String.valueOf(i) , record);
                i++;
            }
            System.out.println(i + " records added to document");
        } catch (IOException ex) {
            Logger.getLogger(CSVMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
