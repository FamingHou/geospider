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

    protected FacebookMessage[] dataArray;
    protected FacebookError error;
    protected FacebookPaging paging;

    /**
     * Fills in messages which are in the specific class type.
     * 
     * @param dataArray
     *            polymorphism of Facebook messages, it could be an array of
     *            class type FacebookPage, FacebookPost or FaceComment
     * @param error
     *            an object of class FacebookError
     * @param paging
     *            an object of class FacebookPaging which contains the URL of
     *            the next page
     */
    public FacebookResponse(FacebookMessage[] dataArray, FacebookError error, FacebookPaging paging) {
        this.dataArray = dataArray; // using reference, beware of multithreading issues.
        this.error = error;
        this.paging = paging;
    }

    /**
     * @return the dataArray
     */
    public FacebookMessage[] getDataArray() {
        return dataArray;
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

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.message.response.GeoResponse#isDataEmpty()
     */
    @Override
    public boolean isDataEmpty() {
        if (dataArray != null && dataArray.length != 0)
            return true;
        else
            return false;
    }

    /**
     * Checks whether the response has valid URl of next of paging.
     * 
     * <pre>
     *   "paging": {
    "cursors": {
      "before": "WTI5dGJXVnVkRjlqZAFhKemIzSTZAOelEwTnpJNE9UWTVNRFUxTWpZAeE9qRTFNVEkxTWpZAMU1qST0ZD",
      "after": "WTI5dGJXVnVkRjlqZAFhKemIzSTZAOelEwT0RRM056YzVNRFF6TXpnd09qRTFNVEkxTkRjNU5qQT0ZD"
    },
    "next": "https://graph.facebook.com/v2.11/744728735721951_744728902388601/comments?access_token=EAACEdEose0cBABQNOTtcayBz2UOJZBkGhtQuyXyJPxE7hSw1ZBP6ZCXYrHGWhp3a7X280utbLQ4i6JZBikxHPUZChYHIr47e7Urs85gpbZA6OOtNw8taRCUGUAqcY2j1GAWMgiVeAZAMfuyYM8ItwmQdA1RXikQkUVgXBbEXFpHpLzEKbv59BFm1zWIXNQ0FQkZD&pretty=0&limit=2&after=WTI5dGJXVnVkRjlqZAFhKemIzSTZAOelEwT0RRM056YzVNRFF6TXpnd09qRTFNVEkxTkRjNU5qQT0ZD"
    }
     * </pre>
     * 
     * @return The URL of next paging
     */
    public boolean hasNextPagingURL() {
        if (paging != null && paging.getNextURL() != null)
            return true;
        else
            return false;
    }

}
