/**
 * 
 */
package massey.geospider.probe;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.global.GeoConstants;
import massey.geospider.message.response.GeoResponse;

/**
 * A probe defines the whole steps for collecting messages from different social
 * media platforms using template pattern.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public interface Probe extends GeoConstants {

    /**
     * Collects messages according to used typed options.
     * 
     * @param geoCmdLine
     *            user typed value (e.g. keyword for searching)
     * @param inputGeoResponse
     *            <ul>
     *            <li>null for the searching of the first time</li>
     *            <li>if it is not null, it contains the URL of the next page if
     *            exists</li>
     *            <li>class type FacebookPagesResponse for pages searching level
     *            </li>
     *            <li>class type FacebookPostsResponse for posts searching level
     *            </li>
     *            <li>class type FacebookCommentsResponse for comments searching
     *            level</li>
     *            </ul>
     */
    public void collect(final GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse);

}
