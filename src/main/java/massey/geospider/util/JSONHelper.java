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
     * @return attribute value of the attribute name with String type
     */
    public static String get(JSONObject obj, String name) {
        if (obj == null || name == null)
            return null;
        return obj.isNull(name) ? null : obj.getString(name);
    }

    /**
     * Returns the attribute value of the attribute name of the JSONObject
     * 
     * @param obj
     *            JSONObject
     * @param name
     *            attribute name
     * @return the corresponding attribute value of the name; Long.MIN_VALUE
     *         will be returned when errors happen
     */
    public static long getLong(JSONObject obj, String name) {
        if (obj == null || name == null)
            return Long.MIN_VALUE;
        return obj.isNull(name) ? Long.MIN_VALUE : obj.getLong(name);
    }

    /**
     * Returns the attribute value of the attribute name of the JSONObject
     * 
     * @param obj
     *            JSONObject
     * @param name
     *            attribute name
     * @return the corresponding attribute value of the name; Double.MIN_VALUE
     *         will be returned when errors happen
     */
    public static double getDouble(JSONObject obj, String name) {
        if (obj == null || name == null)
            return Double.MIN_VALUE;
        return obj.isNull(name) ? Double.MIN_VALUE : obj.getDouble(name);
    }

    /**
     * Returns the attribute value of the attribute name of the JSONObject
     * 
     * @param obj
     *            JSONObject
     * @param name
     *            attribute name
     * @return the corresponding attribute value of the name; Integer.MIN_VALUE
     *         will be returned when errors happen
     */
    public static int getInt(JSONObject obj, String name) {
        if (obj == null || name == null)
            return Integer.MIN_VALUE;
        return obj.isNull(name) ? Integer.MIN_VALUE : obj.getInt(name);
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
