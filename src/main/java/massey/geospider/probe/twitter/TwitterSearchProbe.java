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
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.twitter.TwitterSearchResponse;
import massey.geospider.message.twitter.TwitterSearchMetaData;
import massey.geospider.message.twitter.TwitterStatus;
import massey.geospider.persistence.dao.StatsPageDAO;
import massey.geospider.persistence.dao.StatsPageDAOImpl;
import massey.geospider.persistence.dto.StatsPage;
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
public class TwitterSearchProbe extends TwitterAbstractProbe {

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
     * Is this class is called recursively? If yes, isRecursive = true;
     * otherwise false.
     */
    private boolean isRecursive = false;

    /**
     * 
     */
    public TwitterSearchProbe() {
    }

    @Override
    protected boolean doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info("Fetching all tweets&replies filtered by the keyword [" + geoCmdLine.getKeywordOptionValue() + "]");
        if (!isRecursive) {
            // this method is called for the first time.
            boolean isProcessing = doProcessOneSearchLogic(geoCmdLine);
            isRecursive = true; //
            return isProcessing; // keep processing or stop
        }
        return true;
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
        log.info("update stats_page...");
        //
        updateStatsPage(geoCmdLine);
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
            if (twSearchRsp != null) { // Task_20180119_3
                log.info("get the minimum id from the previous search response, ");
                log.info("and append it into the value of max_id as the next search URL.");
                String minIdMinusOne = twSearchRsp.getMinIdMinusOne();
                if (minIdMinusOne != null)
                    uriBuilder.addParameter("max_id", minIdMinusOne);
                else
                    throw new URISyntaxException(minIdMinusOne, "the minIdMinusOne is null.");
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
        // @TODO using batch mode for better performance, unnecessary in
        // multi-threading environment.
        for (TwitterStatus twStatus : twStatusList) {
            doPersistenceOne(geoCmdLine, twStatus);
            doPostPersistenceOne(geoCmdLine, twStatus);
        }
    }

    /**
     * Task_20180119_2_Twitter:
     * 
     * If the parent status id of this current status id is not in database, the
     * inserts it into db. The parent status will be inserted into db
     * recursively up to the status id which has no parent.
     * 
     * @param geoCmdLine
     * @param twStatus
     */
    private void doPostPersistenceOne(GeoCmdLine geoCmdLine, TwitterStatus twStatus) {
        String id = twStatus.getId();
        String parentId = twStatus.getInReplyToStatusId();
        if (parentId != null && !parentId.trim().equals("")) {
            log.info(new StringBuilder().append(parentId).append(" ( which is the parent id of ").append(id)
                    .append(") is going to be fetched.").toString());
            TwitterShowOneStatusRecursiveProbe probe = new TwitterShowOneStatusRecursiveProbe(parentId);
            probe.collect(geoCmdLine, null);
        }
    }

    /**
     * Insert statistics information of a Twitter searching into table
     * stats_page. The value of keyword is set to both page_id and page_name.
     * 
     * @param geoCmdLine
     * @return StatsPage
     */
    private StatsPage insertStatsPage(GeoCmdLine geoCmdLine) {
        String keyword = geoCmdLine.getKeywordOptionValue();
        log.info("insertStatsPage:==> keyword = " + keyword);

        StatsPage statsPage = new StatsPage();
        statsPage.setKeyword(keyword);
        statsPage.setVendorType(VENDOR_TYPE_TWITTER);
        statsPage.setPageId(keyword);
        statsPage.setPageName(keyword);

        StatsPageDAO spDao = new StatsPageDAOImpl();
        spDao.insertOne(statsPage);
        return statsPage;
    }

    /**
     * Update statistics information of a Twitter searching into table
     * stats_page.
     * 
     */
    private StatsPage updateStatsPage(GeoCmdLine geoCmdLine) {
        String keyword = geoCmdLine.getKeywordOptionValue();
        log.info("updateStatsPage:==> keyword = " + keyword);
        StatsPage statsPage = new StatsPage();
        statsPage.setPageId(keyword);

        statsPage.setSizeOfPostsInTotal(this.sizeOfTweetsInTotal);
        statsPage.setSizeOfPostsHasKeyword(this.sizeOfTweetsHasKeyword);
        statsPage.setSizeOfPostsHasKeywordAndGeo(this.sizeOfTweetsHasKeywordAndGeo);

        statsPage.setSizeOfRepliesInTotal(this.sizeOfRepliesInTotal);
        statsPage.setSizeOfRepliesHasKeyword(this.sizeOfRepliesHasKeyword);
        statsPage.setSizeOfRepliesHasKeywordAndGeo(this.sizeOfRepliesHasKeywordAndGeo);

        statsPage.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

        StatsPageDAO spDao = new StatsPageDAOImpl();
        spDao.updateByPageId(statsPage);
        return statsPage;
    }

    /**
     * Task_20171214_2
     * 
     * <pre>
     *  If keyword does not exist in table stats_page: 
     *      doOneStatsPageTransaction();
     *  else:
     *      if is_need_refresh = 1: (isNeedRefresh=true)
     *          //
     *          spDao.deleteByPageId(keyword);
     *          doOneStatsPageTransaction();
     *      else 
     *          // do nothing, do not refresh the existing statistics record.
     * </pre>
     * 
     * @param geoCmdLine
     *            a {@code GeoCmdLine} object
     * @return true - keep processing if keyword is in table stats_page and
     *         is_need_refresh=true; otherwise false - stop processing
     */
    protected boolean doProcessOneSearchLogic(GeoCmdLine geoCmdLine) {
        String keyword = geoCmdLine.getKeywordOptionValue();
        StatsPageDAO spDao = new StatsPageDAOImpl();
        StatsPage statsPage = spDao.selectOneByPageId(keyword);
        if (statsPage == null) {
            log.info(keyword + " does not exist in table stats_page. do doOneStatsPageTransaction()");
            // this pageId does not exist in table stats_page.
            insertStatsPage(geoCmdLine);
        } else {
            if (statsPage.isNeedRefresh()) {
                log.info(keyword + " exists in table stats_page but is_need_refresh=true, do refresh.");
                // delete the existing one.
                spDao.deleteByPageId(keyword);
                insertStatsPage(geoCmdLine);
            } else {
                // do nothing
                log.info(keyword + " exists in table stats_page and is_need_refresh=false, do nothing.");
                return false;
            }
        }
        return true;
    }

}
