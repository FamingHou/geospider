/**
 * 
 */
package massey.geospider.message.response.twitter;

import org.apache.log4j.Logger;

import massey.geospider.message.twitter.TwitterSearchMetaData;
import massey.geospider.message.twitter.TwitterStatus;

/**
 * The mapping response object after doing searching request like
 * https://api.twitter.com/1.1/search/tweets.json?q="earthquakefrank"&tweet_mode=extended&lang=en&count=2&include_entities=0
 * 
 * The meta part of the response:
 * 
 * <pre>
 *     "search_metadata": {
        "completed_in": 0.019,
        "max_id": 953403110239563776,
        "max_id_str": "953403110239563776",
        "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2",
        "query": "%22earthquakefrank%22",
        "refresh_url": "?since_id=953403110239563776&q=%22earthquakefrank%22&lang=en",
        "count": 2,
        "since_id": 0,
        "since_id_str": "0"
    }
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterSearchResponse extends TwitterResponse {

    private static final Logger log = Logger.getLogger(TwitterSearchResponse.class);

    protected TwitterStatus[] dataArray;
    protected TwitterSearchMetaData meta;

    // @deprecated
    private String maxIdFromNextResults;

    public TwitterSearchResponse() {

    }

    /**
     * 
     * @param dataArray
     * @param meta
     */
    public TwitterSearchResponse(TwitterStatus[] dataArray, TwitterSearchMetaData meta) {
        this.dataArray = dataArray;
        this.meta = meta;
        // populateMaxIdFromNextResults();
    }

    @Override
    public boolean isDataEmpty() {
        if (dataArray == null || dataArray.length == 0)
            return true;
        return false;
    }

    /**
     * Query next page or not?
     * 
     * Task_20180119_3:
     * 
     * if {@link getMinIdMinusOne} is not null, return true; otherwise return
     * false;
     * 
     * @return true if there is no status in response; false otherwise.
     */
    public boolean hasNextResults() {
        return getMinIdMinusOne() != null;
    }

    /**
     * @deprecated Set the value of max_id in next_results.
     * 
     *             <pre>
     *     "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2&include_entities=1",
     *             </pre>
     * 
     * @return 953038141715226623 in this case
     */
    private void populateMaxIdFromNextResults() {
        if (meta != null) {
            String nextResults = meta.getNextResults();
            if (nextResults != null) {
                int from = nextResults.indexOf('=') + 1;
                int to = nextResults.indexOf('&');
                try {
                    String maxId = nextResults.substring(from, to);
                    if (maxId != null && !maxId.trim().equals(""))
                        maxIdFromNextResults = maxId;
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 
     * @deprecated replace by {@link getMinId}
     * 
     *             Get the value of max_id in next_results.
     * 
     *             <pre>
     *     "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2&include_entities=1",
     *             </pre>
     * 
     * @return 953038141715226623 in this case
     */
    public String getMaxIdFromNextResults() {
        return this.maxIdFromNextResults;
    }

    /**
     * Get the minimum id
     * 
     * @return if dataArray is not empty, return the id of the last status; null
     *         otherwise
     */
    public String getMinId() {
        // the last status has the minimum id.
        if (dataArray != null && dataArray.length > 0) {
            TwitterStatus status = dataArray[dataArray.length - 1];
            if (status != null)
                return status.getId();
            else
                return null;
        }
        return null;
    }

    /**
     * get the String result of method getMinId, convert it into Long, minus 1,
     * convert into String back, then return it.
     * 
     * @return
     */
    public String getMinIdMinusOne() {
        String minIdStr = getMinId();
        if (minIdStr != null) {
            try {
                long minId = Long.parseLong(minIdStr);
                return String.valueOf(minId - 1);
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
        return null;
    }

    /**
     * @return the dataArray
     */
    public TwitterStatus[] getDataArray() {
        return dataArray;
    }

    /**
     * @param dataArray
     *            the dataArray to set
     */
    public void setDataArray(TwitterStatus[] dataArray) {
        this.dataArray = dataArray;
    }

    /**
     * @return the meta
     */
    public TwitterSearchMetaData getMeta() {
        return meta;
    }

    /**
     * @param meta
     *            the meta to set
     */
    public void setMeta(TwitterSearchMetaData meta) {
        this.meta = meta;
    }

}
