/**
 * 
 */
package massey.geospider.probe.flickr;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.message.flickr.FlickrComment;
import massey.geospider.message.flickr.FlickrPhoto;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.flickr.FlickrCommentsOfOnePhotoResponse;
import massey.geospider.util.DateHelper;
import massey.geospider.util.JSONHelper;
import massey.geospider.util.StringHelper;

/**
 * Request URL:
 * 
 * <pre>
 * https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList&api_key=fd6841a5030427fb74060d876fe7d580&photo_id=4105054854&format=json&nojsoncallback=1
 * </pre>
 * 
 * Response:
 * 
 * <pre>
 * {
    "comments": {
        "photo_id": "4105054854",
        "comment": [
            {
                "id": "2149990-4105054854-72157622680956043",
                "author": "7359335@N02",
                "author_is_deleted": 0,
                "authorname": "Lee Sie",
                "iconserver": "8126",
                "iconfarm": 9,
                "datecreate": "1258260705",
                "permalink": "https://www.flickr.com/photos/stuckincustoms/4105054854/#comment72157622680956043",
                "path_alias": "lee_sie",
                "realname": "Lee Sie",
                "_content": "Wow this place looks amazing!"
            },
            ...
        ]
    },
    "stat": "ok"
}
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FlickrShowCommentsOfOnePhotoProbe extends FlickrAbstractProbe {

    private static final Logger log = Logger.getLogger(FlickrShowCommentsOfOnePhotoProbe.class);

    /** The parent probe of this probe */
    private FlickrSearchProbe flickrSearchProbe;
    /** The id of the photo */
    private String photoId;

    /**
     * 
     * @param flickrSearchProbe
     * @param photoId
     */
    public FlickrShowCommentsOfOnePhotoProbe(FlickrSearchProbe flickrSearchProbe, String photoId) {
        this.flickrSearchProbe = flickrSearchProbe;
        this.photoId = photoId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.flickr.FlickrAbstractProbe#onCollectEnd(massey.
     * geospider.boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info(new StringBuilder().append("Comments of photo ").append(photoId).append(" were fetched."));
    }

    /*
     * Fetches all comments of one photo
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doRequest(massey.geospider.boot.
     * GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected GeoResponse doRequest(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("#doRequest()");
        FlickrCommentsOfOnePhotoResponse commentsRsp = (FlickrCommentsOfOnePhotoResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, commentsRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.info("url of searching comments of one photo is null or an empty string");
            return null;
        } else {
            // do http request
            String responseString = HttpHelper.doGetAsync(urlString);
            return createCommentsOfOnePhotoResponse(responseString);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doProcessResponse(massey.geospider.
     * boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        FlickrCommentsOfOnePhotoResponse commentsRsp = (FlickrCommentsOfOnePhotoResponse) inputGeoResponse;
        // do filter comments
        List<FlickrComment> flickrCommentList = doFilterSearchResult(geoCmdLine, commentsRsp);
        //
        doPersistence(geoCmdLine, flickrCommentList);
    }

    /**
     * Creates the request url for flickr.photos.comments.getList API of Flickr.
     * 
     * <pre>
     * The URL is: <BR>
     * https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList&api_key=fd6841a5030427fb74060d876fe7d580&photo_id=4105054854&format=json&nojsoncallback=1
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param commentsRsp
     *            FlickrCommentsOfOnePhotoResponse object.
     * @return if commentsRsp is null, URL is built from geoCmdLine; otherwise,
     *         URL is from commentsRsp
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final FlickrCommentsOfOnePhotoResponse commentsRsp) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PropReader.get(FLICKR_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
            // use URIBuilder to solve URISyntax issues
            URIBuilder uriBuilder = new URIBuilder(sb.toString());
            uriBuilder.addParameter("method", "flickr.photos.comments.getList");
            uriBuilder.addParameter("api_key", PropReader.get(FLICKR_API_KEY_PROP_NAME));
            uriBuilder.addParameter("photo_id", photoId);
            uriBuilder.addParameter("format", "json");
            uriBuilder.addParameter("nojsoncallback", "1");
            log.info(uriBuilder.toString());
            return uriBuilder.toString();
        } catch (URISyntaxException e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Parses response JSON String and creates a
     * FlickrCommentsOfOnePhotoResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type FlickrCommentsOfOnePhotoResponse
     */
    private FlickrCommentsOfOnePhotoResponse createCommentsOfOnePhotoResponse(String responseString) {
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
        JSONObject commentsObj = JSONHelper.getJSONObj(jsonObj, "comments");
        // commentsObj will not be null according to the test on Flickr
        FlickrComment[] commentArray = parseComments(commentsObj);
        String photoId = JSONHelper.get(commentsObj, "photo_id");
        return new FlickrCommentsOfOnePhotoResponse(photoId, commentArray);
    }

    /**
     * Parses comments of the response and converts them into an array of
     * FlickrComment.
     * 
     * @param jsonObj
     * @return
     */
    private FlickrComment[] parseComments(JSONObject jsonObj) {
        JSONArray commentArrayObj = JSONHelper.getJSONArray(jsonObj, "comment");
        if (commentArrayObj == null)
            return null;
        int n = commentArrayObj.length();
        FlickrComment[] commentArray = new FlickrComment[n];
        for (int i = 0; i < n; ++i) {
            JSONObject commentObj = commentArrayObj.getJSONObject(i);
            commentArray[i] = parseOneComment(commentObj);
        }
        return commentArray;
    }

    /**
     * Parses one commentObj in json format into an object of FlickrComment
     * 
     * @param commentObj
     * @return
     */
    private FlickrComment parseOneComment(JSONObject commentObj) {
        String id = JSONHelper.get(commentObj, "id"); // id
        String text = JSONHelper.get(commentObj, "_content"); // _content
        long createdAtLong = JSONHelper.getLong(commentObj, "datecreate"); // datecreate
        Timestamp createdAt = DateHelper.parse(createdAtLong);
        FlickrComment comment = new FlickrComment();
        comment.setId(id);
        comment.setText(text);
        comment.setCreatedAt(createdAt);
        return comment;
    }

    /**
     * Only objects of type FlickrComment which contain GeoPlace can be
     * returned.
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param flickrCommentsRsp
     *            an object of class FlickrCommentsOfOnePhotoResponse
     * @return a list of object of class FlickrComment which contains the
     *         keyword and GeoPlace
     */
    private List<FlickrComment> doFilterSearchResult(final GeoCmdLine geoCmdLine,
            final FlickrCommentsOfOnePhotoResponse flickrCommentsRsp) {
        FlickrComment[] commentArray = flickrCommentsRsp.getCommentArray();
        flickrSearchProbe.sizeOfCommentsInTotal += commentArray == null ? 0 : commentArray.length;
        // filter comments
        List<FlickrComment> commentList = doFilterKeyword(geoCmdLine, flickrCommentsRsp);
        flickrSearchProbe.sizeOfCommentsHasKeyword += commentList.size();
        // filter geoplaces
        List<FlickrComment> hasGeoCommentList = doFilterGeo(commentList);
        flickrSearchProbe.sizeOfCommentsHasKeywordAndGeo += hasGeoCommentList.size();
        return hasGeoCommentList;
    }

    /**
     * Checks whether the comment has the keyword or not
     * 
     * @param geoCmdLine
     * @param flickrCommentsRsp
     * @return
     */
    private List<FlickrComment> doFilterKeyword(final GeoCmdLine geoCmdLine,
            final FlickrCommentsOfOnePhotoResponse flickrCommentsRsp) {
        List<FlickrComment> commentList = new ArrayList<>();
        if (flickrCommentsRsp != null) {
            FlickrComment[] commentArray = flickrCommentsRsp.getCommentArray();
            for (int i = 0; i < commentArray.length; ++i) {
                if (StringHelper.hasKeyword(commentArray[i].getText(), geoCmdLine.getKeywordOptionValue())) {
                    commentList.add(commentArray[i]);
                }
            }
        }
        return commentList;
    }

    /**
     * Only the comments which contain valid geoplace(s) can be returned
     * 
     * @param flickrCommentList
     * @return
     */
    private List<FlickrComment> doFilterGeo(List<FlickrComment> flickrCommentList) {
        List<FlickrComment> hasGeoList = new ArrayList<>();
        for (FlickrComment flickrComment : flickrCommentList) {
            if (super.hasGeoPlace(flickrComment.getText()))
                hasGeoList.add(flickrComment);
        }
        return hasGeoList;
    }

    /**
     * Saves objects of class FlickrComment into database
     * 
     * @param geoCmdLine
     * @param flickrCommentList
     *            a list which contains objects of class FlickrComment
     */
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FlickrComment> flickrCommentList) {
        for (FlickrComment flickrComment : flickrCommentList) {
            doPersistenceOne(geoCmdLine, flickrComment, RECORD_TYPE_COMMENT, true, true);
        }
    }

}
