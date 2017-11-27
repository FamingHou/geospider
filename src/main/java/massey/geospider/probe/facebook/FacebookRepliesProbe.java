/**
 * 
 */
package massey.geospider.probe.facebook;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.response.GeoResponse;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookRepliesProbe extends FacebookCommentsProbe {

    private static final Logger log = Logger.getLogger(FacebookRepliesProbe.class);

    private String commentId;

    /**
     * Override the postId as the commentId
     * 
     * @param commentId
     */
    public FacebookRepliesProbe(String commentId) {
        super(commentId); // set super.postId as commentId to reuse the same
                          // processing logic of class FacebookCommentsProbe
        this.commentId = commentId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.facebook.FacebookCommentsProbe#doPreCollect()
     */
    @Override
    protected void doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookRepliesProbe#doPreCollect()");
        log.info("Fetching all replies of the comment " + commentId);

        if (inputGeoResponse == null)
            log.info("The first page of replies searching...");
        else
            log.info("The next page of replies searching...");
    }

}
