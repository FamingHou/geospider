/**
 * 
 */
package massey.geospider.probe.facebook.concurrent;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.probe.facebook.FacebookPagesProbe;

/**
 * The concurrent implementation of class FacebookPagesProbe
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPagesProbeConcurrent extends FacebookPagesProbe {

    private static final Logger log = Logger.getLogger(FacebookPagesProbeConcurrent.class);

    /**
     * 
     */
    public FacebookPagesProbeConcurrent() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.facebook.FacebookPagesProbe#
     * doCollectAllPostsOfOnePage(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.facebook.FacebookPage)
     */
    @Override
    protected void doCollectAllPostsOfOnePage(GeoCmdLine geoCmdLine, FacebookPage fbPage) {
        if (fbPage == null)
            return;
        FacebookPostsProbeConcurrent fbPostsProbeConcurrent = new FacebookPostsProbeConcurrent(fbPage);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbPostsProbeConcurrent.collect(geoCmdLine, null);
    }

}
