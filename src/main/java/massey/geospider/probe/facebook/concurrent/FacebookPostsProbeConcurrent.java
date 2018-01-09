/**
 * 
 */
package massey.geospider.probe.facebook.concurrent;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.facebook.FacebookPost;
import massey.geospider.probe.facebook.FacebookPostsProbe;

/**
 * The concurrent implementation of class FacebookPostsProbe
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPostsProbeConcurrent extends FacebookPostsProbe {

    /**
     * @param fbPage
     */
    public FacebookPostsProbeConcurrent(FacebookPage fbPage) {
        super(fbPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.facebook.FacebookPostsProbe#
     * doCollectAllCommentsOfOnePost(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.facebook.FacebookPost)
     */
    @Override
    protected void doCollectAllCommentsOfOnePost(GeoCmdLine geoCmdLine, FacebookPost fbPost) {
        if (fbPost == null)
            return;
        FacebookCommentsProbeConcurrent fbCommentsProbeConcurrent = new FacebookCommentsProbeConcurrent(fbPost);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbCommentsProbeConcurrent.collect(geoCmdLine, null);
    }

}
