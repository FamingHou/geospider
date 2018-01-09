/**
 * 
 */
package massey.geospider.controller;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.global.GeoConstants;
import massey.geospider.probe.Probe;
import massey.geospider.probe.ProbeFactory;

/**
 * GeoController dispatches user specified social media vendor to its
 * corresponding Probe which is created by class ProbeFactory.
 * 
 * This is a single object class.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoController {

    private static final Logger log = Logger.getLogger(GeoController.class);

    /** The singleton object reference */
    private static GeoController geoController;

    /**
     * 
     */
    private GeoController() {
    }

    /**
     * 
     * @return the singleton object
     */
    public static GeoController getSingleton() {
        if (geoController == null) {
            synchronized (GeoController.class) {
                log.debug("create the singleton object.");
                geoController = new GeoController();
            }
        }
        return geoController;
    }

    /**
     * Dispatches collecting work to the corresponding Probe object according to
     * the social media vendor name
     * 
     * It is a thread safe method.
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     */
    public synchronized void dispatch(final GeoCmdLine geoCmdLine) {
        if (geoCmdLine == null)
            return;
        LinkedHashSet<String> socialMediaSet = populateSocialMediaSet(geoCmdLine);
        for (Iterator it = socialMediaSet.iterator(); it.hasNext();) {
            String vendor = (String) it.next();
            Probe probe = ProbeFactory.generateProbe(geoCmdLine, vendor);
            if (probe != null) {
                // do collecting work
                log.info(probe.getClass().getName() + " is going to collect data");
                probe.collect(geoCmdLine, null);
            } else {
                log.error("Probe is null for vendor " + vendor);
            }
        }
    }

    /**
     * Creates a LinkedHashSet object and populates the social media vendor
     * names into it if they are specified by user in GeoCmdLine.
     * 
     * LinkedHashSet is used to make sure that the names in this collection are
     * unique and linked.
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @return a LinkedHashSet object which contains the social media vendor
     *         names
     */
    private LinkedHashSet<String> populateSocialMediaSet(final GeoCmdLine geoCmdLine) {
        LinkedHashSet<String> socialMediaSet = new LinkedHashSet<>();
        if (geoCmdLine.isFacebookOption())
            socialMediaSet.add(GeoConstants.FACEBOOK_OPTION);
        if (geoCmdLine.isTwitterOption())
            socialMediaSet.add(GeoConstants.TWITTER_OPTION);
        if (geoCmdLine.isRedditOption())
            socialMediaSet.add(GeoConstants.REDDIT_OPTION);
        if (geoCmdLine.isInstagramOption())
            socialMediaSet.add(GeoConstants.INSTAGRAM_OPTION);
        if (geoCmdLine.isYoutubeOption())
            socialMediaSet.add(GeoConstants.YOUTUBE_OPTION);
        if (geoCmdLine.isFlickrOption())
            socialMediaSet.add(GeoConstants.FLICKR_OPTION);
        return socialMediaSet;
    }

}
