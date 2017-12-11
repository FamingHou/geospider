/**
 * 
 */
package massey.geospider.probe.facebook;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.response.GeoResponse;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookRepliesProbe extends FacebookCommentsProbe {

    private static final Logger log = Logger.getLogger(FacebookRepliesProbe.class);

    /**
     * Sets parentId as a commentId
     * 
     * @param commentId
     */
    public FacebookRepliesProbe(FacebookMessage fbParent) {
        // set super.fbParent as an object of FacebookComment to reuse the same
        // processing logic of class FacebookCommentsProbe
        super(fbParent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.facebook.FacebookCommentsProbe#doPreCollect()
     */
    @Override
    protected void doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookRepliesProbe#doPreCollect()");
        log.info("Fetching all replies of the comment " + fbParent.getId());
        if (inputGeoResponse == null)
            log.info("The first page of replies searching...");
        else
            log.info("The next page of replies searching...");
    }

    /**
     * Returns 4 which means this is a reply
     */
    @Override
    protected int getRecordType() {
        return RECORD_TYPE_REPLY;
    }

}
