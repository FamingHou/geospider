/**
 * 
 */
package massey.geospider.probe.facebook;

import java.util.List;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.facebook.FacebookComment;
import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookCommentsResponse;

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

    /**
     * 
     * Only the comments which contain the keyword and geoplaces can be
     * returned. Although the class type is FacebookComment, it represents
     * FacebookReply object as in Facebook, a Reply class is implementated by
     * FacebookComment.
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param fbRepliesRsp
     *            an object of class FacebookCommentsResponse
     * @return a list of object of class FacebookComment which contains the
     *         keyword
     */
    protected List<FacebookComment> doFilterComment(final GeoCmdLine geoCmdLine,
            final FacebookCommentsResponse fbRepliesRsp) {
        // the method name is just used to override parent method, actually it
        // is the method doFliterReply.
        // get the reference of FacebookPage of current FacebookComment object.
        // current object is a Comment, fbParent.getParent() is a Post,
        // fbParent.getParent().getParent() is aPage.
        FacebookPage fbPage = (FacebookPage) fbParent.getParent().getParent();
        if (fbRepliesRsp != null && fbRepliesRsp.getDatas() != null) {
            // append fbCommentsRsp.getDatas() into SizeOfRepliesInTotal
            fbPage.setSizeOfRepliesInTotal(fbPage.getSizeOfRepliesInTotal() + fbRepliesRsp.getDatas().length);
        }
        // filter keyword
        List<FacebookComment> hasKeywordReplyList = doFilterKeyword(geoCmdLine, fbRepliesRsp);
        // append hasKeywordReplyList size into SizeOfRepliesHasKeyword
        fbPage.setSizeOfRepliesHasKeyword(fbPage.getSizeOfRepliesHasKeyword() + hasKeywordReplyList.size());
        // filter geoplaces
        List<FacebookComment> hasKeywordAndGeoReplyList = doFilterGeo(hasKeywordReplyList);
        // append hasKeywordAndGeoReplyList size into
        // SizeOfRepliesHasKeywordAndGeo
        fbPage.setSizeOfRepliesHasKeywordAndGeo(
                fbPage.getSizeOfRepliesHasKeywordAndGeo() + hasKeywordAndGeoReplyList.size());
        return hasKeywordAndGeoReplyList;
    }

}
