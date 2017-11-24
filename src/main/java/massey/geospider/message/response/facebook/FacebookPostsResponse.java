/**
 * 
 */
package massey.geospider.message.response.facebook;

import massey.geospider.message.facebook.FacebookPost;

/**
 * The response object represents the result JSON string which is achieved after
 * searching posts under one specific page API is called.
 * 
 * <pre>
 * Request: 195551580614605/posts?pretty=0&limit=3
 * Response:
 * 
 * {
  "data": [
    {
      "created_time": "2017-11-21T21:31:23+0000",
      "message": "A light earthquake magnitude 4.7 (ml/mb) has occurred on Tuesday, 118 km SSE of Chignik Lake, Alaska (73 miles). The temblor was detected at 10:02:11/10:02 am (local time epicenter) at a depth of 33.05 km (21 miles). How did you react? Was there any damage?
 #‎earthquake‬",
      "id": "195551580614605_853308971505526"
    },
    {
      "created_time": "2017-11-21T18:57:20+0000",
      "message": "A moderate earthquake magnitude 5.1 (ml/mb) has struck on Wednesday near Tadine, New Caledonia. The earthquake was picked up at 05:23:38/5:23 am (local time epicenter) at a depth of 10 km (6 miles). How did you react? Was there any damage?
 #‎earthquake‬",
      "id": "195551580614605_853261941510229"
    },
    {
      "created_time": "2017-11-21T18:29:21+0000",
      "message": "A light earthquake with magnitude 4.7 (ml/mb) was reported 164 km SSE of Krajan Tambakrejo, Indonesia (102 miles) on Wednesday. The earthquake was picked up at 01:40:47/1:40 am (local time epicenter) at a depth of 35 km (22 miles). How would you describe the shaking?
 #‎earthquake‬",
      "id": "195551580614605_853252248177865"
    }
  ],
  "paging": {
    "cursors": {
      "before": "Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXhPVFUxTlRFMU9EQTJNVFEyTURVNk1qSTNORFEwTnpNeU56VTFOemM0TURjMk1nOE1ZAWEJwWDNOMGIzSjVYMmxrRHg4eE9UVTFOVEUxT0RBMk1UUTJNRFZAmT0RVek16QTRPVGN4TlRBMU5USTJEd1IwYVcxbEJsb1VteXNC",
      "after": "Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXhPVFUxTlRFMU9EQTJNVFEyTURVNk5qYzJOakl4TXpNek9Ua3dOelkxTnpnd09ROE1ZAWEJwWDNOMGIzSjVYMmxrRHg4eE9UVTFOVEUxT0RBMk1UUTJNRFZAmT0RVek1qVXlNalE0TVRjM09EWTFEd1IwYVcxbEJsb1VjSUVC"
    },
    "next": "https://graph.facebook.com/v2.11/195551580614605/posts?access_token=EAACEdEose0cBAOpmNQuR1BaY5O6ajc7qNF9IULF3TdZAMZCUeBL6VT1bPtZB6oWBqOY0Ob0U1SbLgdKngh0unqzSWE4Vhe53ZAbZBlrVBXIefGafeDLfJCTV40hUOZB6hlgU2czLjmqo4GT2n9ZCW0uZC5qhHKALTJhHv6ZASd0YHWZCdrIl9hZCiVb2MC7QAd2X1gZD&pretty=0&limit=3&after=Q2c4U1pXNTBYM0YxWlhKNVgzTjBiM0o1WDJsa0R5TXhPVFUxTlRFMU9EQTJNVFEyTURVNk5qYzJOakl4TXpNek9Ua3dOelkxTnpnd09ROE1ZAWEJwWDNOMGIzSjVYMmxrRHg4eE9UVTFOVEUxT0RBMk1UUTJNRFZAmT0RVek1qVXlNalE0TVRjM09EWTFEd1IwYVcxbEJsb1VjSUVC"
  }
}
 * 
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPostsResponse extends FacebookResponse {

    /**
     * 
     * @param datas
     * @param error
     * @param paging
     */
    public FacebookPostsResponse(FacebookPost[] datas, FacebookError error, FacebookPaging paging) {
        super(datas, error, paging);
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.message.response.FacebookResponse#getDatas()
     */
    @Override
    public FacebookPost[] getDatas() {
        return (FacebookPost[]) datas;
    }

}
