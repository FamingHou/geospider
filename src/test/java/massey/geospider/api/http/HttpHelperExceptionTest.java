/**
 * 
 */
package massey.geospider.api.http;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import junit.framework.TestCase;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class HttpHelperExceptionTest extends TestCase {

    /**
     * @param name
     */
    public HttpHelperExceptionTest(String name) {
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

    public void testInvalidCommentId() {
        StringBuilder sb = new StringBuilder();
        // sb.append("https://graph.facebook.com/v2.11/195551580614605_510852445751182/comments");
        sb.append(
                "https://graph.facebook.com/v2.11/195551580614605:510852445751182:10104292594832341_510853312417762/comments");
        try {
            // using URIBuilder to solve URISyntax issues
            URIBuilder builder = new URIBuilder(sb.toString());
            builder.addParameter("access_token", "2058156934406300%7CNmjKHgsdzUQV05v2nQpogKVh3OU");
            builder.addParameter("pretty", "0");
            builder.addParameter("limit", "100");

            HttpHelper.doGet(builder.toString());
            System.out.println("All done.");
        } catch (URISyntaxException ue) {
            ue.printStackTrace();
        }
    }

}
