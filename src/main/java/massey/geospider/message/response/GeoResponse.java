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
     * Checks whether the response has valid datas or not. If datas is null or
     * empty, it is invalid.
     * 
     * @return true - valid datas in response; false - otherwise.
     */
    public abstract boolean isDatasEmpty();

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
    public abstract boolean hasNextPagingURL();

}
