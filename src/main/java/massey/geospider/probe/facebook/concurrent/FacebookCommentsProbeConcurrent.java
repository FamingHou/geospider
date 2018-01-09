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
     * multi-threading.
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
            log.info(new StringBuilder().append("fork <<< ").append(nextURL));
            log.info(new StringBuilder().append("input size: <<").append(length));
            // option-1: when using submit, Future<String> must be printed to make join works.
            // option-2: using invokeAll.
//            List<Future<String>> list = new ArrayList<Future<String>>(length);
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                CollectingRepliesCallable task = new CollectingRepliesCallable(this, geoCmdLine, fbCommentArray[i]);
//                Future<String> submit = GeoExecutorService.getSingle().getService().submit(task);
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle().getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join.").append(nextURL));
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

}
