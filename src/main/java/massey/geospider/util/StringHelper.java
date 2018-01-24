/**
 * 
 */
package massey.geospider.util;

import java.util.regex.Pattern;

/**
 * All static methods for processing Strings.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class StringHelper {

    /**
     * 
     */
    private StringHelper() {
    }

    /**
     * Checks whether String s contains the keyword in case insensitive pattern.
     * 
     * @param s
     * @param keyword
     * @return true - if s contains keyword; false - otherwise.
     */
    public static boolean hasKeyword(String s, String keyword) {
        if (s == null || keyword == null)
            return false;
        return Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(s).find();
    }

}
