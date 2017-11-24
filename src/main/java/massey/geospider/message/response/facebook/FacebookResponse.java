/**
 * 
 */
package massey.geospider.message.response.facebook;

import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.response.GeoResponse;

/**
 * The http response data structure of the API of Facebook
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class FacebookResponse extends GeoResponse {

    protected FacebookMessage[] datas;
    protected FacebookError error;
    protected FacebookPaging paging;

    /**
     * Fills in messages which are in the specific class type.
     * 
     * @param datas
     *            polymorphism of Facebook messages, it could be an array of
     *            class type FacebookPage, FacebookPost or FaceComment
     * @param error
     *            an object of class FacebookError
     * @param paging
     *            an object of class FacebookPaging which contains the URL of
     *            the next page
     */
    public FacebookResponse(FacebookMessage[] datas, FacebookError error, FacebookPaging paging) {
        this.datas = datas; // using reference, beware of multithreading issues.
        this.error = error;
        this.paging = paging;
    }

    /**
     * @return the datas
     */
    public FacebookMessage[] getDatas() {
        return datas;
    }

    /**
     * @return the error
     */
    public FacebookError getError() {
        return error;
    }

    /**
     * @return the paging
     */
    public FacebookPaging getPaging() {
        return paging;
    }

}
