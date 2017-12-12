/**
 * 
 */
package massey.geospider.api.http;

import static org.junit.Assert.fail;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class HttpHelperGeotxtAPITest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void test() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public void testDoRequestGeoTxtOrgAPI() throws Exception {
        // String message = "I am here";
        String message = "I am living in Auckland";
        StringBuilder sb = new StringBuilder();
        sb.append("http://geotxt.org/v2/api/geotxt.json");

        // using URIBuilder to solve URISyntax issues
        URIBuilder builder = new URIBuilder(sb.toString());
        builder.addParameter("m", "stanfords");
        builder.addParameter("q", message);

        String responseString = HttpHelper.doGet(builder.toString());

        JSONObject jsonObj = new JSONObject(responseString);
        if (jsonObj == null || jsonObj.isNull("features"))
            return;
        JSONArray dataArray = jsonObj.getJSONArray("features");
        int len = dataArray.length();
        System.out.println("length = " + len);
    }

}
