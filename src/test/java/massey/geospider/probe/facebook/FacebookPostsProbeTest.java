/**
 * 
 */
package massey.geospider.probe.facebook;

import junit.framework.TestCase;
import massey.geospider.message.response.facebook.FacebookPaging;
import massey.geospider.message.response.facebook.FacebookPostsResponse;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPostsProbeTest extends TestCase {

    /**
     * @param name
     */
    public FacebookPostsProbeTest(String name) {
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

    public void testNullpointerException() {
        final String nextURL = "https://graph.facebook.com/v2.11/9990179612/posts?access_token=2058156934406300%7CNmjKHgsdzUQV05v2nQpogKVh3OU&pretty=0&fields=id%2Cmessage%2Ccreated_time%2Cplace&limit=100&after=Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R4NDVPVGt3TVRjNU5qRXlPamN4TmpVM09EQXpORFkxTmprMk9UZA3pOREVQREdGd2FWOXpkRzl5ZAVY5cFpBOGNPVGs1TURFM09UWXhNbDh4TURFMU5EQTFOak13TXprME5EWXhNdzhFZAEdsdFpRWlh6ZADM4QVE9PQZDZD";
        FacebookPostsProbe fbPostsProbe = new FacebookPostsProbe(null);
        FacebookPaging paging = new FacebookPaging(nextURL, null);
        FacebookPostsResponse fbPostsRsp = new FacebookPostsResponse(null, null, paging);
        fbPostsProbe.doRequest(null, fbPostsRsp);
        System.out.println("No NullpointerException thrown.");
    }

}
