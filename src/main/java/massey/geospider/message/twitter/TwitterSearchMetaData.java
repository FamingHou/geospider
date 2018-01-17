/**
 * 
 */
package massey.geospider.message.twitter;

/**
 * <pre>
 * "search_metadata": {
        "completed_in": 0.044,
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
public class TwitterSearchMetaData extends TwitterMessage {

    protected String maxId;
    protected String nextResults;
    protected String refreshUrl;
    protected String sinceId;

    /**
     * 
     */
    public TwitterSearchMetaData() {
    }

    /**
     * @return the maxId
     */
    public String getMaxId() {
        return maxId;
    }

    /**
     * @param maxId
     *            the maxId to set
     */
    public void setMaxId(String maxId) {
        this.maxId = maxId;
    }

    /**
     * @return the nextResults
     */
    public String getNextResults() {
        return nextResults;
    }

    /**
     * @param nextResults
     *            the nextResults to set
     */
    public void setNextResults(String nextResults) {
        this.nextResults = nextResults;
    }

    /**
     * @return the refreshUrl
     */
    public String getRefreshUrl() {
        return refreshUrl;
    }

    /**
     * @param refreshUrl
     *            the refreshUrl to set
     */
    public void setRefreshUrl(String refreshUrl) {
        this.refreshUrl = refreshUrl;
    }

    /**
     * @return the sinceId
     */
    public String getSinceId() {
        return sinceId;
    }

    /**
     * @param sinceId
     *            the sinceId to set
     */
    public void setSinceId(String sinceId) {
        this.sinceId = sinceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TwitterSearchMetaData [maxId=" + maxId + ", nextResults=" + nextResults + ", refreshUrl=" + refreshUrl
                + ", sinceId=" + sinceId + "]";
    }

}
