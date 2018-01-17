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

    /**
     * Creates a new JSONObject object. This method should be called everywhere
     * which wants to create a new JSONObject to avoid RuntimeException which
     * will halt the main process.
     * 
     * @param jsonString
     *            an input String to be parsed into JSONObject
     * @return null if jsonString is invalid JSON String, a new JSONObject
     *         otherwise.
     */
    public static JSONObject createAJSONObject(String jsonString) {
        if (!isValidJson(jsonString))
            return null;
        return new JSONObject(jsonString);
    }

    /**
     * Returns the attribute value of the attribute name of the JSONObject
     * 
     * @param obj
     *            JSONObject object
     * @param name
     *            attribute name
     * @return attribute value of the attribute name
     */
    public static String get(JSONObject obj, String name) {
        if (obj == null || name == null)
            return null;
        return obj.isNull(name) ? null : obj.getString(name);
    }

    /**
     * Returns an object of JSONObject by name
     * 
     * @param obj
     * @param name
     * @return JSONObject
     */
    public static JSONObject getJSONObj(JSONObject obj, String name) {
        if (obj == null || obj.isNull(name))
            return null;
        else
            return obj.getJSONObject(name);
    }

    /**
     * Returns an object of JSONArray by name
     * 
     * @param obj
     * @param name
     * @return JSONArray
     */
    public static JSONArray getJSONArray(JSONObject obj, String name) {
        if (obj == null || obj.isNull(name))
            return null;
        return obj.getJSONArray(name);
    }

}
