/**
 * 
 */
package massey.geospider.probe.twitter;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.twitter.TwitterOneStatusResponse;

/**
 * This class is responsible for fetching all details of one status and its
 * parent status recursively.
 * 
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterShowOneStatusRecursiveProbe extends TwitterShowOneStatusProbe {

    private static final Logger log = Logger.getLogger(TwitterShowOneStatusRecursiveProbe.class);

    /**
     * @param id
     */
    public TwitterShowOneStatusRecursiveProbe(String id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.twitter.TwitterShowOneStatusProbe#doPostCollect(
     * massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info("This class will only fetch this status and its parent status recursively.");
        TwitterOneStatusResponse response = (TwitterOneStatusResponse) inputGeoResponse;
        String curId = response.getTwStatus().getId();
        String parentId = response.getTwStatus().getInReplyToStatusId();
        log.info(new StringBuilder().append("status_id=").append(curId).append("; parent_status_id=").append(parentId));
        if (parentId != null) {
            TwitterShowOneStatusRecursiveProbe probe = new TwitterShowOneStatusRecursiveProbe(parentId);
            // fetch details of parent status id
            probe.collect(geoCmdLine, null);
        }
    }

}
