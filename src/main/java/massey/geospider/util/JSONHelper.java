/**
 * 
 */
package massey.geospider.util;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility methods for processing JSON related.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class JSONHelper {

    private static final Logger log = Logger.getLogger(JSONHelper.class);

    /**
     * 
     */
    private JSONHelper() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Checks whether the String str is valid JSON String
     * 
     * @param str
     *            a String object
     * @return true - if str is a valid JSON String; false - otherwise.
     */
    public static boolean isValidJson(String str) {
        try {
            new JSONArray(str);
        } catch (JSONException e) {
            try {
                new JSONObject(str);
            } catch (JSONException e1) {
                log.error("This following string:");
                log.error(str);
                log.error(":(continued) is neither a JSONArray nor a JSONObject.");
                return false;
            }
        }
        return true;
    }

}
