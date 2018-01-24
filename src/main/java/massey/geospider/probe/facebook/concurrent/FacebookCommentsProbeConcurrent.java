/**
 * 
 */
package massey.geospider.probe.facebook.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.facebook.FacebookComment;
import massey.geospider.message.facebook.FacebookMessage;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookCommentsResponse;
import massey.geospider.probe.facebook.FacebookCommentsProbe;
import massey.geospider.util.GeoExecutorService;

/**
 * The current implementation of class FacebookCommentsProbe
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookCommentsProbeConcurrent extends FacebookCommentsProbe {

    private static final Logger log = Logger.getLogger(FacebookCommentsProbeConcurrent.class);

    /**
     * @param fbParent
     */
    public FacebookCommentsProbeConcurrent(FacebookMessage fbParent) {
        super(fbParent);
    }

    /*
     * This is an override method to implement concurrent solution using
     * multi-threading for persistence of one Comment
     * 
     * @see massey.geospider.probe.facebook.FacebookCommentsProbe#doPersistence(
     * massey. geospider.boot.GeoCmdLine, java.util.List)
     */
    @Override
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FacebookComment> fbCommentList) {
        if (fbCommentList != null && !fbCommentList.isEmpty()) {
            int length = fbCommentList.size();
            log.info(new StringBuilder().append("<doPersistence_Comments> fork <<< "));
            log.info(new StringBuilder().append("input size: <<").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                OneCommentPersistenceCallable task = new OneCommentPersistenceCallable(this, geoCmdLine,
                        fbCommentList.get(i));
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle().getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </doPersistence_Comments>"));
        }
    }

    /*
     * This is an override method to implement concurrent solution using
     * multi-threading for FilterGeo of Comments
     * 
     * @see
     * massey.geospider.probe.facebook.FacebookCommentsProbe#doFilterGeo(java.
     * util. List)
     */
    @Override
    protected List<FacebookComment> doFilterGeo(List<FacebookComment> fbCommentList) {
        List<FacebookComment> hasGeoList = new ArrayList<>();
        if (fbCommentList != null && !fbCommentList.isEmpty()) {
            int length = fbCommentList.size();
            log.info(new StringBuilder().append("<doFilterGeo_Comments> fork <<< "));
            log.info(new StringBuilder().append("input size: <<").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                OneCommentFilterGeoCallable task = new OneCommentFilterGeoCallable(this, fbCommentList.get(i),
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
            log.info(new StringBuilder().append(">>> join. </doFilterGeo_Comments>"));
        }
        return hasGeoList;
    }

    /*
     * This is an override method to implement concurrent solution using
     * multi-threading for persistence of Comments.
     * 
     * @see massey.geospider.probe.facebook.FacebookCommentsProbe#doPostCollect(
     * massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("FacebookCommentsProbeConcurrent#doPostCollect()_start");
        // FacebookComments level
        FacebookCommentsResponse fbCommentsRsp = (FacebookCommentsResponse) inputGeoResponse;
        if (fbCommentsRsp != null) {
            String nextURL = fbCommentsRsp.getPaging() == null ? "N/A" : fbCommentsRsp.getPaging().getNextURL();
            FacebookComment[] fbCommentArray = fbCommentsRsp.getDatas();
            int length = fbCommentArray.length;
            log.info(new StringBuilder().append("<fetching_replies> fork <<< ").append(nextURL));
            log.info(new StringBuilder().append("input size: <<").append(length));
            // option-1: when using submit, Future<String> must be printed to
            // make join works.
            // option-2: using invokeAll.
            // List<Future<String>> list = new
            // ArrayList<Future<String>>(length);
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                CollectingRepliesCallable task = new CollectingRepliesCallable(this, geoCmdLine, fbCommentArray[i]);
                // Future<String> submit =
                // GeoExecutorService.getSingle().getService().submit(task);
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle().getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </fetching_replies>").append(nextURL));
        }
        log.debug("FacebookCommentsProbeConcurrent#doPostCollect()_end.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.facebook.FacebookCommentsProbe#
     * doCollectAllRepliesOfOneComment(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.facebook.FacebookComment)
     */
    @Override
    protected void doCollectAllRepliesOfOneComment(GeoCmdLine geoCmdLine, FacebookComment fbComment) {
        if (fbComment == null)
            return;
        FacebookRepliesProbeConcurrent fbRepliesProbeConcurrent = new FacebookRepliesProbeConcurrent(fbComment);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbRepliesProbeConcurrent.collect(geoCmdLine, null);
    }

    /**
     * The method which is used for collecting replies of one comment
     * concurrently.
     *
     */
    class CollectingRepliesCallable implements Callable<String> {

        private final FacebookCommentsProbeConcurrent fbCommProbConc;
        private final GeoCmdLine geoCmdLine;
        final FacebookComment fbComment;

        public CollectingRepliesCallable(final FacebookCommentsProbeConcurrent fbCommProbConc,
                final GeoCmdLine geoCmdLine, final FacebookComment fbComment) {
            this.fbCommProbConc = fbCommProbConc;
            this.geoCmdLine = geoCmdLine;
            this.fbComment = fbComment;
        }

        @Override
        public String call() throws Exception {
            fbCommProbConc.doCollectAllRepliesOfOneComment(geoCmdLine, fbComment);
            String id = fbComment.getId();
            log.debug(new StringBuilder().append("Replies of comment id [").append(id).append("] was collected."));
            return id; // return comment id for validation.
        }

    }

    /**
     * 
     * This class is used for doing persistence for one Comment concurrently
     *
     */
    class OneCommentPersistenceCallable implements Callable<String> {

        private final FacebookCommentsProbeConcurrent fbCommentsProbConc;
        private final GeoCmdLine geoCmdLine;
        private final FacebookComment fbComment;

        public OneCommentPersistenceCallable(final FacebookCommentsProbeConcurrent fbCommentsProbConc,
                final GeoCmdLine geoCmdLine, final FacebookComment fbComment) {
            this.fbCommentsProbConc = fbCommentsProbConc;
            this.geoCmdLine = geoCmdLine;
            this.fbComment = fbComment;
        }

        @Override
        public String call() throws Exception {
            fbCommentsProbConc.doPersistenceOne(geoCmdLine, fbComment);
            fbCommentsProbConc.doPostPersistenceOne(geoCmdLine, fbComment);
            String id = fbComment.getId();
            log.debug(new StringBuilder().append("comment id [").append(id)
                    .append("] were inserted into social_media_record."));
            return id;
        }

    }

    /**
     * This class is used for doing FilterFeo for one Comment concurrently
     *
     */
    class OneCommentFilterGeoCallable implements Callable<String> {
        private final FacebookCommentsProbeConcurrent fbCommentsProbConc;
        private final FacebookComment fbComment;
        private List<FacebookComment> hasGeoList;

        public OneCommentFilterGeoCallable(final FacebookCommentsProbeConcurrent fbCommentsProbConc,
                final FacebookComment fbComment, List<FacebookComment> hasGeoList) {
            this.fbCommentsProbConc = fbCommentsProbConc;
            this.fbComment = fbComment;
            this.hasGeoList = hasGeoList;
        }

        public String call() throws Exception {
            fbCommentsProbConc.doFilterGeoOne(hasGeoList, fbComment);
            String id = fbComment.getId();
            log.debug(new StringBuilder().append("comment id [").append(id).append("] was appended into hasGeoList"));
            return id;
        }
    }

}
