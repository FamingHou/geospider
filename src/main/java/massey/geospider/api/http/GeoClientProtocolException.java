/**
 * 
 */
package massey.geospider.api.http;

import org.apache.http.client.ClientProtocolException;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoClientProtocolException extends ClientProtocolException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Status Code of HTTP response */
    private int statusCode;

    /**
     * @param s
     */
    public GeoClientProtocolException(String s, int statusCode) {
        super(s);
        this.statusCode = statusCode;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
