/**
 * 
 */
package massey.geospider.conf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import massey.geospider.global.GeoConstants;

/**
 * Property file reader class which is responsible for reading parameter values
 * user defined.
 * 
 * It is a singleton object class.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class PropReader implements GeoConstants {

    private static final Logger log = Logger.getLogger(PropReader.class);

    private static PropReader single = null;

    private static final String PROP_FILE_NAME = "geospider.properties";
    final Properties props = new Properties();
    /** Access tokens of Twitter */
    final List<String> twAccessTokenList = new ArrayList<>();
    /** Current index of access token in twAccessTokenList */
    private int currentTWAccessTokenIndex = -1;

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
    private static PropReader getReader() {
        if (single == null) {
            synchronized (log) {
                single = new PropReader();
                single.loadProperties();
            }
        }
        return single;
    }

    /**
     * Searches for the property with the specified key in the property map for
     * internal usage
     * 
     * @param key
     *            the property key
     * @return the value in the property map with the specified key
     */
    private String getProperty(String key) {
        String val = props.getProperty(key);
        if (val == null)
            throw new IllegalArgumentException(
                    "The value with the specified key [ " + key + " ] was not found in file " + PROP_FILE_NAME);
        return val;
    }

    /**
     * Searches for the property with the specified key in the property map.
     * 
     * @param key
     *            the property key
     * @return the value in the property map with the specified key
     */
    public static String get(String key) {
        return getReader().getProperty(key);
    }

    /**
     * Returns the next access token to solve rate limit issue of Twitter
     * 
     * @return
     */
    private synchronized String getNextTwitterAccessTokenInternal() {
        if (twAccessTokenList.isEmpty()) {
            // populate twAccessTokenList
            String countStr = getProperty(NUMBER_OF_TW_ACCESS_TOKENS_PROP_NAME);
            try {
                int count = Integer.parseInt(countStr);
                for (int i = 0; i < count; ++i) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TW_ACCESS_TOKEN_PROP_NAME);
                    sb.append('_');
                    sb.append(i);
                    twAccessTokenList.add(getProperty(sb.toString()));
                }
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
        //
        String token = twAccessTokenList.get(++currentTWAccessTokenIndex);
        if (currentTWAccessTokenIndex >= twAccessTokenList.size() - 1) {
            currentTWAccessTokenIndex -= twAccessTokenList.size();
        }
        return token;
    }

    /**
     * Returns the next access token to solve rate limit issue of Twitter
     * 
     * @return
     */
    public static String getNextTwitterAccessToken() {
        return getReader().getNextTwitterAccessTokenInternal();
    }

}
