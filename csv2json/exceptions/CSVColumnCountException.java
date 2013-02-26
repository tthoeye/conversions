/** 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 * @author Thimo Thoeye 
 */ 
package exceptions;

/**
 *
 * @author thoeyeth
 */
public class CSVColumnCountException extends RuntimeException {
    
    private int headerColumnCount;
    private int currentColumnCount;
    
    public CSVColumnCountException(int currentColumnCount , int headerColumnCount) {
        super("Number of columns in row does not match number of headers! " + currentColumnCount + " vs " + headerColumnCount);
        this.currentColumnCount = currentColumnCount;
        this.headerColumnCount = headerColumnCount;
    }

    /**
     * @return the headerColumnCount
     */
    public int getHeaderColumnCount() {
        return headerColumnCount;
    }

    /**
     * @param headerColumnCount the headerColumnCount to set
     */
    public void setHeaderColumnCount(int headerColumnCount) {
        this.headerColumnCount = headerColumnCount;
    }

    /**
     * @return the currentColumnCount
     */
    public int getCurrentColumnCount() {
        return currentColumnCount;
    }

    /**
     * @param currentColumnCount the currentColumnCount to set
     */
    public void setCurrentColumnCount(int currentColumnCount) {
        this.currentColumnCount = currentColumnCount;
    }
    
}
