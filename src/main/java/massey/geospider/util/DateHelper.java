/**
 * 
 */
package massey.geospider.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * All static methods for processing Date related functions.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class DateHelper {

    private static final Logger log = Logger.getLogger(DateHelper.class);

    /**
     * 
     */
    private DateHelper() {
    }

    /**
     * Constructs a {@code Timestamp} object using dateString with a template of
     * format.
     * 
     * @param dateString
     * @param format
     * @return a {@code TimeStamp} object
     */
    public static Timestamp parse(String dateString, String format) {
        if (dateString == null || dateString.equals("") || format == null)
            return null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            Date date = dateFormat.parse(dateString);
            long time = date.getTime();
            return new Timestamp(time);
        } catch (Exception e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Constructs a Timestamp object using a seconds time value.
     * 
     * @param epochTime
     *            seconds since January 1, 1970, 00:00:00 GMT.
     * @return java.sql.Timestamp
     */
    public static Timestamp parse(long epochTime) {
        return new Timestamp(epochTime*1000);
    }
}
