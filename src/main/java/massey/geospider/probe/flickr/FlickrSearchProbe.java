package massey.geospider.probe.flickr;

import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.message.flickr.FlickrPhoto;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.flickr.FlickrAbstractResponse;
import massey.geospider.message.response.flickr.FlickrSearchResponse;
import massey.geospider.util.DateHelper;
import massey.geospider.util.JSONHelper;

/**
 * FlickrSearchProbe is responsible for fetching all photos and all comments of
 * each photo.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FlickrSearchProbe extends FlickrAbstractProbe {

    private static final Logger log = Logger.getLogger(FlickrSearchProbe.class);

    /** In Flickr, Photo is mapped into Post, Comment to Comment, no Reply */

    /** The number of photos in total, Post level */
    protected int sizeOfPhotosInTotal = 0;
    /** The number of photos that has keyword. */
    protected int sizeOfPhotosHasKeyword = 0;
    /** The number of photos that has both keyword and geoplaces */
    protected int sizeOfPhotosHasKeywordAndGeo = 0;

    /** The number of comments in total, Comment level */
    protected int sizeOfCommentsInTotal = 0;
    /** The number of comments that has keyword. */
    protected int sizeOfCommentsHasKeyword = 0;
    /** The number of comments that has both keyword and geoplaces */
    protected int sizeOfCommentsHasKeywordAndGeo = 0;

    /**
     * Is this class is called recursively? If yes, isRecursive = true;
     * otherwise false.
     */
    private boolean isRecursive = false;

    public FlickrSearchProbe() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doPreCollect(massey.geospider.boot.
     * GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected boolean doPreCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info("Fetching all photos filtered by the keyword [" + geoCmdLine.getKeywordOptionValue() + "]");
        if (!isRecursive) {
            // this method is called for the first time.
            boolean isProcessing = doProcessOneSearchLogic(geoCmdLine);
            isRecursive = true; //
            return isProcessing; // keep processing or stop
        }
        return true;
    }

    /**
     * Does HTTP request and parses response JSON based on Flickr search API
     */
    @Override
    protected GeoResponse doRequest(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FlickrSearchProbe#doRequest()");
        FlickrSearchResponse flickrRsp = (FlickrSearchResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, flickrRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.info("url of searching Flickr is null or an empty string");
            return null;
        } else {
            // do http request
            String responseString = HttpHelper.doGetAsync(urlString);
            return createFlickrSearchResponse(responseString);
        }
    }

    /**
     * Counts the number of photos/comments of all Flickr photoes, the number of
     * photos/comments which have GeoPlace, and saves them into database.
     * 
     */
    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        FlickrSearchResponse flickrSearchRsp = (FlickrSearchResponse) inputGeoResponse;
        // do filter photos
        List<FlickrPhoto> flickrPhotoList = doFilterSearchResult(geoCmdLine, flickrSearchRsp);
        //
        doPersistence(geoCmdLine, flickrPhotoList);
    }

    /*
     * Fetching all comments of each photo
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doPostCollect(massey.geospider.boot.
     * GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        // FlickrComments level
        FlickrSearchResponse flickrSearchRsp = (FlickrSearchResponse) inputGeoResponse;
        if (flickrSearchRsp != null) {
            FlickrPhoto[] photoArray = flickrSearchRsp.getPhotoArray();
            for (int i = 0; i < photoArray.length; i++) {
                doCollectAllCommentsOfOnePhoto(geoCmdLine, photoArray[i]);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doNextPageCollect(massey.geospider.
     * boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doNextPageCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("#doNextPageCollect()");
        // call method collect recursively
        collect(geoCmdLine, inputGeoResponse);
    }

    @Override
    protected void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FlickrSearchProbe#onCollectEnd()");
        log.info("update stats_page...");
        //
        updateStatsPage(geoCmdLine);
    }

    /**
     * Creates the request url for search API of Flickr.
     * 
     * <pre>
     * The URL is: <BR>
     *  https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=your_key&text=earthquake&sort=date-posted-desc&extras=description,date_upload,last_update,date_taken,geo,tags&page=1&per_page=50&format=json&nojsoncallback=1
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param flickrSearchRsp
     *            FlickrSearchResponse object.
     * @return if twSearchRsp is null, URL is built from geoCmdLine; otherwise,
     *         URL is from twSearchRsp
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final FlickrSearchResponse flickrSearchRsp) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PropReader.get(FLICKR_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
            // use URIBuilder to solve URISyntax issues
            URIBuilder uriBuilder = new URIBuilder(sb.toString());
            uriBuilder.addParameter("method", "flickr.photos.search");
            uriBuilder.addParameter("api_key", PropReader.get(FLICKR_API_KEY_PROP_NAME));
            uriBuilder.addParameter("text", geoCmdLine.getKeywordOptionValue());
            // sorted by dateupload
            uriBuilder.addParameter("sort", "date-posted-desc");
            uriBuilder.addParameter("extras", "description,date_upload,last_update,date_taken,geo,tags");
            if (flickrSearchRsp == null)
                uriBuilder.addParameter("page", "1");
            else
                uriBuilder.addParameter("page", String.valueOf(flickrSearchRsp.getPage() + 1));
            uriBuilder.addParameter("per_page", PropReader.get(FLICKR_PER_PAGE_PROP_NAME));
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
     * Parses response JSON String and creates a FlickrAbstractResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type FlickrAbstractResponse
     */
    private FlickrAbstractResponse createFlickrSearchResponse(String responseString) {
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
        JSONObject photosObj = JSONHelper.getJSONObj(jsonObj, "photos");
        FlickrPhoto[] photos = parsePhotos(photosObj);
        int page = JSONHelper.getInt(photosObj, "page");
        int pages = JSONHelper.getInt(photosObj, "pages");
        int total = JSONHelper.getInt(photosObj, "total");
        return new FlickrSearchResponse(photos, page, pages, total);
    }

    /**
     * Parses JSON data array of response and creates an array of class type
     * FlickrPhoto
     * 
     * @param photosObj
     * @return FlickrPhoto[]
     */
    protected FlickrPhoto[] parsePhotos(JSONObject photosObj) {
        JSONArray dataArray = JSONHelper.getJSONArray(photosObj, "photo");
        if (dataArray == null)
            return null;
        int n = dataArray.length();
        FlickrPhoto[] photoArray = new FlickrPhoto[n];
        for (int i = 0; i < n; ++i) {
            JSONObject photoObj = dataArray.getJSONObject(i);
            photoArray[i] = parseOnePhoto(photoObj);
        }
        return photoArray;
    }

    /**
     * Converts JSONObject into a FlickrPhoto Object
     * 
     * @param photoObj
     * @return
     */
    protected FlickrPhoto parseOnePhoto(JSONObject photoObj) {
        String id = JSONHelper.get(photoObj, "id");
        String text = "";
        JSONObject descObj = JSONHelper.getJSONObj(photoObj, "description");
        if (descObj != null) {
            text = JSONHelper.get(descObj, "_content");
        }
        String tags = JSONHelper.get(photoObj, "tags");
        long createdAtLong = JSONHelper.getLong(photoObj, "dateupload");
        Timestamp createdAt = DateHelper.parse(createdAtLong);
        // latitude & longitude
        double latitude = JSONHelper.getDouble(photoObj, "latitude");
        double longitude = JSONHelper.getDouble(photoObj, "longitude");
        FlickrPhoto photo = new FlickrPhoto();
        photo.setId(id);
        photo.setText(text);
        photo.setTags(tags);
        photo.setCreatedAt(createdAt);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
        return photo;
    }

    /**
     * Only objects of type FlickrPhoto which contain GeoPlace can be returned.
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param flickrSearchRsp
     *            an object of class FlickrSearchResponse
     * @return a list of object of class FlickrPhoto which contains the keyword
     *         and GeoPlace
     */
    private List<FlickrPhoto> doFilterSearchResult(final GeoCmdLine geoCmdLine,
            final FlickrSearchResponse flickrSearchRsp) {
        FlickrPhoto[] photoArray = flickrSearchRsp.getPhotoArray();
        this.sizeOfPhotosInTotal += photoArray == null ? 0 : photoArray.length;
        // filter photo
        List<FlickrPhoto> photoList = doFilterKeyword(geoCmdLine, flickrSearchRsp);
        this.sizeOfPhotosHasKeyword += photoList.size();
        // filter geoplaces
        List<FlickrPhoto> hasGeoPhotoList = doFilterGeo(photoList);
        this.sizeOfPhotosHasKeywordAndGeo += hasGeoPhotoList.size();
        return hasGeoPhotoList;
    }

    /**
     * Checks whether the message has the keyword or not
     * 
     * @param geoCmdLine
     * @param flickrSearchRsp
     * @return
     */
    protected List<FlickrPhoto> doFilterKeyword(final GeoCmdLine geoCmdLine,
            final FlickrSearchResponse flickrSearchRsp) {
        // the Flickr search result will always contian the keyword.
        return Arrays.asList(flickrSearchRsp.getPhotoArray());
    }

    /**
     * Only the which contain valid geoplace(s) can be returned
     * 
     * @param flickrPhotoList
     * @return
     */
    protected List<FlickrPhoto> doFilterGeo(List<FlickrPhoto> flickrPhotoList) {
        List<FlickrPhoto> hasGeoList = new ArrayList<>();
        for (FlickrPhoto photo : flickrPhotoList) {
            doFilterGeoOne(hasGeoList, photo);
        }
        return hasGeoList;
    }

    /**
     * If photo has geo places, then appends it into hasGeoList
     * 
     * @param hasGeoList
     * @param photo
     */
    protected void doFilterGeoOne(List<FlickrPhoto> hasGeoList, FlickrPhoto photo) {
        if (super.hasGeoPlace(photo.getText()))
            hasGeoList.add(photo);
    }

    /**
     * Saves objects of class type FlickrPhoto into database
     * 
     * @param geoCmdLine
     * @param flickrPhotoList
     *            a list which contains objects of class type FlickrPhoto
     */
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FlickrPhoto> flickrPhotoList) {
        for (FlickrPhoto flickrPhoto : flickrPhotoList) {
            doPersistenceOne(geoCmdLine, flickrPhoto, RECORD_TYPE_POST, true, true);
        }
    }

    /**
     * Fetches all comments of one specific photo.
     * 
     * @param geoCmdLine
     * @param photo
     */
    protected void doCollectAllCommentsOfOnePhoto(GeoCmdLine geoCmdLine, FlickrPhoto photo) {
        if (photo == null)
            return;
        FlickrShowCommentsOfOnePhotoProbe commentsProbe = new FlickrShowCommentsOfOnePhotoProbe(this, photo.getId());
        commentsProbe.collect(geoCmdLine, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.AbstractProbe#getSizeOfPostsInTotal()
     */
    @Override
    protected int getSizeOfPostsInTotal() {
        return sizeOfPhotosInTotal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.AbstractProbe#getSizeOfPostsHasKeyword()
     */
    @Override
    protected int getSizeOfPostsHasKeyword() {
        return sizeOfPhotosHasKeyword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#getSizeOfPostsHasKeywordAndGeo()
     */
    @Override
    protected int getSizeOfPostsHasKeywordAndGeo() {
        return sizeOfPhotosHasKeywordAndGeo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.AbstractProbe#getSizeOfCommentsInTotal()
     */
    @Override
    protected int getSizeOfCommentsInTotal() {
        return sizeOfCommentsInTotal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.AbstractProbe#getSizeOfCommentsHasKeyword()
     */
    @Override
    protected int getSizeOfCommentsHasKeyword() {
        return sizeOfCommentsHasKeyword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#getSizeOfCommentsHasKeywordAndGeo()
     */
    @Override
    protected int getSizeOfCommentsHasKeywordAndGeo() {
        return sizeOfCommentsHasKeywordAndGeo;
    }

}
