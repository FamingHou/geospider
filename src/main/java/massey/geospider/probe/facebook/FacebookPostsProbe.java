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
import massey.geospider.message.facebook.FacebookLocation;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.facebook.FacebookPlace;
import massey.geospider.message.facebook.FacebookPost;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPaging;
import massey.geospider.message.response.facebook.FacebookPostsResponse;
import massey.geospider.persistence.dao.SocialMediaRecordDAO;
import massey.geospider.persistence.dao.SocialMediaRecordDAOImpl;
import massey.geospider.persistence.dto.SocialMediaRecord;
import massey.geospider.util.DateHelper;
import massey.geospider.util.JSONHelper;

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

    /** The current facebookPost which is being processed */
    private FacebookPost currentPost;
    /** The parent Facebook page */
    private FacebookPage fbPage;

    /**
     * 
     */
    public FacebookPostsProbe(FacebookPage fbPage) {
        this.fbPage = fbPage;
    }

    @Override
    protected boolean doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doPreCollect()");
        log.info("Fetching all posts of the page " + fbPage.getId());
        if (inputGeoResponse == null)
            log.info("The first page of posts searching...");
        else
            log.info("The next page of posts searching...");
        return true;
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
            log.info("||Notice|| url of fetching posts is null or an empty string");
            return null;
        } else {
            String responseString = HttpHelper.doGetAsync(urlString);
            return createFacebookPostsResponse(responseString);
        }
    }

    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doProcessResponse()");
        // FacebookPosts level
        FacebookPostsResponse fbPostsRsp = (FacebookPostsResponse) inputGeoResponse;
        List<FacebookPost> fbPostList = doFilterPost(geoCmdLine, fbPostsRsp);
        doPersistence(geoCmdLine, fbPostList);
    }

    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPostsProbe#doPostCollect(), fetching comments under one page");
        // FacebookPosts level
        FacebookPostsResponse fbPostsRsp = (FacebookPostsResponse) inputGeoResponse;
        if (fbPostsRsp != null) {
            FacebookPost[] fbPostArray = fbPostsRsp.getDatas();
            for (int i = 0; i < fbPostArray.length; i++) {
                currentPost = fbPostArray[i];
                log.info("fetch all comments of the post ===> " + currentPost.getId());
                // fetch all comments under one FacebookPost
                doCollectAllCommentsOfOnePost(geoCmdLine, currentPost);
            }

        }
    }

    @Override
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        StringBuilder sb = new StringBuilder("currentPostId = ");
        if (currentPost == null)
            sb.append("null");
        else
            sb.append(currentPost.getId());
        log.debug(sb.toString());
        // call collect method recursively to search the next page on posts
        // level.
        collect(geoCmdLine, inputGeoResponse);
    }

    /**
     * Creates the request url for posts searching level.
     * 
     * <pre>
     * The URL is
     * https://graph.facebook.com/v2.11/{<b><i>PAGE_ID</i></b>}/posts?
     * fields=id,message,created_time,place&access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}&pretty=0&limit=25
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
                sb.append(fbPage.getId()).append(SEPARATOR);
                sb.append("posts");
                // using URIBuilder to solve URISyntax issues
                URIBuilder builder = new URIBuilder(sb.toString());
                // Task_20171201_3 add field place to collect geo_location
                builder.addParameter("fields", "id,message,created_time,place");
                builder.addParameter("access_token", PropReader.get(FB_ACCESS_TOKEN_PROP_NAME));
                builder.addParameter("pretty", "0");
                builder.addParameter("limit", PropReader.get(FB_POST_LIMIT_PROP_NAME));
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
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
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
            String createdTime = postObj.isNull("created_time") ? "" : postObj.getString("created_time");
            Timestamp vendorRecordCreatedTime = DateHelper.parse(createdTime, DATETIME_FORMAT_FB);
            FacebookPlace fbPlace = createFBPlace(postObj);
            postArray[i] = new FacebookPost(id, fbPage, message, vendorRecordCreatedTime, fbPlace);
        }
        return postArray;
    }

    /**
     * Fetches all comments of one specific post.
     * 
     * @param geoCmdLine
     * @param fbPost
     */
    protected void doCollectAllCommentsOfOnePost(GeoCmdLine geoCmdLine, FacebookPost fbPost) {
        if (fbPost == null)
            return;
        FacebookCommentsProbe fbCommentsProbe = new FacebookCommentsProbe(fbPost);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbCommentsProbe.collect(geoCmdLine, null);
    }

    /**
     * Only the posts which contain the keyword and GeoPlace can be returned
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param fbPostsRsp
     *            an object of class FacebookPostsResponse
     * @return a list of object of class FacebookPost which contains the keyword
     *         and GeoPlace
     */
    private List<FacebookPost> doFilterPost(final GeoCmdLine geoCmdLine, final FacebookPostsResponse fbPostsRsp) {
        if (fbPostsRsp != null && fbPostsRsp.getDatas() != null) {
            // append the size of fbPostsRsp.getDatas() into SizeOfPostsInTotal
            fbPage.setSizeOfPostsInTotal(fbPage.getSizeOfPostsInTotal() + fbPostsRsp.getDatas().length);
        }
        // filter keyword
        List<FacebookPost> hasKeywordPostList = doFilterKeyword(geoCmdLine, fbPostsRsp);
        // append the size of hasKeywordPostList into SizeOfPostsHasKeyword
        fbPage.setSizeOfPostsHasKeyword(fbPage.getSizeOfPostsHasKeyword() + hasKeywordPostList.size());
        // filter geoplaces
        List<FacebookPost> hasKeywordAndGeoPostList = doFilterGeo(hasKeywordPostList);
        // append the size of hasKeywordAndGeoPostList into
        // SizeOfPostsHasKeywordAndGeo
        fbPage.setSizeOfPostsHasKeywordAndGeo(
                fbPage.getSizeOfPostsHasKeywordAndGeo() + hasKeywordAndGeoPostList.size());
        return hasKeywordAndGeoPostList;
    }

    /**
     * Only the posts which contain the keyword can be returned
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param fbPostsRsp
     *            an object of class FacebookPostsResponse
     * @return a list of object of class FacebookPost which contains the keyword
     */
    private List<FacebookPost> doFilterKeyword(final GeoCmdLine geoCmdLine, final FacebookPostsResponse fbPostsRsp) {
        List<FacebookPost> hasKeywordlist = new ArrayList<>();
        if (fbPostsRsp != null) {
            FacebookPost[] fbPostsArray = fbPostsRsp.getDatas();
            for (int i = 0; i < fbPostsArray.length; i++) {
                if (fbPostsArray[i] != null && fbPostsArray[i].getMessage() != null) {
                    String s1 = fbPostsArray[i].getMessage();
                    String s2 = geoCmdLine.getKeywordOptionValue();
                    // CASE_INSENSITIVE
                    boolean isContain = Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
                    if (isContain) {
                        hasKeywordlist.add(fbPostsArray[i]);
                    }
                }
            }
        }
        return hasKeywordlist;
    }

    /**
     * Only the posts which contain valid geoplace(s) can be returned
     * 
     * @param fbPostList
     * @return
     */
    protected List<FacebookPost> doFilterGeo(List<FacebookPost> fbPostList) {
        List<FacebookPost> hasGeoList = new ArrayList<>();
        for (FacebookPost fbPost : fbPostList) {
            doFilterGeoOne(hasGeoList, fbPost);
        }
        return hasGeoList;
    }

    /**
     * If facebookPost has geo places, add it into hasGeoList
     * 
     * @param hasGeoList
     * @param facebookPost
     */
    protected void doFilterGeoOne(List<FacebookPost> hasGeoList, FacebookPost facebookPost) {
        if (super.hasGeoPlace(facebookPost))
            hasGeoList.add(facebookPost);
    }

    /**
     * Saves objects of class type FacebookPost in fbPostList into database
     * 
     * @param geoCmdLine
     * @param fbPostList
     *            a list which contains objects of class type FacebookPost
     */
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FacebookPost> fbPostList) {
        // @TODO using batch mode for better performance. It is unnecessary for
        // multi-threading solution.
        for (FacebookPost facebookPost : fbPostList) {
            doPersistenceOne(geoCmdLine, facebookPost);
        }
    }

    /**
     * Inserts one facebookPost object into database.
     * 
     * @param geoCmdLine
     * @param facebookPost
     */
    protected void doPersistenceOne(GeoCmdLine geoCmdLine, FacebookPost facebookPost) {
        SocialMediaRecord smRecord = new SocialMediaRecord();
        smRecord.setKeyword(geoCmdLine.getKeywordOptionValue());
        smRecord.setVendorRecordId(facebookPost.getId());
        smRecord.setVendorRecordParentId(facebookPost.getParent().getId());
        smRecord.setMessage(facebookPost.getMessage());
        smRecord.setVendorType(VENDOR_TYPE_FACEBOOK);
        smRecord.setRecordType(RECORD_TYPE_POST);
        smRecord.setVendorRecordCreatedTime(facebookPost.getCreatedTime());
        if (facebookPost.getFbPlace() != null) {
            FacebookPlace fbPlace = facebookPost.getFbPlace();
            smRecord.setPlaceId(fbPlace.getId());
            smRecord.setPlaceName(fbPlace.getName());
            if (fbPlace.getLocation() != null) {
                FacebookLocation fbLoc = fbPlace.getLocation();
                smRecord.setPlaceCity(fbLoc.getCity());
                smRecord.setPlaceCountry(fbLoc.getCountry());
                smRecord.setPlaceZip(fbLoc.getZip());
                smRecord.setPlaceLatitude(fbLoc.getLatitude());
                smRecord.setPlaceLongitude(fbLoc.getLongitude());
            }
        }
        smRecord.setHasGeo(true);
        smRecord.setHasKeyword(true);
        SocialMediaRecordDAO smrDao = new SocialMediaRecordDAOImpl();
        smrDao.insertOne(smRecord);
    }

    /**
     * <pre>
     *       "place": {
            "name": "Oaxaca City, Mexico",
            "location": {
              "city": "Oaxaca de Juárez",
              "country": "Mexico",
              "latitude": 17.05,
              "longitude": -96.7167,
              "zip": "68000"
            },
            "id": "108148499213006"
          }
     * </pre>
     * 
     * @param postObj
     * @return an object of class FacebookPlace
     */
    private FacebookPlace createFBPlace(JSONObject postObj) {
        FacebookPlace fbPlace = new FacebookPlace();
        JSONObject placeObj = postObj.isNull("place") ? null : postObj.getJSONObject("place");
        if (placeObj != null) {
            fbPlace.setId(placeObj.isNull("id") ? "" : placeObj.getString("id"));
            fbPlace.setName(placeObj.isNull("name") ? "" : placeObj.getString("name"));
            JSONObject locObj = placeObj.isNull("location") ? null : placeObj.getJSONObject("location");
            if (locObj != null) {
                FacebookLocation fbLoc = new FacebookLocation();
                fbLoc.setCity(locObj.isNull("city") ? "" : locObj.getString("city"));
                fbLoc.setCountry(locObj.isNull("country") ? "" : locObj.getString("country"));
                fbLoc.setLatitude(locObj.isNull("latitude") ? 0d : locObj.getDouble("latitude"));
                fbLoc.setLongitude(locObj.isNull("longitude") ? 0d : locObj.getDouble("longitude"));
                fbLoc.setZip(locObj.isNull("zip") ? "" : locObj.getString("zip"));
                fbPlace.setLocation(fbLoc);
            }
        }
        return fbPlace;
    }
}
