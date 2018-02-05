/**
 * 
 */
package massey.geospider.message.response;

/**
 * All common fields of the http response message of the API from different
 * social media vendors will be saved in this abstract class.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class GeoResponse {

    /**
     * 
     */
    public GeoResponse() {
    }

    /**
     * Checks whether the response has valid data or not. If data is null or
     * empty, it is invalid.
     * 
     * @return true - valid data in response; false - otherwise.
     */
    public abstract boolean isDataEmpty();

}
