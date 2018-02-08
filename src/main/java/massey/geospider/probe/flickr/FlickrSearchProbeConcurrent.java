/**
 * 
 */
package massey.geospider.probe.flickr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.flickr.FlickrPhoto;
import massey.geospider.util.GeoExecutorService;

/**
 * The multi-threading implementation of FlickrSearchProbe
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FlickrSearchProbeConcurrent extends FlickrSearchProbe {

    private static final Logger log = Logger.getLogger(FlickrSearchProbeConcurrent.class);

    /**
     * 
     */
    public FlickrSearchProbeConcurrent() {
    }

    /*
     * The concurrent implementation for FilterGeo of a list of FlickrPhoto by
     * using multi-threading solution
     * 
     * @see
     * massey.geospider.probe.flickr.FlickrSearchProbe#doFilterGeo(java.util.
     * List)
     */
    @Override
    protected List<FlickrPhoto> doFilterGeo(List<FlickrPhoto> flickrPhotoList) {
        List<FlickrPhoto> hasGeoList = new ArrayList<>();
        if (flickrPhotoList != null && !flickrPhotoList.isEmpty()) {
            int length = flickrPhotoList.size();
            log.info(new StringBuilder().append("<#doFilterGeo_FlickrPhotoList> fork <<< "));
            log.info(new StringBuilder().append("input size: << ").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                OneFlickrPhotoFilterGeoCallable task = new OneFlickrPhotoFilterGeoCallable(this, flickrPhotoList.get(i),
                        hasGeoList);
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle().getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </#doFilterGeo_FlickrPhotoList>"));
        }
        return hasGeoList;
    }

    /*
     * Multi-threading implementation of method doPersistence
     * 
     * @see
     * massey.geospider.probe.flickr.FlickrSearchProbe#doPersistence(massey.
     * geospider.boot.GeoCmdLine, java.util.List)
     */
    @Override
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FlickrPhoto> flickrPhotoList) {
        if (flickrPhotoList != null && !flickrPhotoList.isEmpty()) {
            int length = flickrPhotoList.size();
            log.info(new StringBuilder().append("<#doPersistence_FlickrPhotoList> fork <<< "));
            log.info(new StringBuilder().append("input size: << ").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                OnePhotoPersistenceCallable task = new OnePhotoPersistenceCallable(this, geoCmdLine,
                        flickrPhotoList.get(i));
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle().getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </#doPersistence_FlickrPhotoList>"));
        }
    }

    /**
     * The class which defines the unit task of doing FilterFeo for each
     * FlickrPhoto in an list concurrently
     *
     */
    class OneFlickrPhotoFilterGeoCallable implements Callable<String> {
        private final FlickrSearchProbeConcurrent searchProbConc;
        private final FlickrPhoto photo;
        private List<FlickrPhoto> hasGeoList;

        /**
         * 
         * @param searchProbConc
         * @param photo
         * @param hasGeoList
         */
        public OneFlickrPhotoFilterGeoCallable(final FlickrSearchProbeConcurrent searchProbConc,
                final FlickrPhoto photo, List<FlickrPhoto> hasGeoList) {
            this.searchProbConc = searchProbConc;
            this.photo = photo;
            this.hasGeoList = hasGeoList;
        }

        public String call() throws Exception {
            searchProbConc.doFilterGeoOne(hasGeoList, photo);
            String id = photo.getId();
            log.debug(new StringBuilder().append("photo id [").append(id).append("] was appended into hasGeoList"));
            return id;
        }
    }

    /**
     * 
     * This class defines the unit task of doing persistence for each post
     * concurrently
     *
     */
    class OnePhotoPersistenceCallable implements Callable<String> {

        private final FlickrSearchProbeConcurrent searchProbConc;
        private final GeoCmdLine geoCmdLine;
        private final FlickrPhoto photo;

        public OnePhotoPersistenceCallable(final FlickrSearchProbeConcurrent searchProbConc,
                final GeoCmdLine geoCmdLine, final FlickrPhoto photo) {
            this.searchProbConc = searchProbConc;
            this.geoCmdLine = geoCmdLine;
            this.photo = photo;
        }

        @Override
        public String call() throws Exception {
            searchProbConc.doPersistenceOne(geoCmdLine, photo, RECORD_TYPE_POST, true, true);
            String id = photo.getId();
            log.debug(new StringBuilder().append("photo id [").append(id)
                    .append("] was inserted into social_media_record."));
            return id;
        }

    }

}
