/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package io;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public interface RecordReader {
    
    abstract Map<String, Object> readRecord()
            throws IOException;
    
    abstract String getName();
    
    abstract String getSourceFormat();
    
}
