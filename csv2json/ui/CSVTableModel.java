/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import io.CSVReader;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Thimo
 */
public class CSVTableModel extends DefaultTableModel {
    
    private CSVReader reader;
    
    /**
     * Constructor uses a CSV reader to determine the headers req'd
     * @param reader 
     */
    public CSVTableModel() {
        super();
    }
    
    public boolean setCSVReader(CSVReader reader) {
        this.dataVector.clear();
        this.reader = reader;
        this.addColumn("name", new Vector<String>(reader.getHeaders()));
        // TODO: detect if anything changed
        return true;
    }
}
