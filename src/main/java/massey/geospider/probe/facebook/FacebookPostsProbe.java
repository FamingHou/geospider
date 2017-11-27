/**
 * 
 */
package massey.geospider.probe.facebook;

import java.net.URISyntaxException;
import java.sql.Timestamp;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.global.GeoConstants;
import massey.geospider.message.facebook.FacebookPost;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPaging;
import massey.geospider.message.response.facebook.FacebookPostsResponse;

/**
 * 
 * The implementation of Probe interface for Facebook in posts result level.<BR>
 * <BR>
 * 
 * Facebook provides an API to get posts under one specific page by sending a
 * HTTP GET request with parameters <b><i>PAGE_ID</i></b></BR>
 * </BR>
 * 
 * The URL is
 * <a href="">https://graph.facebook.com/v2.11/{<b><i>PAGE_ID</i></b>}/posts?
 * access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}&pretty=0&limit=25</a>
 * 
 * </BR>
 * </BR>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPostsProbe extends FacebookAbstractProbe implements GeoConstants {

    private static final Logger log = Logger.getLogger(FacebookPostsProbe.class);

    private String pageId;

    /**
     * 
     */
    public FacebookPostsProbe(String pageId) {
        this.pageId = pageId;
    }

    @Override
    protected void doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doPreCollect()");
        log.info("Fetching all posts of the page " + pageId);
        if (inputGeoResponse == null)
            log.info("The first page of posts searching...");
        else
            log.info("The next page of posts searching...");
    }

    /**
     * Does HTTP request and parses response JSON based on Facebook APIs in
     * Posts level.
     */
    @Override
    protected FacebookPostsResponse doRequest(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doRequest()");
        FacebookPostsResponse fbPostsRsp = (FacebookPostsResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, fbPostsRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.error("||Notice|| url of fetching posts is null or an empty string");
            return null;
        } else {
            String responseString = HttpHelper.doGet(urlString);
            return createFacebookPostsResponse(responseString);
        }
    }

    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doProcessResponse()");
        // FacebookPosts level
        FacebookPostsResponse fbPostsRsp = (FacebookPostsResponse) inputGeoResponse;
        // @TODO do filter and persistence
    }

    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doPostCollect(), fetching comments under one page");
        // FacebookPosts level
        FacebookPostsResponse fbPostsRsp = (FacebookPostsResponse) inputGeoResponse;
        if (fbPostsRsp != null) {
            FacebookPost[] fbPostArray = fbPostsRsp.getDatas();
            for (int i = 0; i < fbPostArray.length; i++) {
                // fetch all comments under one FacebookPost
                doCollectAllCommentsOfOnePost(fbPostArray[i]);
            }

        }
    }

    @Override
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doNextPageCollect()");

    }

    /**
     * Creates the request url for posts searching level.
     * 
     * <pre>
     * The URL is
     * https://graph.facebook.com/v2.11/{<b><i>PAGE_ID</i></b>}/posts?access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}&pretty=0&limit=25
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param fbPostsRsp
     *            FacebookPostsResponse object.
     * @return
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final FacebookPostsResponse fbPostsRsp) {
        if (fbPostsRsp == null) {
            // build url by the keyword which is from geoCmdLine.
            try {
                log.debug("url is generated by pageId for the first searching");
                StringBuilder sb = new StringBuilder();
                sb.append(PropReader.get(FB_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
                sb.append(PropReader.get(FB_VERSION_PROP_NAME)).append(SEPARATOR);
                sb.append(pageId).append(SEPARATOR);
                sb.append("posts");
                // using URIBuilder to solve URISyntax issues
                URIBuilder builder = new URIBuilder(sb.toString());
                builder.addParameter("access_token", PropReader.get(FB_ACCESS_TOKEN_PROP_NAME));
                builder.addParameter("pretty", "0");
                builder.addParameter("limit", PropReader.get(FB_PAGE_LIMIT_PROP_NAME));
                log.info(builder.toString());
                return builder.toString();
            } catch (URISyntaxException e) {
                log.error(e, e);
                return null;
            }
        } else {
            log.debug("url is generated by fbPagesRsp for the next page searching");
            String urlString = fbPostsRsp.getPaging() == null ? null : fbPostsRsp.getPaging().getNextURL();
            log.info(urlString);
            return urlString;
        }
    }

    /**
     * Parses response JSON String and creates a FacebookPostsResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type FacebookPostsResponse
     */
    private FacebookPostsResponse createFacebookPostsResponse(String responseString) {
        if (responseString == null)
            return null;
        JSONObject jsonObj = new JSONObject(responseString);
        FacebookPost[] datas = parseDatas(jsonObj);
        FacebookError error = parseError(jsonObj);
        FacebookPaging paging = parsePaging(jsonObj);
        return new FacebookPostsResponse(datas, error, paging);
    }

    /**
     * Parses the following JSON data array and creates an array of class type
     * FacebookPost
     * 
     * <pre>
     *   "data": [
    {
      "created_time": "2017-11-23T07:42:19+0000",
      "message": "A light earthquake magnitude 4.7 (ml/mb) strikes 64 km W of Zhamog, China (40 miles) on Thursday. The temblor was picked up at 15:13:47/3:13 pm (local time epicenter) at a depth of 10 km (6 miles). Did you feel it? Was there any damage?
    #‎earthquake‬",
      "id": "195551580614605_853969938106096"
    },
    {
      "created_time": "2017-11-23T03:09:21+0000",
      "message": "A light earthquake with magnitude 4.1 (ml/mb) was reported 118 km NW of Bandar ‘Abbas, Iran (73 miles) on Thursday. The temblor was picked up at 06:17:23/6:17 am (local time epicenter) at a depth of 10 km (6 miles). How would you describe the shaking?
    #‎earthquake‬",
      "id": "195551580614605_853869968116093"
    },
    {
      "created_time": "2017-11-23T02:55:26+0000",
      "message": "A minor earthquake with magnitude 2.92 (ml/mb) was detected on Wednesday near Ridgecrest, Alta Sierra, United States. The temblor was reported at 18:45:36/6:45 pm (local time epicenter) at a depth of 2.98 km (2 miles). How did you react? Did you feel it?
    #‎earthquake‬",
      "id": "195551580614605_853864614783295"
    }
    ],
     * </pre>
     * 
     * @param jsonObj
     *            JSONObject
     * @return FacebookPost[]
     *         <ul>
     *         <li>if jsonObj is null or empty, return null</li>
     *         <li>if there is no JSONArray under name tag "data", return
     *         null</li>
     *         </ul>
     */
    private FacebookPost[] parseDatas(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("data"))
            return null;
        JSONArray dataArray = jsonObj.getJSONArray("data");
        int n = dataArray.length();
        FacebookPost[] postArray = new FacebookPost[n];
        for (int i = 0; i < n; ++i) {
            JSONObject postObj = dataArray.getJSONObject(i);
            String id = postObj.isNull("id") ? "" : postObj.getString("id");
            String message = postObj.isNull("message") ? "" : postObj.getString("message");
            Timestamp createdTime = null; // @TODO
            postArray[i] = new FacebookPost(id, pageId, message, createdTime);
        }
        return postArray;
    }

    /**
     * Fetches all comments of one specific post.
     * 
     * @param fbPost
     */
    private void doCollectAllCommentsOfOnePost(FacebookPost fbPost) {
        if (fbPost == null)
            return;
        FacebookCommentsProbe fbCommentsProbe = new FacebookCommentsProbe(fbPost.getId());
        // geoCmdLine is null as no input argument value is needed
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbCommentsProbe.collect(null, null);
    }
}