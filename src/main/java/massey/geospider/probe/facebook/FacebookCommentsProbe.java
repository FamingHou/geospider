/**
 * 
 */
package massey.geospider.probe.facebook;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.global.GeoConstants;
import massey.geospider.message.facebook.FacebookComment;
import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookCommentsResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPaging;
import massey.geospider.persistence.dao.SocialMediaRecordDAO;
import massey.geospider.persistence.dao.SocialMediaRecordDAOImpl;
import massey.geospider.persistence.dto.SocialMediaRecord;
import massey.geospider.util.DateHelper;
import massey.geospider.util.JSONHelper;

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

    /** It could be a FacebookPost or a FacebookComment */
    protected FacebookMessage fbParent;

    /**
     * 
     */
    public FacebookCommentsProbe(FacebookMessage fbParent) {
        this.fbParent = fbParent;
    }

    @Override
    protected boolean doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doPreCollect()");
        log.info("Fetching all comments of the post " + fbParent.getId());
        if (inputGeoResponse == null)
            log.info("The first page of comments searching...");
        else
            log.info("The next page of comments searching...");
        return true;
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
            log.info("||Notice|| url of fetching comments is null or an empty string");
            return null;
        } else {
            String responseString = HttpHelper.doGetAsync(urlString);
            return createFacebookCommentsResponse(responseString);
        }
    }

    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doProcessResponse()");
        // FacebookComments level
        FacebookCommentsResponse fbCommentsRsp = (FacebookCommentsResponse) inputGeoResponse;
        List<FacebookComment> fbCommentList = doFilterComment(geoCmdLine, fbCommentsRsp);
        doPersistence(geoCmdLine, fbCommentList);
    }

    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doPostCollect(), fetching all replies under one comment");
        // FacebookComments level
        FacebookCommentsResponse fbCommentsRsp = (FacebookCommentsResponse) inputGeoResponse;
        if (fbCommentsRsp != null) {
            FacebookComment[] fbCommentArray = fbCommentsRsp.getDatas();
            for (int i = 0; i < fbCommentArray.length; i++) {
                // fetch all replies under one FacebookComment.
                // a reply is still a type of class FacebookComment.
                doCollectAllRepliesOfOneComment(geoCmdLine, fbCommentArray[i]);
            }
        }
    }

    @Override
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbe#doNextPageCollect()");
        // call collect method recursively to search the next page on comments
        // level.
        collect(geoCmdLine, inputGeoResponse);
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
            try {
                log.debug("url is generated by postId for the first searching");
                StringBuilder sb = new StringBuilder();
                sb.append(PropReader.get(FB_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
                sb.append(PropReader.get(FB_VERSION_PROP_NAME)).append(SEPARATOR);
                sb.append(fbParent.getId()).append(SEPARATOR);
                sb.append("comments");
                // use URIBuilder to solve URISyntax issues
                URIBuilder builder = new URIBuilder(sb.toString());
                builder.addParameter("access_token", PropReader.get(FB_ACCESS_TOKEN_PROP_NAME));
                builder.addParameter("pretty", "0");
                builder.addParameter("limit", PropReader.get(FB_COMMENT_LIMIT_PROP_NAME));
                log.info(builder.toString());
                return builder.toString();
            } catch (URISyntaxException e) {
                log.error(e, e);
                return null;
            }
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
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
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
            JSONObject commentObj = dataArray.getJSONObject(i);
            String id = commentObj.isNull("id") ? "" : commentObj.getString("id");
            String message = commentObj.isNull("message") ? "" : commentObj.getString("message");
            String createdTime = commentObj.isNull("created_time") ? "" : commentObj.getString("created_time");
            Timestamp vendorRecordCreatedTime = DateHelper.parse(createdTime, DATETIME_FORMAT_FB);
            postArray[i] = new FacebookComment(id, fbParent, message, vendorRecordCreatedTime);
        }
        return postArray;
    }

    /**
     * Fetches all replies of one specific comment.
     * 
     * @param geoCmdLine
     * @param fbComment
     */
    protected void doCollectAllRepliesOfOneComment(GeoCmdLine geoCmdLine, FacebookComment fbComment) {
        if (fbComment == null)
            return;
        FacebookRepliesProbe fbRepliesProbe = new FacebookRepliesProbe(fbComment);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbRepliesProbe.collect(geoCmdLine, null);
    }

    /**
     * Only the comments which contain the keyword and geoplaces can be returned
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param fbCommentsRsp
     *            an object of class FacebookCommentsResponse
     * @return a list of object of class FacebookComment which contains the
     *         keyword
     */
    protected List<FacebookComment> doFilterComment(final GeoCmdLine geoCmdLine,
            final FacebookCommentsResponse fbCommentsRsp) {
        // get the reference of FacebookPage of current FacebookPost object.
        FacebookPage fbPage = (FacebookPage) fbParent.getParent();
        if (fbCommentsRsp != null && fbCommentsRsp.getDatas() != null) {
            // append fbCommentsRsp.getDatas() into SizeOfCommentsInTotal
            fbPage.setSizeOfCommentsInTotal(fbPage.getSizeOfCommentsInTotal() + fbCommentsRsp.getDatas().length);
        }
        // filter keyword
        List<FacebookComment> hasKeywordCommentList = doFilterKeyword(geoCmdLine, fbCommentsRsp);
        // append hasKeywordCommentList size into SizeOfCommentsHasKeyword
        fbPage.setSizeOfCommentsHasKeyword(fbPage.getSizeOfCommentsHasKeyword() + hasKeywordCommentList.size());
        // filter geoplaces
        List<FacebookComment> hasKeywordAndGeoCommentList = doFilterGeo(hasKeywordCommentList);
        // append hasKeywordAndGeoCommentList size into
        // SizeOfCommentsHasKeywordAndGeo
        fbPage.setSizeOfCommentsHasKeywordAndGeo(
                fbPage.getSizeOfCommentsHasKeywordAndGeo() + hasKeywordAndGeoCommentList.size());
        return hasKeywordAndGeoCommentList;
    }

    /**
     * Only the comments which contain the keyword can be returned
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param fbCommentsRsp
     *            an object of class FacebookCommentsResponse
     * @return a list of object of class Facebookcomment which contains the
     *         keyword
     */
    protected List<FacebookComment> doFilterKeyword(final GeoCmdLine geoCmdLine,
            final FacebookCommentsResponse fbCommentsRsp) {
        List<FacebookComment> hasKeywordlist = new ArrayList<>();
        if (fbCommentsRsp != null) {
            FacebookComment[] fbCommentsArray = fbCommentsRsp.getDatas();
            for (int i = 0; i < fbCommentsArray.length; i++) {
                if (fbCommentsArray[i] != null && fbCommentsArray[i].getMessage() != null) {
                    String s1 = fbCommentsArray[i].getMessage();
                    String s2 = geoCmdLine.getKeywordOptionValue();
                    // CASE_INSENSITIVE
                    boolean isContain = Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
                    if (isContain) {
                        hasKeywordlist.add(fbCommentsArray[i]);
                    }
                }
            }
        }
        return hasKeywordlist;
    }

    /**
     * Only the comments which contain valid geoplace(s) can be returned
     * 
     * @param fbCommentList
     * @return
     */
    protected List<FacebookComment> doFilterGeo(List<FacebookComment> fbCommentList) {
        List<FacebookComment> hasGeoList = new ArrayList<>();
        for (FacebookComment fbComment : fbCommentList) {
            doFilterGeoOne(hasGeoList, fbComment);
        }
        return hasGeoList;
    }

    /**
     * If facebookComment has geo places, add it into hasGeoList
     * 
     * @param hasGeoList
     * @param facebookComment
     */
    protected void doFilterGeoOne(List<FacebookComment> hasGeoList, FacebookComment facebookComment) {
        if (super.hasGeoPlace(facebookComment))
            hasGeoList.add(facebookComment);
    }

    /**
     * Saves objects of class type FacebookComment in fbCommentList into
     * database
     * 
     * @param geoCmdLine
     * @param fbCommentList
     *            a list which contains objects of class type FacebookComment
     */
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FacebookComment> fbCommentList) {
        // @TODO using batch mode for better performance
        for (FacebookComment facebookComment : fbCommentList) {
            doPersistenceOne(geoCmdLine, facebookComment);
        }
    }

    /**
     * Inserts into one FacebookComment object into table social_media_record
     * 
     * @param geoCmdLine
     * @param facebookComment
     */
    protected void doPersistenceOne(GeoCmdLine geoCmdLine, FacebookComment facebookComment) {
        SocialMediaRecord smRecord = new SocialMediaRecord();
        smRecord.setKeyword(geoCmdLine.getKeywordOptionValue());
        smRecord.setVendorRecordId(facebookComment.getId());
        smRecord.setVendorRecordParentId(facebookComment.getParent().getId());
        smRecord.setMessage(facebookComment.getMessage());
        smRecord.setVendorType(VENDOR_TYPE_FACEBOOK);
        smRecord.setRecordType(getRecordType());
        smRecord.setVendorRecordCreatedTime(facebookComment.getCreatedTime());
        SocialMediaRecordDAO smrDao = new SocialMediaRecordDAOImpl();
        smrDao.insertOne(smRecord);
    }

    /**
     * 
     * @return 3 which means this is a comment
     */
    protected int getRecordType() {
        return RECORD_TYPE_COMMENT;
    }
}
