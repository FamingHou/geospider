/**
 * 
 */
package massey.geospider.message.response.facebook;

import massey.geospider.message.facebook.FacebookComment;

/**
 * The response object represents the result JSON string which is achieved after
 * searching comments under one specific post API is called.
 * 
 * <pre>
 * Request: 195551580614605_851321118370978/comments?limit=3
 * Response:
 * {
  "data": [
    {
      "created_time": "2017-11-18T00:41:17+0000",
      "message": "Glad I live in Utah small ones help the big ones same with cold",
      "id": "851321118370978_851353171701106"
    },
    {
      "created_time": "2017-11-18T07:22:28+0000",
      "message": "Mahnaz Ayub ap kidhar hyen?",
      "id": "851321118370978_851485728354517"
    },
    {
      "created_time": "2017-11-17T23:33:23+0000",
      "message": "Somer Nowak",
      "id": "851321118370978_851331968369893"
    }
  ],
  "paging": {
    "cursors": {
      "before": "MwZDZD",
      "after": "MQZDZD"
    }
  }
}
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookCommentsResponse extends FacebookResponse {

    /**
     * 
     * @param datas
     * @param error
     * @param paging
     */
    public FacebookCommentsResponse(FacebookComment[] datas, FacebookError error, FacebookPaging paging) {
        super(datas, error, paging);
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.message.response.FacebookResponse#getDatas()
     */
    @Override
    public FacebookComment[] getDatas() {
        return (FacebookComment[]) datas;
    }

}
