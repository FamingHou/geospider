/**
 * 
 */
package massey.geospider.probe;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.global.GeoConstants;
import massey.geospider.probe.facebook.FacebookPagesProbe;
import massey.geospider.probe.facebook.concurrent.FacebookPagesProbeConcurrent;
import massey.geospider.probe.flickr.FlickrSearchProbe;
import massey.geospider.probe.flickr.FlickrSearchProbeConcurrent;
import massey.geospider.probe.twitter.TwitterSearchProbe;

/**
 * All probes are created in this class.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class ProbeFactory implements GeoConstants {

    private static final Logger log = Logger.getLogger(ProbeFactory.class);

    /**
     * 
     */
    private ProbeFactory() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates the corresponding Probe object according to the vendor name
     * 
     * @param geoCmdLine
     *            an object of class GeoCmdLine
     * @param vendor
     *            the social media vendor name
     * @return
     *         <ul>
     *         <li>if vendor is {@link GeoConstants#FACEBOOK_OPTION}, return
     *         {@link FacebookPagesProbe}</li>
     *         </ul>
     */
    public static synchronized Probe generateProbe(GeoCmdLine geoCmdLine, String vendor) {
        if (vendor != null) {
            if (vendor.equalsIgnoreCase(FACEBOOK_OPTION)) {
                log.info("FacebookPageProbe is going to be created for Facebook");
                if (geoCmdLine.isConcurrentOption())
                    return new FacebookPagesProbeConcurrent();
                else
                    return new FacebookPagesProbe();
            }
            if (vendor.equalsIgnoreCase(TWITTER_OPTION)) {
                log.info("TwitterProbe is going to be created for twitter");
                return new TwitterSearchProbe(); // @TODO
            }
            if (vendor.equalsIgnoreCase(REDDIT_OPTION)) {
                log.info("RedditProbe is going to be created for Reddit");
                return null; // @TODO
            }
            if (vendor.equalsIgnoreCase(INSTAGRAM_OPTION)) {
                log.info("InstagramProbe is going to be created for Instagram");
                return null; // @TODO
            }
            if (vendor.equalsIgnoreCase(YOUTUBE_OPTION)) {
                log.info("YoutubeProbe is going to be created for Youtube");
                return null; // @TODO
            }
            if (vendor.equalsIgnoreCase(FLICKR_OPTION)) {
                log.info("FlickrProbe is going to be created for Flickr");
                if (geoCmdLine.isConcurrentOption())
                    return new FlickrSearchProbeConcurrent();
                else
                    return new FlickrSearchProbe();
            }
        }
        return null;
    }

}
