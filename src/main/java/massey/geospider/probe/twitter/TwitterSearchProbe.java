/**
 * 
 */
package massey.geospider.probe.twitter;

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
import massey.geospider.global.GeoConstants;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.twitter.TwitterSearchResponse;
import massey.geospider.message.twitter.TwitterSearchMetaData;
import massey.geospider.message.twitter.TwitterStatus;
import massey.geospider.persistence.dao.SocialMediaRecordDAO;
import massey.geospider.persistence.dao.SocialMediaRecordDAOImpl;
import massey.geospider.persistence.dto.SocialMediaRecord;
import massey.geospider.util.DateHelper;
import massey.geospider.util.JSONHelper;

/**
 * For Twitter, the search API can return all records including both tweets and
 * replies in one request, then the application can get all results in one
 * search request and do not have to call other APIS to fetch
 * posts/comments/replies recursively like Facebook.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterSearchProbe extends TwitterAbstractProbe implements GeoConstants {

    private static final Logger log = Logger.getLogger(TwitterSearchProbe.class);

    /** The number of tweets in total */
    protected int sizeOfTweetsInTotal = 0;
    /** The number of tweets that has keyword. */
    protected int sizeOfTweetsHasKeyword = 0;
    /** The number of tweets that has both keyword and geoplaces */
    protected int sizeOfTweetsHasKeywordAndGeo = 0;

    /** The number of replies in total */
    protected int sizeOfRepliesInTotal = 0;
    /** The number of replies that has keyword. */
    protected int sizeOfRepliesHasKeyword = 0;
    /** The number of replies that has both keyword and geoplaces */
    protected int sizeOfRepliesHasKeywordAndGeo = 0;

    /**
     * 
     */
    public TwitterSearchProbe() {
    }

    @Override
    protected void doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info("Fetching all tweets&replies filtered by the keyword [" + geoCmdLine.getKeywordOptionValue() + "]");
    }

    /**
     * Does HTTP request and parses response JSON based on Twitter search API
     */
    @Override
    protected GeoResponse doRequest(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("TwitterSearchProbe#doRequest()");
        TwitterSearchResponse twSearchRsp = (TwitterSearchResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, twSearchRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.info("url of searching Twitter is null or an empty string");
            return null;
        } else {
            // do http request
            String responseString = HttpHelper.doGetAsync4Twitter(urlString);
            return createTwitterSearchResponse(responseString);
        }
    }

    /**
     * Counts the number of tweets/replies of all Twitter statuses, the number
     * of tweets/replies which have GeoPlace, and saves them into database.
     * 
     */
    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        TwitterSearchResponse twSearchRsp = (TwitterSearchResponse) inputGeoResponse;
        // do filter tweet
        List<TwitterStatus> twTweetList = doFilterSearchResultsByTweet(geoCmdLine, twSearchRsp);
        // do filter reply
        List<TwitterStatus> twReplyList = doFilterSearchResultsByReply(geoCmdLine, twSearchRsp);
        //
        doPersistence(geoCmdLine, twTweetList);
        doPersistence(geoCmdLine, twReplyList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doPostCollect(massey.geospider.boot.
     * GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("TwitterSearchProbe#doPostCollect()");
        log.info("Do nothing in method #doPostCollect()");
    }

    /*
     * Does collecting work of the next pagination.
     * 
     * <pre> "next_results":
     * "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2"
     * </pre>
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doNextPageCollect(massey.geospider.
     * boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doNextPageCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("TwitterSearchProbe#doNextPageCollect()");
        TwitterSearchResponse twSearchRsp = (TwitterSearchResponse) inputGeoResponse;
        log.info("next_results=" + twSearchRsp.getMeta() == null ? "null" : twSearchRsp.getMeta().getNextResults());
        // call method collect recursively
        collect(geoCmdLine, inputGeoResponse);
    }

    /**
     * Insert a record into table stats_page when there is no next_results in
     * response.
     */
    @Override
    protected void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("TwitterSearchProbe#onCollectEnd()");
        log.info("insert into stats_page...");
        // @TODO
    }

    /**
     * Creates the request url for search API of Twitter.
     * 
     * <pre>
     * The URL is: <BR>
     * https://api.twitter.com/1.1/search/tweets.json?q="earthquakefrank"&tweet_mode=extended&lang=en&count=2
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param twSearchRsp
     *            TwitterSearchResponse object.
     * @return if twSearchRsp is null, URL is built from geoCmdLine; otherwise,
     *         URL is from twSearchRsp
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final TwitterSearchResponse twSearchRsp) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PropReader.get(TW_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
            sb.append(PropReader.get(TW_VERSION_PROP_NAME)).append(SEPARATOR);
            sb.append("search/tweets.json");
            // use URIBuilder to solve URISyntax issues
            URIBuilder uriBuilder = new URIBuilder(sb.toString());
            uriBuilder.addParameter("include_entities", "0");
            uriBuilder.addParameter("tweet_mode", "extended");
            uriBuilder.addParameter("lang", "en");
            uriBuilder.addParameter("count", PropReader.get(TW_SEARCH_LIMIT_PROP_NAME));
            uriBuilder.addParameter("q", geoCmdLine.getKeywordOptionValue());
            if (twSearchRsp != null && twSearchRsp.getMeta() != null) {
                log.debug("url is generated from twSearchRsp for the next page searching");
                uriBuilder.addParameter("max_id", twSearchRsp.getMaxIdFromNextResults());
            }
            log.info(uriBuilder.toString());
            return uriBuilder.toString();
        } catch (URISyntaxException e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Parses response JSON String and creates a TwitterSearchResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type TwitterSearchResponse
     */
    private TwitterSearchResponse createTwitterSearchResponse(String responseString) {
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
        TwitterStatus[] tweets = parseStatuses(jsonObj);
        TwitterSearchMetaData meta = parseSearchMetaData(jsonObj);
        return new TwitterSearchResponse(tweets, meta);
    }

    /**
     * Parses JSON data array of response and creates an array of class type
     * TwitterStatus
     * 
     * @param jsonObj
     * @return TwitterStatus[]
     */
    private TwitterStatus[] parseStatuses(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("statuses"))
            return null;
        JSONArray dataArray = jsonObj.getJSONArray("statuses");
        int n = dataArray.length();
        TwitterStatus[] tweetArray = new TwitterStatus[n];
        for (int i = 0; i < n; ++i) {
            JSONObject statusObj = dataArray.getJSONObject(i);
            String id = JSONHelper.get(statusObj, "id_str");
            String text = JSONHelper.get(statusObj, "full_text");
            String createdAtStr = JSONHelper.get(statusObj, "created_at");
            String inReplyToStatusId = JSONHelper.get(statusObj, "in_reply_to_status_id_str");
            Timestamp createdAt = DateHelper.parse(createdAtStr, DATETIME_FORMAT_TW);
            // latitude & longitude
            double latitude = parseLatitude(statusObj);
            double longitude = parseLongitude(statusObj);
            TwitterStatus twStatus = new TwitterStatus();
            twStatus.setId(id);
            twStatus.setText(text);
            twStatus.setCreatedAt(createdAt);
            twStatus.setInReplyToStatusId(inReplyToStatusId);
            twStatus.setLatitude(latitude);
            twStatus.setLongitude(longitude);
            tweetArray[i] = twStatus;
        }
        return tweetArray;
    }

    /**
     * <pre>
            "coordinates": {
                "type": "Point",
                "coordinates": [
                    174.70057725,
                    -36.73333452
                ]
            },
     * </pre>
     * 
     * @param statusObj
     * @return -36.73333452
     */
    private double parseLatitude(JSONObject statusObj) {
        double latitude = 0d;
        JSONObject coordinateOuterObj = JSONHelper.getJSONObj(statusObj, "coordinates");
        JSONArray coordinateInnerArray = JSONHelper.getJSONArray(coordinateOuterObj, "coordinates");
        try {
            if (coordinateInnerArray != null)
                latitude = coordinateInnerArray.getDouble(1);
        } catch (Exception e) {
            log.error(e, e);
        }
        return latitude;
    }

    /**
     * <pre>
            "coordinates": {
                "type": "Point",
                "coordinates": [
                    174.70057725,
                    -36.73333452
                ]
            },
     * </pre>
     * 
     * @param statusObj
     * @return 174.70057725
     */
    private double parseLongitude(JSONObject statusObj) {
        double longitude = 0d;
        JSONObject coordinateOuterObj = JSONHelper.getJSONObj(statusObj, "coordinates");
        JSONArray coordinateInnerArray = JSONHelper.getJSONArray(coordinateOuterObj, "coordinates");
        try {
            if (coordinateInnerArray != null)
                longitude = coordinateInnerArray.getDouble(0);
        } catch (Exception e) {
            log.error(e, e);
        }
        return longitude;
    }

    /**
     * Parses JSON data array of response and creates an object of class type
     * TwitterSearchMetaData
     * 
     * <pre>
     *     "search_metadata": {
        "completed_in": 0.044,
        "max_id": 953403110239563776,
        "max_id_str": "953403110239563776",
        "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2",
        "query": "%22earthquakefrank%22",
        "refresh_url": "?since_id=953403110239563776&q=%22earthquakefrank%22&lang=en",
        "count": 2,
        "since_id": 0,
        "since_id_str": "0"
    }
     * </pre>
     * 
     * @param jsonObj
     * @return TwitterSearchMetaData
     */
    private TwitterSearchMetaData parseSearchMetaData(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("search_metadata"))
            return null;
        JSONObject metaObj = jsonObj.getJSONObject("search_metadata");
        String maxId = JSONHelper.get(metaObj, "max_id_str");
        String nextResults = JSONHelper.get(metaObj, "next_results");
        String refreshUrl = JSONHelper.get(metaObj, "refresh_url");
        String sinceId = JSONHelper.get(metaObj, "since_id_str");
        // TwitterSearchMetaData
        TwitterSearchMetaData meta = new TwitterSearchMetaData();
        meta.setMaxId(maxId);
        meta.setNextResults(nextResults);
        meta.setRefreshUrl(refreshUrl);
        meta.setSinceId(sinceId);
        return meta;
    }

    /**
     * Only the statuses of type TwitterTweet which contain GeoPlace can be
     * returned.
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param twSearchRsp
     *            an object of class TwitterSearchResponse
     * @return a list of object of class TwitterStatus which contains the
     *         keyword and GeoPlace
     */
    private List<TwitterStatus> doFilterSearchResultsByTweet(final GeoCmdLine geoCmdLine,
            final TwitterSearchResponse twSearchRsp) {
        // filter tweet
        List<TwitterStatus> tweetList = doFilterTweet(geoCmdLine, twSearchRsp);
        this.sizeOfTweetsInTotal += tweetList.size();
        this.sizeOfTweetsHasKeyword += tweetList.size();
        // filter geoplaces
        List<TwitterStatus> hasGeoTweetList = doFilterGeo(tweetList);
        this.sizeOfTweetsHasKeywordAndGeo += hasGeoTweetList.size();
        return hasGeoTweetList;
    }

    /**
     * Only the statuses of type TwitterReply which contain GeoPlace can be
     * returned.
     * 
     * @param geoCmdLine
     *            an object of GeoCmdLine
     * @param twSearchRsp
     *            an object of class TwitterSearchResponse
     * @return a list of object of class TwitterStatus which contains the
     *         keyword and GeoPlace
     */
    private List<TwitterStatus> doFilterSearchResultsByReply(final GeoCmdLine geoCmdLine,
            final TwitterSearchResponse twSearchRsp) {
        // filter reply
        List<TwitterStatus> replyList = doFilterReply(geoCmdLine, twSearchRsp);
        this.sizeOfRepliesInTotal += replyList.size();
        this.sizeOfRepliesHasKeyword += replyList.size();
        // filter geoplaces
        List<TwitterStatus> hasGeoReplyList = doFilterGeo(replyList);
        this.sizeOfRepliesHasKeywordAndGeo += hasGeoReplyList.size();
        return hasGeoReplyList;
    }

    /**
     * If in_reply_to_status_id_str is null, return TwitterTweet List.
     * 
     * @param geoCmdLine
     * @param twSearchRsp
     * @return
     */
    private List<TwitterStatus> doFilterTweet(final GeoCmdLine geoCmdLine, final TwitterSearchResponse twSearchRsp) {
        List<TwitterStatus> tweetList = new ArrayList<>();
        if (twSearchRsp != null) {
            TwitterStatus[] twStatusArray = twSearchRsp.getDatas();
            for (int i = 0; i < twStatusArray.length; i++) {
                if (twStatusArray[i] != null) {
                    String inReplyToStatusId = twStatusArray[i].getInReplyToStatusId();
                    if (inReplyToStatusId == null || inReplyToStatusId.trim().equals("")) {
                        tweetList.add(twStatusArray[i]);
                    }
                }
            }
        }
        return tweetList;
    }

    /**
     * If in_reply_to_status_id_str is not null, return TwitterReply List.
     * 
     * @param geoCmdLine
     * @param twSearchRsp
     * @return
     */
    private List<TwitterStatus> doFilterReply(final GeoCmdLine geoCmdLine, final TwitterSearchResponse twSearchRsp) {
        List<TwitterStatus> replyList = new ArrayList<>();
        if (twSearchRsp != null) {
            TwitterStatus[] twStatusArray = twSearchRsp.getDatas();
            for (int i = 0; i < twStatusArray.length; i++) {
                if (twStatusArray[i] != null) {
                    String inReplyToStatusId = twStatusArray[i].getInReplyToStatusId();
                    if (inReplyToStatusId != null && !inReplyToStatusId.trim().equals("")) {
                        replyList.add(twStatusArray[i]);
                    }
                }
            }
        }
        return replyList;
    }

    /**
     * Only the which contain valid geoplace(s) can be returned
     * 
     * @param twStatusList
     * @return
     */
    private List<TwitterStatus> doFilterGeo(List<TwitterStatus> twStatusList) {
        List<TwitterStatus> hasGeoList = new ArrayList<>();
        for (TwitterStatus twStatus : twStatusList) {
            // TwitterStatus twStatus = (TwitterStatus) obj;
            if (super.hasGeoPlace(twStatus))
                hasGeoList.add(twStatus);
        }
        return hasGeoList;
    }

    /**
     * Saves objects of class type Twitter or TwitterReply in fbPostList into
     * database
     * 
     * @param geoCmdLine
     * @param twStatusList
     *            a list which contains objects of class type FacebookPost
     */
    private void doPersistence(GeoCmdLine geoCmdLine, List<TwitterStatus> twStatusList) {
        // @TODO using batch mode for better performance
        for (TwitterStatus twStatus : twStatusList) {
            SocialMediaRecord smRecord = new SocialMediaRecord();
            smRecord.setKeyword(geoCmdLine.getKeywordOptionValue());
            smRecord.setVendorRecordId(twStatus.getId());
            smRecord.setMessage(twStatus.getText());
            smRecord.setVendorType(VENDOR_TYPE_TWITTER);
            smRecord.setVendorRecordCreatedTime(twStatus.getCreatedAt());
            smRecord.setPlaceLatitude(twStatus.getLatitude());
            smRecord.setPlaceLongitude(twStatus.getLongitude());
            if (twStatus.getInReplyToStatusId() != null) { // TwitterReply
                smRecord.setVendorRecordParentId(twStatus.getInReplyToStatusId());
                smRecord.setRecordType(RECORD_TYPE_REPLY);
            } else { // TwitterTweet
                smRecord.setRecordType(RECORD_TYPE_POST);
            }

            SocialMediaRecordDAO smrDao = new SocialMediaRecordDAOImpl();
            smrDao.insertOne(smRecord);
        }
    }

}
