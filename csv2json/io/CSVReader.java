/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package csv2json.io;

import csv2json.exceptions.CSVColumnCountException;
import csv2json.io.RecordReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thoeyeth
 */
public class CSVReader extends BufferedReader implements RecordReader {
    
    protected Reader rdr;
    protected String delimiter;
    protected String encapsulator;
    protected boolean hasHeader;
    protected List<String> headers;
    
    
    public CSVReader (Reader rdr) {
        this(rdr, 1, ";", " " , true);
    }
    
    public CSVReader (Reader rdr, List<String> headers) {
        this(rdr, 1, ";", " " , false);
        this.hasHeader = true;
        this.headers = headers;
    }
    
    public CSVReader (Reader rdr, int buffer, String delimiter, String encapsulator, boolean hasHeader) {
        super(rdr, buffer);
        this.rdr = rdr;
        this.delimiter = delimiter;
        this.encapsulator = encapsulator;
        this.hasHeader = hasHeader;
        this.headers = new ArrayList<>();
        if (hasHeader) {
            readHeader();
        }
    }
    
    private void readHeader() {
        String line = "";
        try {
            line = super.readLine();
        } catch (IOException ex) {
            Logger.getLogger(CSVReader.class.getName()).log(Level.SEVERE, "Error while reading headers in CSV", ex);
            return;
        }
        headers.clear();
        headers.addAll(Arrays.asList(line.split(delimiter)));
    }
    
    /**
     *
     * @return
     * @throws IOException, CSVColumnCountException
     */
    @Override
    public Map<String, Object> readRecord() 
        throws IOException, CSVColumnCountException {
        
        String line = super.readLine();
        if (line == null) {
            return null;
        }
        String[] items = line.split(delimiter);
        if (items.length != headers.size()) {
            throw new CSVColumnCountException(items.length, headers.size());
        }
        Map<String, Object> record = new HashMap<>(headers.size());
        for (int i = 0; i < headers.size(); i++) {
            record.put(headers.get(i), items[i]);
        }
        return record;
    }
}
