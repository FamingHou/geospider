/**
 * 
 */
package massey.geospider.message.response.facebook;

import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.facebook.FacebookPage;

/**
 * The response object represents the result JSON string which is achieved after
 * searching pages API is called.
 * 
 * <pre>
 * Request: search?pretty=0&q=earthquake&type=page&limit=3
 * Response:
 * {
  "data": [
    {
      "name": "Earthquakes Today",
      "id": "195551580614605"
    },
    {
      "name": "EarthQuake Cafe",
      "id": "1161796077197725"
    },
    {
      "name": "Earthquake Gaming",
      "id": "1485751281544775"
    }
  ],
  "paging": {
    "cursors": {
      "before": "MAZDZD",
      "after": "MgZDZD"
    },
    "next": "https://graph.facebook.com/v2.11/search?access_token=EAACEdEose0cBANJ2QNRBSYy1hVk6sDtm4CZC09tAsFgTCPDacanjmiY3vkQwmsxWvSJsxVrtIhNNAAGNkt5Qm6X5iQhCG961J2UmjQ9ZAsArnOOoiKVJ1QAJR5ZB4DZAoqGoAky7DMrLNXMIxPv4tMfsheObpCWHfLBcMjkyYwRgY2mpNHiBkx0hB04iWREZD&pretty=0&q=earthquake&type=page&limit=3&after=MgZDZD"
  }
}
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPagesResponse extends FacebookResponse {

    /**
     * 
     * @param dataArray
     * @param error
     * @param paging
     */
    public FacebookPagesResponse(FacebookPage[] dataArray, FacebookError error, FacebookPaging paging) {
        super(dataArray, error, paging);
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.message.response.FacebookResponse#getDataarray()
     */
    @Override
    public FacebookPage[] getDataArray() {
        return (FacebookPage[]) dataArray;
    }

}
