/**
 * 
 */
package massey.geospider.message.response.twitter;

import massey.geospider.message.twitter.TwitterStatus;

/**
 * The response of Twitter API which shows details of one status(tweet or reply)
 * 
 * This is only for one status query, no parent status will be fetched
 * recursively.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterOneStatusResponse extends TwitterSearchResponse {

    private TwitterStatus twStatus;

    /**
     * 
     * @param twStatus
     */
    public TwitterOneStatusResponse(TwitterStatus twStatus) {
        this.twStatus = twStatus;
    }

    @Override
    public boolean isDatasEmpty() {
        return twStatus == null || twStatus.getId() == null || twStatus.getId().equals("");
    }

    /*
     * It is unnecessary to do the next page query as only one status was
     * returned.
     * 
     * (if this method returns false, method doNextPageCollect will not be
     * called.)
     * 
     * @see massey.geospider.message.response.twitter.TwitterSearchResponse#
     * hasNextResults()
     */
    @Override
    public boolean hasNextResults() {
        return false;
    }

    /**
     * @return the twStatus
     */
    public TwitterStatus getTwStatus() {
        return twStatus;
    }

    /**
     * @param twStatus
     *            the twStatus to set
     */
    public void setTwStatus(TwitterStatus twStatus) {
        this.twStatus = twStatus;
    }

}
