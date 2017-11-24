/**
 * 
 */
package massey.geospider.probe;

import org.apache.log4j.Logger;

import massey.geospider.global.GeoConstants;
import massey.geospider.probe.facebook.FacebookPagesProbe;

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
     * @param vendor
     *            the social media vendor name
     * @return
     *         <ul>
     *         <li>if vendor is {@link GeoConstants#FACEBOOK_OPTION}, return
     *         {@link FacebookPagesProbe}</li>
     *         </ul>
     */
    public static synchronized Probe generateProbe(String vendor) {
        if (vendor != null) {
            if (vendor.equalsIgnoreCase(FACEBOOK_OPTION)) {
                log.info("FacebookPageProbe is going to be created for Facebook");
                return new FacebookPagesProbe();
            }
            if (vendor.equalsIgnoreCase(TWITTER_OPTION)) {
                log.info("TwitterProbe is going to be created for twitter");
                return null; // @TODO
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
                return null; // @TODO
            }
        }
        return null;
    }

}
