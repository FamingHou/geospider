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
import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookError;
import massey.geospider.message.response.facebook.FacebookPaging;
import massey.geospider.probe.AbstractProbe;

/**
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class FacebookAbstractProbe extends AbstractProbe {

    private static final Logger log = Logger.getLogger(FacebookAbstractProbe.class);

    /*
     * 
     * @see massey.geospider.probe.Probe#collect(org.apache.commons.cli.Options)
     */
    @Override
    public void collect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        doPreCollect(geoCmdLine, inputGeoResponse);
        GeoResponse geoResponse = doRequest(geoCmdLine, inputGeoResponse);
        // if the value of datas in response is empty, it also means that there
        // is no need to doNextPageCollect
        if (geoResponse != null && geoResponse.isDatasEmpty()) {
            doProcessResponse(geoCmdLine, geoResponse);
            doPostCollect(geoCmdLine, geoResponse);
            // only call doNextPageCollect when the response has nextPagingURL
            if (geoResponse.hasNextPagingURL())
                doNextPageCollect(geoCmdLine, geoResponse);
        } else {
            log.info("geoResponse is null, which means this is the last page or the request url is invalid;");
            log.info("or geoResponse has no a empty data list.");
        }
        // doFinishProbe(geoCmdLine, geoResponse);
    }

    /**
     * Parses the following JSON data and creates an object of class
     * FacebookError
     * 
     * <pre>
     *   "error": {
    "message": "Error validating access token: Session has expired on Tuesday, 21-Nov-17 15:00:00 PST. The current time is Wednesday, 22-Nov-17 01:44:35 PST.",
    "type": "OAuthException",
    "code": 190,
    "error_subcode": 463,
    "fbtrace_id": "E7TrVfcBlKG"
    }
     * </pre>
     * 
     * @param jsonObj
     *            JSONObject
     * @return FacebookError
     */
    protected FacebookError parseError(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("error"))
            return null;
        JSONObject errorObj = jsonObj.getJSONObject("error");
        String message = errorObj.isNull("message") ? "" : errorObj.getString("message");
        String type = errorObj.isNull("type") ? "" : errorObj.getString("type");
        int code = errorObj.isNull("code") ? 0 : errorObj.getInt("code");
        int errorSubcode = errorObj.isNull("error_subcode") ? 0 : errorObj.getInt("error_subcode");
        String fbTraceId = errorObj.isNull("fbtrace_id") ? "" : errorObj.getString("fbtrace_id");
        return new FacebookError(message, type, code, errorSubcode, fbTraceId);
    }

    /**
     * Parses the following JSON data and creates an object of class type
     * FacebookPaging
     * 
     * <pre>
     *   "paging": {
    "cursors": {
      "before": "MwZDZD",
      "after": "NQZDZD"
    },
    "next": "https://graph.facebook.com/v2.11/search?access_token=EAACEdEose0cBAFXCkSC9NRyJ5X7ouYgRtdMIg5ebsQKgb7iZA7fnfPfTWyjJkfF9x8kp3jleTgZAgQvYxZBSnQDimdk9q7LRjOjNcaGNiTXyS0AP9BbtS7ZBZBzPa15TXbMZCLOaZB4JnZBrtPTeJTdNyclgcCaarQDrtClkUfFssAoX0tkVjrKWCqF2aJFwZAWZCYJHGBXislVwZDZD&pretty=0&q=earthquake&type=page&limit=3&after=NQZDZD",
    "previous": "https://graph.facebook.com/v2.11/search?access_token=EAACEdEose0cBAFXCkSC9NRyJ5X7ouYgRtdMIg5ebsQKgb7iZA7fnfPfTWyjJkfF9x8kp3jleTgZAgQvYxZBSnQDimdk9q7LRjOjNcaGNiTXyS0AP9BbtS7ZBZBzPa15TXbMZCLOaZB4JnZBrtPTeJTdNyclgcCaarQDrtClkUfFssAoX0tkVjrKWCqF2aJFwZAWZCYJHGBXislVwZDZD&pretty=0&q=earthquake&type=page&limit=3&before=MwZDZD"
    }
     * </pre>
     * 
     * @param jsonObj
     * @return an object of class type FacebookPaging
     */
    protected FacebookPaging parsePaging(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("paging"))
            return null;
        JSONObject pagingObj = jsonObj.getJSONObject("paging");
        String nextURL = pagingObj.isNull("next") ? null : pagingObj.getString("next");
        String previousURL = pagingObj.isNull("previous") ? null : pagingObj.getString("previous");
        return new FacebookPaging(nextURL, previousURL);
    }

    /**
     * Checks whether the message in FacebookMessage object has valid
     * geoplace(s)
     * 
     * @param fbMessage
     * @return true - if fbMessage has valid geoplaces; false -otherwise
     */
    protected boolean hasGeoPlace(FacebookMessage fbMessage) {
        try {
            String msg = fbMessage.getMessage();
            StringBuilder sb = new StringBuilder();
            sb.append("http://geotxt.org/v2/api/geotxt.json");
            // using URIBuilder to solve URISyntax issues
            URIBuilder builder = new URIBuilder(sb.toString());
            builder.addParameter("m", "stanfords");
            builder.addParameter("q", msg);

            String responseString = HttpHelper.doGet(builder.toString());
            JSONObject jsonObj = new JSONObject(responseString);
            if (jsonObj != null && !jsonObj.isNull("features")) {
                JSONArray dataArray = jsonObj.getJSONArray("features");
                int len = dataArray.length();
                if (len > 0)
                    return true;
            }
        } catch (URISyntaxException e) {
            log.error(e, e);
        }
        return false;
    }
}
