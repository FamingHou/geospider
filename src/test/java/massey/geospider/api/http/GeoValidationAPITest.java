/**
 * 
 */
package massey.geospider.api.http;

import java.net.URLEncoder;

import org.json.JSONObject;

import junit.framework.TestCase;
import massey.geospider.util.JSONHelper;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoValidationAPITest extends TestCase {

    /**
     * @param name
     */
    public GeoValidationAPITest(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValidate() {
        try {
            String msg = "I am living in Auckland, New Zealand";
            StringBuilder sb = new StringBuilder();
            sb.append("http://127.0.0.1:8000/validate/");
            String msgEncoded = URLEncoder.encode(msg, "UTF-8").replaceAll("\\+", "%20");
            sb.append(msgEncoded);
            System.out.println(msgEncoded);
            // using URIBuilder to solve URISyntax issues
            // URIBuilder builder = new URIBuilder(sb.toString());

            // String responseString =
            // HttpHelper.doGetAsync(builder.toString());
            String responseString = HttpHelper.doGetAsync(sb.toString());
            JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
            String isValidStr = JSONHelper.get(jsonObj, "is_valid_str");
            System.out.println(isValidStr);
            if (isValidStr != null && isValidStr.trim().equalsIgnoreCase("true"))
                System.out.println("true...");
            // return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return false;
    }
}
