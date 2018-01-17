/**
 * 
 */
package massey.geospider.message.response.twitter;

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

    protected TwitterStatus[] datas;
    protected TwitterSearchMetaData meta;

    private String maxIdFromNextResults;

    /**
     * 
     */
    public TwitterSearchResponse(TwitterStatus[] datas, TwitterSearchMetaData meta) {
        this.datas = datas;
        this.meta = meta;
        populateMaxIdFromNextResults();
    }

    @Override
    public boolean isDatasEmpty() {
        if (datas == null || datas.length == 0)
            return false;
        return true;
    }

    /**
     * Query next page or not?
     * 
     * @return true if next_results is not null; false otherwise.
     */
    public boolean hasNextResults() {
        if (maxIdFromNextResults == null)
            return false;
        else
            return true;
    }

    /**
     * 
     * Set the value of max_id in next_results.
     * 
     * <pre>
     *     "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2&include_entities=1",
     * </pre>
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
     * Get the value of max_id in next_results.
     * 
     * <pre>
     *     "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2&include_entities=1",
     * </pre>
     * 
     * @return 953038141715226623 in this case
     */
    public String getMaxIdFromNextResults() {
        return this.maxIdFromNextResults;
    }

    /**
     * @return the datas
     */
    public TwitterStatus[] getDatas() {
        return datas;
    }

    /**
     * @param datas
     *            the datas to set
     */
    public void setDatas(TwitterStatus[] datas) {
        this.datas = datas;
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
