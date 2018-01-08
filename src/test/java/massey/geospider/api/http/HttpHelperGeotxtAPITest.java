/**
 * 
 */
package massey.geospider.api.http;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import junit.framework.TestCase;
import massey.geospider.util.JSONHelper;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class HttpHelperGeotxtAPITest extends TestCase {

    /**
     * @param name
     */
    public HttpHelperGeotxtAPITest(String name) {
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

    public void testDoRequestGeoTxtOrgAPI() throws Exception {
        // String message = "I am here";
        String message = "I am living in Auckland";
        StringBuilder sb = new StringBuilder();
        sb.append("http://geotxt.org/v2/api/geotxt.json");

        // using URIBuilder to solve URISyntax issues
        URIBuilder builder = new URIBuilder(sb.toString());
        builder.addParameter("m", "stanfords");
        builder.addParameter("q", message);

        String responseString = HttpHelper.doGetAsync(builder.toString());
//        HttpHelperPoolingClient.shutdown();
        
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
        if (jsonObj == null || jsonObj.isNull("features"))
            return;
        JSONArray dataArray = jsonObj.getJSONArray("features");
        int len = dataArray.length();
        System.out.println("length = " + len);
    }

}
