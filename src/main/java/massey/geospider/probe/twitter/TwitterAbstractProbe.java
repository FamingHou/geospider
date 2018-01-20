/**
 * 
 */
package massey.geospider.probe.twitter;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.twitter.TwitterSearchResponse;
import massey.geospider.message.twitter.TwitterStatus;
import massey.geospider.probe.AbstractProbe;

/**
 * Common methods of TwitterProbe.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class TwitterAbstractProbe extends AbstractProbe {

    private static final Logger log = Logger.getLogger(TwitterAbstractProbe.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.Probe#collect(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.response.GeoResponse)
     */
    @Override
    public void collect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        boolean isProcessing = doPreCollect(geoCmdLine, inputGeoResponse);
        if (isProcessing) {
            TwitterSearchResponse twSearchRsp = (TwitterSearchResponse) doRequest(geoCmdLine, inputGeoResponse);
            // if the value of datas in response is empty, it also means that
            // there
            // is no need to doNextPageCollect
            if (twSearchRsp != null && twSearchRsp.isDatasEmpty()) {
                doProcessResponse(geoCmdLine, twSearchRsp);
                doPostCollect(geoCmdLine, twSearchRsp);
                // only call doNextPageCollect when the response has
                // hasNextResults
                // in Twitter
                if (twSearchRsp.hasNextResults()) {
                    doNextPageCollect(geoCmdLine, twSearchRsp);
                } else {
                    log.info("twSearchRsp is not null, but it has no nextResults, call method onCollectEnd()");
                    onCollectEnd(geoCmdLine, twSearchRsp);
                }
            } else {
                log.info("twitterResponse is null, which means this is the last page or the request url is invalid;");
                log.info("or geoResponse has a empty data list.");
                log.info("The end of this searching, call method onCollectEnd()");
                onCollectEnd(geoCmdLine, twSearchRsp);
            }
        } else {
            log.info("stop processing.");
        }
    }

    /**
     * Checks whether the text of fbMessage has valid geoplace(s)
     * 
     * @param twStatus
     * @return true - if twStatus has valid geoplaces; false -otherwise
     */
    protected boolean hasGeoPlace(TwitterStatus twStatus) {
        // @TODO
        return true;
    }

    /**
     * A hook method when collecting is finished. In other word, it will be
     * invoked when the last page was fetched (next_results is null).
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     * @return
     */
    protected abstract void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse);

}
