/**
 * 
 */
package massey.geospider.language;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * GS-1001-2 A language detector class which can detect which language the
 * message is written in.
 * 
 * https://github.com/shuyo/language-detection
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class LangDetector {

    private static final Logger log = Logger.getLogger(LangDetector.class);

    static {
        try {
            URI uri = LangDetector.class.getClassLoader().getResource("profiles").toURI();
            log.info("uri = " + uri);
            DetectorFactory.loadProfile(new File(uri));
            // detector = DetectorFactory.create();
            log.info("profiles were loaded successfully.");
        } catch (LangDetectException e) {
            log.error(e, e);
        } catch (URISyntaxException e) {
            log.error(e, e);
        }
    }

    /**
     * Detects which language the message text was written in
     * 
     * @param text
     *            the input message
     * @return the language code
     */
    public static String detectLanguage(String text) {
        Detector detector;
        String lang = "";
        try {
            detector = DetectorFactory.create();
            detector.append(text);
            lang = detector.detect();
        } catch (LangDetectException e) {
            log.error(e, e);
        }
        return lang;
    }

    /**
     * Is the text written in English or not?
     * 
     * @param text
     * @return true - the text is written in English; false - otherwise
     */
    public static boolean isEnglish(String text) {
        String lan = detectLanguage(text);
        if (lan != null && lan.trim().toLowerCase().equals("en")) {
            return true;
        } else {
            log.debug(String.format("[%s] is {%s}", text, lan));
            return false;
        }
    }

}
