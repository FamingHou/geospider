/**
 * 
 */
package massey.geospider.probe.facebook;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.global.GeoConstants;
import massey.geospider.message.facebook.FacebookComment;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookCommentsResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPaging;

/**
 * 
 * The implementation of Probe interface for Facebook in comments result
 * level.<BR>
 * <BR>
 * 
 * Facebook provides an API to get comments under one specific post by sending a
 * HTTP GET request with parameters <b><i>POST_ID</i></b></BR>
 * </BR>
 * 
 * The URL is
 * <a href="">https://graph.facebook.com/v2.11/{<b><i>POST_ID</i></b>}/comments?
 * access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}&pretty=0&limit=25</a>
 * 
 * </BR>
 * </BR>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookCommentsProbe extends FacebookAbstractProbe implements GeoConstants {

    private static final Logger log = Logger.getLogger(FacebookCommentsProbe.class);

    protected String postId;

    /**
     * 
     */
    public FacebookCommentsProbe(String postId) {
        this.postId = postId;
    }

    @Override
    protected void doPreCollect() {
        log.debug("FacebookCommentsProbe#doPreCollect()");
        log.info("Fetching all comments of the post " + postId);
    }

    /**
     * Does HTTP request and parses response JSON based on Facebook APIs in
     * Comments level
     */
    @Override
    protected GeoResponse doRequest(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doRequest()");
        FacebookCommentsResponse fbCommentsRsp = (FacebookCommentsResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, fbCommentsRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.error("||Notice|| url of fetching comments is null or an empty string");
            return null;
        } else {
            String responseString = HttpHelper.doGet(urlString);
            return createFacebookCommentsResponse(responseString);
        }
    }

    @Override
    protected void doProcessResponse(GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doProcessResponse()");
        // FacebookComments level
        FacebookCommentsResponse fbCommentsRsp = (FacebookCommentsResponse) inputGeoResponse;
        // @TODO do filter and persistence

    }

    @Override
    protected void doPostCollect(GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doPostCollect(), fetching all replies under one comment");
        // FacebookComments level
        FacebookCommentsResponse fbCommentsRsp = (FacebookCommentsResponse) inputGeoResponse;
        if (fbCommentsRsp != null) {
            FacebookComment[] fbCommentArray = fbCommentsRsp.getDatas();
            for (int i = 0; i < fbCommentArray.length; i++) {
                // fetch all replies under one FacebookComment.
                // a reply is still a type of class FacebookComment.
                doCollectAllRepliessOfOneComment(fbCommentArray[i]);
            }
        }
    }

    @Override
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doNextPageCollect()");

    }

    /**
     * Creates the request url for comments searching level.
     * 
     * <pre>
     * The URL is
     * https://graph.facebook.com/v2.11/{<b><i>POST_ID</i></b>}/comments?access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}&pretty=0&limit=25
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param fbCommentsRsp
     *            FacebookCommentsResponse object.
     * @return
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final FacebookCommentsResponse fbCommentsRsp) {
        if (fbCommentsRsp == null) {
            // build url by the keyword which is from geoCmdLine.
            log.debug("url is generated by postId for the first searching");
            StringBuilder urlString = new StringBuilder();
            urlString.append(FB_DOMAIN_NAME).append(SEPARATOR);
            urlString.append(FB_VERSION).append(SEPARATOR);
            urlString.append(postId).append(SEPARATOR);
            urlString.append("comments?access_token=").append(FB_ACCESS_TOKEN);
            urlString.append("&pretty=0&limit=").append(FB_PAGE_LIMIT);
            log.info(urlString);
            return urlString.toString();
        } else {
            log.debug("url is generated by fbPostsRsp for the next page searching");
            String urlString = fbCommentsRsp.getPaging() == null ? null : fbCommentsRsp.getPaging().getNextURL();
            log.info(urlString);
            return urlString;
        }
    }

    /**
     * Parses response JSON String and creates a FacebookCommentsResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type FacebookCommentsResponse
     */
    private FacebookCommentsResponse createFacebookCommentsResponse(String responseString) {
        if (responseString == null)
            return null;
        JSONObject jsonObj = new JSONObject(responseString);
        FacebookComment[] datas = parseDatas(jsonObj);
        FacebookError error = parseError(jsonObj);
        FacebookPaging paging = parsePaging(jsonObj);
        return new FacebookCommentsResponse(datas, error, paging);
    }

    /**
     * Parses the following JSON data array and creates an array of class type
     * FacebookComment
     * 
     * <pre>
     *   "data": [
    {
      "created_time": "2017-11-22T01:27:07+0000",
      "message": "😍",
      "id": "1946695855346650_1946714192011483"
    }
    ]
     * </pre>
     * 
     * @param jsonObj
     *            JSONObject
     * @return FacebookComment[]
     *         <ul>
     *         <li>if jsonObj is null or empty, return null</li>
     *         <li>if there is no JSONArray under name tag "data", return
     *         null</li>
     *         </ul>
     */
    private FacebookComment[] parseDatas(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("data"))
            return null;
        JSONArray dataArray = jsonObj.getJSONArray("data");
        int n = dataArray.length();
        FacebookComment[] postArray = new FacebookComment[n];
        for (int i = 0; i < n; ++i) {
            JSONObject postObj = dataArray.getJSONObject(i);
            String id = postObj.isNull("id") ? "" : postObj.getString("id");
            String message = postObj.isNull("message") ? "" : postObj.getString("message");
            Timestamp createdTime = null; // @TODO
            postArray[i] = new FacebookComment(id, postId, message, createdTime);
        }
        return postArray;
    }

    /**
     * Fetches all replies of one specific comment.
     * 
     * @param fbComment
     */
    private void doCollectAllRepliessOfOneComment(FacebookComment fbComment) {
        if (fbComment == null)
            return;
        FacebookRepliesProbe fbCommentsProbe = new FacebookRepliesProbe(fbComment.getId());
        // geoCmdLine is null as no input argument value is needed
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbCommentsProbe.collect(null, null);
    }
}
