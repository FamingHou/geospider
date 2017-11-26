/**
 * 
 */
package massey.geospider.conf;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Property file reader class which is responsible for reading parameter values
 * user defined.
 * 
 * It is a singleton object class.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class PropReader {

    private static final Logger log = Logger.getLogger(PropReader.class);

    private static PropReader single = null;

    public static final String PROP_FILE_NAME = "geospider.properties";
    final Properties props = new Properties();

    /**
     * 
     */
    private PropReader() {
        log.debug("PropReader#constructor()");
    }

    /**
     * 
     */
    private void loadProperties() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            props.load(loader.getResourceAsStream(PROP_FILE_NAME));
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    /**
     * 
     * @return a singleton PropReader object
     */
    public static PropReader getReader() {
        if (single == null) {
            synchronized (log) {
                single = new PropReader();
                single.loadProperties();
            }
        }
        return single;
    }

    /**
     * Searches for the property with the specified key in the property map.
     * 
     * @param key
     *            the property key
     * @return the value in the property map with the specified key
     */
    public String get(String key) {
        String val = props.getProperty(key);
        if (val == null)
            throw new IllegalArgumentException(
                    "The value with the specified key [ " + key + " ] was not found in file " + PROP_FILE_NAME);
        return val;
    }

}
