/**
 * 
 */
package massey.geospider.probe.facebook;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.global.GeoConstants;
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPagesResponse;
import massey.geospider.message.response.facebook.FacebookPaging;

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

    /**
     * 
     */
    public FacebookPagesProbe() {

    }

    @Override
    protected void doPreCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doPreCollect()");
        log.info("Fetching all pages filtered by a keyword [" + geoCmdLine.getKeywordOptionValue() + "]");
        if (inputGeoResponse == null)
            log.info("The first page of pages searching...");
        else
            log.info("The next page of pages searching...");
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
            String responseString = HttpHelper.doGet(urlString);
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
                // fetch all posts under one FacebookPage
                doCollectAllPostsOfOnePage(geoCmdLine, fbPageArray[i]);
            }
        }
    }

    @Override
    protected void doNextPageCollect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookPagesProbe#doNextPageCollect()");
        // call collect method recursively to search the next page on pages
        // level.
        collect(geoCmdLine, inputGeoResponse);
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
        if (responseString == null)
            return null;
        JSONObject jsonObj = new JSONObject(responseString);
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
            String parentId = "_"; // FacebookPage object has no parentId.
            String name = pageObj.isNull("name") ? "" : pageObj.getString("name");
            pageArray[i] = new FacebookPage(id, parentId, name);
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
    private void doCollectAllPostsOfOnePage(GeoCmdLine geoCmdLine, FacebookPage fbPage) {
        if (fbPage == null)
            return;
        FacebookPostsProbe fbPostsProbe = new FacebookPostsProbe(fbPage.getId());
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbPostsProbe.collect(geoCmdLine, null);
    }

}
