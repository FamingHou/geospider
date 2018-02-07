/**
 * 
 */
package massey.geospider.probe;

import java.net.URLEncoder;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.global.GeoConstants;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.persistence.dao.StatsPageDAO;
import massey.geospider.persistence.dao.StatsPageDAOImpl;
import massey.geospider.persistence.dto.StatsPage;
import massey.geospider.util.JSONHelper;

/**
 * A default implementation of Probe interface, defines the detailed name of the
 * template methods and their arguments.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class AbstractProbe implements Probe {

    private static final Logger log = Logger.getLogger(AbstractProbe.class);

    /**
     * Does preparation work for this collecting task
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     * @return true - keep on processing; false - stop processing
     */
    protected boolean doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("doPreCollect");
        return true;
    }

    /**
     * Does request according to the API which is provided by the social media
     * vendor, gets response and then constructs a GeoResponse object.
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     * @return GeoResponse object
     */
    protected abstract GeoResponse doRequest(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse);

    /**
     * Does work (e.g. persistence) since the geoResponse object had been
     * achieved.
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     */
    protected abstract void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse);

    /**
     * Does post work
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     */
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("doPostCollect");
    }

    /**
     * Does collecting work of the next page
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     */
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("doNextPageCollect");
    }

    /**
     * Checks whether the message object has valid geo places
     * 
     * @param msg
     * @return true - if fbMessage has valid geo places; false -otherwise
     */
    protected boolean hasGeoPlace(String msg) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PropReader.get(GeoConstants.GEO_VALIDATION_URL_PROP_NAME));
            String msgEncoded = URLEncoder.encode(msg, "UTF-8").replaceAll("\\+", "%20");
            sb.append(msgEncoded);
            String responseString = HttpHelper.doGetAsync(sb.toString());
            JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
            String isValidStr = JSONHelper.get(jsonObj, "is_valid_str");
            if (isValidStr != null && isValidStr.trim().equalsIgnoreCase("true"))
                return true;
        } catch (Exception e) {
            log.error(e, e);
        }
        return false;
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

    /**
     * Insert statistics information of a searching into table stats_page. The
     * value of keyword is set to both page_id and page_name.
     * 
     * @param geoCmdLine
     * @return StatsPage
     */
    protected StatsPage insertStatsPage(GeoCmdLine geoCmdLine) {
        String keyword = geoCmdLine.getKeywordOptionValue();
        log.info("insertStatsPage:==> keyword = " + keyword);

        StatsPage statsPage = new StatsPage();
        statsPage.setKeyword(keyword);
        statsPage.setVendorType(getVendorType());
        // set vendorType#keyword as the value of pageId
        // pageId is a unique column in table stats_page
        statsPage.setPageId(getVendorType() + "#" + keyword);
        statsPage.setPageName(keyword);

        StatsPageDAO spDao = new StatsPageDAOImpl();
        spDao.insertOne(statsPage);
        return statsPage;
    }

    /**
     * A abstract method to get the vendor type of a social media platform,
     * which is used in template method {@link #insertStatsPage}
     * 
     * @return social media vendor type
     */
    protected abstract int getVendorType();

    /**
     * Update statistics information of a searching into table stats_page.
     * 
     * @param geoCmdLine
     * @return
     */
    protected StatsPage updateStatsPage(GeoCmdLine geoCmdLine) {
        String keyword = geoCmdLine.getKeywordOptionValue();
        log.info("updateStatsPage:==> keyword = " + keyword);
        StatsPage statsPage = new StatsPage();
        statsPage.setPageId(keyword);

        statsPage.setSizeOfPostsInTotal(getSizeOfPostsInTotal());
        statsPage.setSizeOfPostsHasKeyword(getSizeOfPostsHasKeyword());
        statsPage.setSizeOfPostsHasKeywordAndGeo(getSizeOfPostsHasKeywordAndGeo());

        statsPage.setSizeOfCommentsInTotal(getSizeOfCommentsInTotal());
        statsPage.setSizeOfCommentsHasKeyword(getSizeOfCommentsHasKeyword());
        statsPage.setSizeOfCommentsHasKeywordAndGeo(getSizeOfCommentsHasKeywordAndGeo());

        statsPage.setSizeOfRepliesInTotal(getSizeOfRepliesInTotal());
        statsPage.setSizeOfRepliesHasKeyword(getSizeOfRepliesHasKeyword());
        statsPage.setSizeOfRepliesHasKeywordAndGeo(getSizeOfRepliesHasKeywordAndGeo());

        statsPage.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

        StatsPageDAO spDao = new StatsPageDAOImpl();
        spDao.updateByPageId(statsPage);
        return statsPage;
    }

    protected int getSizeOfPostsInTotal() {
        return 0;
    }

    protected int getSizeOfPostsHasKeyword() {
        return 0;
    }

    protected int getSizeOfPostsHasKeywordAndGeo() {
        return 0;
    }

    protected int getSizeOfCommentsInTotal() {
        return 0;
    }

    protected int getSizeOfCommentsHasKeyword() {
        return 0;
    }

    protected int getSizeOfCommentsHasKeywordAndGeo() {
        return 0;
    }

    protected int getSizeOfRepliesInTotal() {
        return 0;
    }

    protected int getSizeOfRepliesHasKeyword() {
        return 0;
    }

    protected int getSizeOfRepliesHasKeywordAndGeo() {
        return 0;
    }

}
