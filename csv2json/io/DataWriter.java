/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Thimo
 */
public abstract class DataWriter extends FileWriter {
    
    public DataWriter(File file) throws IOException {
        super(file);
    }
    
    abstract public void write(Map<String, Object> document, File file) throws IOException;
    
}
