/**
 * 
 */
package massey.geospider.message.response.flickr;

import massey.geospider.message.response.GeoResponse;

/**
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class FlickrAbstractResponse extends GeoResponse {

    /**
     * 
     */
    public FlickrAbstractResponse() {
    }

    /**
     * Returns if the response has the next page to be searched
     * 
     * @return true - has next page; false - has not next page
     */
    public abstract boolean hasNextPage();

}
