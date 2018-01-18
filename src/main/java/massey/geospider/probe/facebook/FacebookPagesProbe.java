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
import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPagesResponse;
import massey.geospider.message.response.facebook.FacebookPaging;
import massey.geospider.persistence.dao.StatsPageDAO;
import massey.geospider.persistence.dao.StatsPageDAOImpl;
import massey.geospider.persistence.dto.StatsPage;
import massey.geospider.util.JSONHelper;

/**
 * 
 * The implementation of Probe interface for Facebook in pages result level.<BR>
 * <BR>
 * 
 * Facebook provides an API to get pages by sending a HTTP GET request with
 * parameters <b><i>YOUR_KEYWORD</i></b></BR>
 * </BR>
 * 
 * The URL is <a href="">https://graph.facebook.com/v2.11/search?
 * access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}&pretty=0&q={<b><i>YOUR_KEYWORD</i></b>}&type=page&limit=25</a>
 * 
 * </BR>
 * </BR>
 * limit=25: the value 25 is the value which is read from the configuration
 * file. It is a possible way to increase this value to reduce network traffic
 * frequency.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPagesProbe extends FacebookAbstractProbe implements GeoConstants {

    private static final Logger log = Logger.getLogger(FacebookPagesProbe.class);

    /** The current FacebookPage object which is being processed */
    protected FacebookPage currentPage;

    /**
     * 
     */
    public FacebookPagesProbe() {

    }

    @Override
    protected boolean doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doPreCollect()");
        log.info("Fetching all pages filtered by a keyword [" + geoCmdLine.getKeywordOptionValue() + "]");
        if (inputGeoResponse == null)
            log.info("The first page of pages searching...");
        else
            log.info("The next page of pages searching...");
        return true;
    }

    /**
     * Does HTTP request and parses response JSON based on Facebook APIs in
     * Pages level
     */
    @Override
    protected GeoResponse doRequest(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doRequest()");
        FacebookPagesResponse fbPagesRsp = (FacebookPagesResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, fbPagesRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.info("||Notice|| url of fetching pages is null or an empty string");
            return null;
        } else {
            // do http request
            String responseString = HttpHelper.doGetAsync(urlString);
            return createFacebookPagesResponse(responseString);
        }
    }

    /**
     * Does processing work on Facebook page level since FacebookPagesResponse
     * was constructed
     */
    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doProcessResponse()");
        // FacebookPages level
        FacebookPagesResponse fbPagesRsp = (FacebookPagesResponse) inputGeoResponse;
        // @TODO do filter and persistence
        // no need to do filter work as all results contain the keyword.
    }

    /**
     * Does Probe work to collect all posts under one specific page.
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doPostCollect(), fetching posts under one page");
        // FacebookPages level
        FacebookPagesResponse fbPagesRsp = (FacebookPagesResponse) inputGeoResponse;
        if (fbPagesRsp != null) {
            FacebookPage[] fbPageArray = fbPagesRsp.getDatas();
            for (int i = 0; i < fbPageArray.length; i++) {
                currentPage = fbPageArray[i];
                doProcessOnePageLogic(geoCmdLine, currentPage);
            }
        }
    }

    @Override
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doNextPageCollect(): previousPageId = " + currentPage == null ? "null"
                : currentPage.getId());
        // call collect method recursively to search the next page on pages
        // level.
        collect(geoCmdLine, inputGeoResponse);
    }

    /**
     * Task_20171214_2
     * 
     * <pre>
     *  If pageId does not exist in table stats_page: 
     *      doOneStatsPageTransaction();
     *  else:
     *      if is_need_refresh = 1: (isNeedRefresh=true)
     *          //
     *          spDao.deleteByPageId(fbPage.getId());
     *          doOneStatsPageTransaction();
     *      else 
     *          // do nothing, do not refresh the existing statistics record.
     * </pre>
     * 
     * @param geoCmdLine
     *            a {@code GeoCmdLine} object
     * @param fbPage
     *            a {@code FacebookPage} object to be processed
     */
    protected void doProcessOnePageLogic(GeoCmdLine geoCmdLine, FacebookPage fbPage) {
        StatsPageDAO spDao = new StatsPageDAOImpl();
        if (fbPage != null) {
            StatsPage statsPage = spDao.selectOneByPageId(fbPage.getId());
            if (statsPage == null) {
                log.info(fbPage.getId() + " does not exist in table stats_page. do doOneStatsPageTransaction()");
                // this pageId does not exist in table stats_page.
                doOneStatsPageTransaction(geoCmdLine, fbPage);
            } else {
                if (statsPage.isNeedRefresh()) {
                    log.info(fbPage.getId() + " exists in table stats_page but is_need_refresh=true, do refresh.");
                    // delete the existing one.
                    spDao.deleteByPageId(fbPage.getId());
                    doOneStatsPageTransaction(geoCmdLine, fbPage);
                } else {
                    // do nothing
                    log.info(fbPage.getId() + " exists in table stats_page and is_need_refresh=false, do nothing.");
                }
            }
        } else {
            log.debug("fbPage is null");
        }

    }

    /**
     * Does one transaction for creating one record of table stats_page.
     * 
     * <pre>
     * Step1: insert a stats_page record with page_id={@ fbPage.getId()},
     * size_of_* = 0, created_time=updated_time=current timestamp,
     * is_need_refresh=0
     * 
     * Step2: do counting all fields of size_of_*
     * 
     * Step3: update this record with all size fields achieved from step2,
     * updated_time=current timestamp.
     * 
     * <pre>
     * The time elapsed for counting equals (updated_time - created_time). It is
     * also good for distributed computing to insert a record with the pageId
     * before step2 as step2 may takes a long time.
     * 
     * @param geoCmdLine
     *            a {@code GeoCmdLine} object
     * @param fbPage
     *            a {@code FacebookPage} object to be processed
     */
    private void doOneStatsPageTransaction(GeoCmdLine geoCmdLine, FacebookPage fbPage) {
        // Step1
        insertStatsPage(geoCmdLine, currentPage);
        // Step2
        log.info("fetch all posts of the page ===> " + currentPage.getId());
        // fetch all posts of one FacebookPage
        doCollectAllPostsOfOnePage(geoCmdLine, currentPage);
        // Step3
        updateStatsPage(currentPage);
    }

    /**
     * Creates the request url for posts searching level.
     * 
     * <pre>
     * The URL is: <BR>
     * https://graph.facebook.com/v2.11/search?access_token={<b><i>YOUR_ACCESS_TOKEN</i></b>}
     * &pretty=0&q={<b><i>YOUR_KEYWORD</i></b>}&type=page&limit=25
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param inputGeoResponse
     *            GeoResponse object.
     * @return if inputGepResponse is null, YOUR_KEYWORD is built from
     *         geoCmdLine; otherwise, YOUR_KEYWORD is from inputGeoResponse
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final FacebookPagesResponse fbPagesRsp) {
        if (fbPagesRsp == null) {
            // build url by the keyword which is from geoCmdLine.
            log.debug("url is generated by geoCmdLine for the first searching");
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(PropReader.get(FB_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
                sb.append(PropReader.get(FB_VERSION_PROP_NAME)).append(SEPARATOR);
                sb.append("search");
                // use URIBuilder to solve URISyntax issues
                URIBuilder uriBuilder = new URIBuilder(sb.toString());
                uriBuilder.addParameter("access_token", PropReader.get(FB_ACCESS_TOKEN_PROP_NAME));
                uriBuilder.addParameter("pretty", "0");
                uriBuilder.addParameter("q", geoCmdLine.getKeywordOptionValue());
                uriBuilder.addParameter("type", "page");
                uriBuilder.addParameter("limit", PropReader.get(FB_PAGE_LIMIT_PROP_NAME));
                log.info(uriBuilder.toString());
                return uriBuilder.toString();
            } catch (URISyntaxException e) {
                log.error(e, e);
                return null;
            }
        } else {
            log.debug("url is generated by fbPagesRsp for the next page searching");
            String urlString = fbPagesRsp.getPaging() == null ? null : fbPagesRsp.getPaging().getNextURL();
            log.info(urlString);
            return urlString;
        }
    }

    /**
     * Parses response JSON String and creates a FacebookPagesResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type FacebookPagesResponse
     */
    private FacebookPagesResponse createFacebookPagesResponse(String responseString) {
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
        FacebookPage[] datas = parseDatas(jsonObj);
        FacebookError error = parseError(jsonObj);
        FacebookPaging paging = parsePaging(jsonObj);
        return new FacebookPagesResponse(datas, error, paging);
    }

    /**
     * Parses the following JSON data array and creates an array of class type
     * FacebookPage
     * 
     * <pre>
     * "data": [
    {
      "name": "Earthquakes Today",
      "id": "195551580614605"
    },
    {
      "name": "EarthQuake Cafe",
      "id": "1161796077197725"
    },
    {
      "name": "EarthQuaker Devices",
      "id": "417104948327391"
    }
    ]
     * </pre>
     * 
     * @param jsonObj
     *            JSONObject
     * @return FacebookPage[]
     *         <ul>
     *         <li>if jsonObj is null or empty, return null</li>
     *         <li>if there is no JSONArray under name tag "data", return
     *         null</li>
     *         </ul>
     */
    private FacebookPage[] parseDatas(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("data"))
            return null;
        JSONArray dataArray = jsonObj.getJSONArray("data");
        int n = dataArray.length();
        FacebookPage[] pageArray = new FacebookPage[n];
        for (int i = 0; i < n; ++i) {
            JSONObject pageObj = dataArray.getJSONObject(i);
            String id = pageObj.isNull("id") ? "" : pageObj.getString("id");
            FacebookMessage parent = null; // FacebookPage object has no parent.
            String name = pageObj.isNull("name") ? "" : pageObj.getString("name");
            pageArray[i] = new FacebookPage(id, parent, name);
        }
        return pageArray;
    }

    /**
     * Collects all posts of one specific page.
     * 
     * @param geocmdLine
     *            an object of class GeoCmdLine
     * @param fbPage
     *            an object of class FacebookPage
     */
    protected void doCollectAllPostsOfOnePage(GeoCmdLine geoCmdLine, FacebookPage fbPage) {
        if (fbPage == null)
            return;
        FacebookPostsProbe fbPostsProbe = new FacebookPostsProbe(fbPage);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbPostsProbe.collect(geoCmdLine, null);
    }

    /**
     * Insert statistics information of a FacebookPage into database
     * 
     * @param geoCmdLine
     * @param fbPage
     *            a FacebookPage object
     */
    private StatsPage insertStatsPage(GeoCmdLine geoCmdLine, FacebookPage fbPage) {
        log.info("insertStatsPage:==> pageId = " + fbPage.getId());

        StatsPage statsPage = new StatsPage();
        statsPage.setKeyword(geoCmdLine.getKeywordOptionValue());
        statsPage.setVendorType(VENDOR_TYPE_FACEBOOK);
        statsPage.setPageId(fbPage.getId());
        statsPage.setPageName(fbPage.getName());

        StatsPageDAO spDao = new StatsPageDAOImpl();
        spDao.insertOne(statsPage);
        return statsPage;
    }

    /**
     * Update statistics information of a FacebookPage into database
     * 
     * @param fbPage
     *            a FacebookPage object
     */
    private StatsPage updateStatsPage(FacebookPage fbPage) {
        log.info("updateStatsPage:==> pageId = " + fbPage.getId());
        StatsPage statsPage = new StatsPage();
        statsPage.setPageId(fbPage.getId());
        // log.info("PostsInTotal:" + fbPage.getSizeOfPostsInTotal());
        // log.info("PostsHasKeyword:" + fbPage.getSizeOfPostsHasKeyword());
        // log.info("PostsHasKeywordAndGeo:" +
        // fbPage.getSizeOfPostsHasKeywordAndGeo());
        //
        // log.info("CommentsInTotal:" + fbPage.getSizeOfCommentsInTotal());
        // log.info("CommentsHasKeyword:" +
        // fbPage.getSizeOfCommentsHasKeyword());
        // log.info("CommentsHasKeywordAndGeo:" +
        // fbPage.getSizeOfCommentsHasKeywordAndGeo());
        //
        // log.info("RepliesInTotal:" + fbPage.getSizeOfRepliesInTotal());
        // log.info("RepliesHasKeyword:" + fbPage.getSizeOfRepliesHasKeyword());
        // log.info("RepliesHasKeywordAndGeo:" +
        // fbPage.getSizeOfRepliesHasKeywordAndGeo());

        statsPage.setSizeOfPostsInTotal(fbPage.getSizeOfPostsInTotal());
        statsPage.setSizeOfPostsHasKeyword(fbPage.getSizeOfPostsHasKeyword());
        statsPage.setSizeOfPostsHasKeywordAndGeo(fbPage.getSizeOfPostsHasKeywordAndGeo());

        statsPage.setSizeOfCommentsInTotal(fbPage.getSizeOfCommentsInTotal());
        statsPage.setSizeOfCommentsHasKeyword(fbPage.getSizeOfCommentsHasKeyword());
        statsPage.setSizeOfCommentsHasKeywordAndGeo(fbPage.getSizeOfCommentsHasKeywordAndGeo());

        statsPage.setSizeOfRepliesInTotal(fbPage.getSizeOfRepliesInTotal());
        statsPage.setSizeOfRepliesHasKeyword(fbPage.getSizeOfRepliesHasKeyword());
        statsPage.setSizeOfRepliesHasKeywordAndGeo(fbPage.getSizeOfRepliesHasKeywordAndGeo());

        statsPage.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

        StatsPageDAO spDao = new StatsPageDAOImpl();
        spDao.updateByPageId(statsPage);
        return statsPage;
    }
}
