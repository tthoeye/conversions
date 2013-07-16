/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Thimo
 */
public class CSVFilter extends FileFilter {
    
    public final static String csv = "csv";
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(csv)) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    @Override
    public String getDescription() {
        return "Comma Separated Values (csv)";
    }
    
}
