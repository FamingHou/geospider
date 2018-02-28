/**
 * 
 */
package massey.geospider.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.global.GeoConstants;

/**
 * A class which has only one singleton object, used for all callers when using
 * thread pooling service.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoExecutorService {

    private static final Logger log = Logger.getLogger(GeoExecutorService.class);

    public static int THREAD_COUNT = 100;
    /** The singleton object reference */
    private static GeoExecutorService geoExecutorService;

    ExecutorService executor;

    /**
     * 
     */
    private GeoExecutorService(GeoCmdLine geoCmdLine) {
        log.info("GeoExecutorService()");
        THREAD_COUNT = getThreadCount(geoCmdLine);
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
        log.info(new StringBuilder().append("The executor with ").append(THREAD_COUNT).append(" threads was created.")
                .toString());
    }

    /**
     * Gets the number of threads for the specific social media vendors
     * 
     * 
     * @param geoCmdLine
     * @return
     *         <ul>
     *         <li>Facebook: the value of
     *         THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_FACEBOOK in
     *         geospider.properties;</li>
     *         <li>Twitter: the value of
     *         THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_TWITTER in
     *         geospider.properties;</li>
     *         <li>Flickr: the value of
     *         THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_FLICKR in
     *         geospider.properties;</li>
     *         <li>Others: the value of THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE in
     *         geospider.properties;</li>
     *         <li>100 when any exception occurs</li>
     *         </ul>
     */
    private int getThreadCount(GeoCmdLine geoCmdLine) {
        try {
            if (geoCmdLine.isFacebookOption())
                return Integer
                        .parseInt(PropReader.get(GeoConstants.THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_FACEBOOK_PROP_NAME));
            else if (geoCmdLine.isTwitterOption())
                return Integer
                        .parseInt(PropReader.get(GeoConstants.THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_TWITTER_PROP_NAME));
            else if (geoCmdLine.isFlickrOption())
                return Integer
                        .parseInt(PropReader.get(GeoConstants.THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_FLICKR_PROP_NAME));
            else
                return Integer.parseInt(PropReader.get(GeoConstants.THREAD_COUNT_OF_GEO_EXECUTOR_SERVICE_PROP_NAME));
        } catch (Exception e) {
            return THREAD_COUNT;
        }

    }

    /**
     * 
     * @return the singleton object
     */
    public static GeoExecutorService getSingle(GeoCmdLine geoCmdLine) {
        if (geoExecutorService == null) {
            synchronized (GeoExecutorService.class) {
                geoExecutorService = new GeoExecutorService(geoCmdLine);
            }
        }
        return geoExecutorService;
    }

    public ExecutorService getService() {
        return executor;
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are
     * executed, but no new tasks will be accepted. Invocation has no additional
     * effect if already shut down.
     *
     * <p>
     * This method does not wait for previously submitted tasks to complete
     * execution. Use {@link #awaitTermination awaitTermination} to do that.
     *
     * @throws SecurityException
     *             if a security manager exists and shutting down this
     *             ExecutorService may manipulate threads that the caller is not
     *             permitted to modify because it does not hold
     *             {@link java.lang.RuntimePermission}{@code ("modifyThread")},
     *             or the security manager's {@code checkAccess} method denies
     *             access.
     */
    public void shutdown() {
        if (executor != null)
            executor.shutdown();
    }

}
