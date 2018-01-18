/**
 * 
 */
package massey.geospider.probe;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.response.GeoResponse;

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

}
