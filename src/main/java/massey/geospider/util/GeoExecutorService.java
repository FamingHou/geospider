/**
 * 
 */
package massey.geospider.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * A class which has only one singleton object, used for all callers when using
 * thread pooling service.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class GeoExecutorService {

    private static final Logger log = Logger.getLogger(GeoExecutorService.class);

    public static final int THREAD_COUNT = 100;
    /** The singleton object reference */
    private static GeoExecutorService geoExecutorService;

    ExecutorService executor;

    /**
     * 
     */
    private GeoExecutorService() {
        log.info("GeoExecutorService()");
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
        log.info("executor was created.");
    }

    /**
     * 
     * @return the singleton object
     */
    public static GeoExecutorService getSingle() {
        if (geoExecutorService == null) {
            synchronized (GeoExecutorService.class) {
                geoExecutorService = new GeoExecutorService();
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
