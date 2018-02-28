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
import massey.geospider.probe.facebook.FacebookRepliesProbe;
import massey.geospider.util.GeoExecutorService;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookRepliesProbeConcurrent extends FacebookRepliesProbe {

    private static final Logger log = Logger.getLogger(FacebookRepliesProbeConcurrent.class);

    /**
     * @param fbParent
     */
    public FacebookRepliesProbeConcurrent(FacebookMessage fbParent) {
        super(fbParent);
    }

    /*
     * This is an override method to implement concurrent solution using
     * multi-threading for persistence of Replies.
     * 
     * @see massey.geospider.probe.facebook.FacebookCommentsProbe#doPersistence(
     * massey.geospider.boot.GeoCmdLine, java.util.List)
     */
    @Override
    protected void doPersistence(GeoCmdLine geoCmdLine, List<FacebookComment> fbReplyList) {
        // super.doPersistence(geoCmdLine, fbCommentList);
        if (fbReplyList != null && !fbReplyList.isEmpty()) {
            int length = fbReplyList.size();
            log.info(new StringBuilder().append("<doPersistence_Replies> fork <<< "));
            log.info(new StringBuilder().append("input size: <<").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                OneReplyPersistenceCallable task = new OneReplyPersistenceCallable(this, geoCmdLine,
                        fbReplyList.get(i));
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle(geoCmdLine).getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </doPersistence_Replies>"));
        }
    }

    /*
     * This is an override method to implement concurrent solution using
     * multi-threading for FilterGeo of Replies
     * 
     * @see
     * massey.geospider.probe.facebook.FacebookRepliesProbe#doFilterGeo(java.
     * util. List)
     */
    @Override
    protected List<FacebookComment> doFilterGeo(GeoCmdLine geoCmdLine, List<FacebookComment> fbReplyList) {
        List<FacebookComment> hasGeoList = new ArrayList<>();
        if (fbReplyList != null && !fbReplyList.isEmpty()) {
            int length = fbReplyList.size();
            log.info(new StringBuilder().append("<doFilterGeo_Replies> fork <<< "));
            log.info(new StringBuilder().append("input size: <<").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                OneReplyFilterGeoCallable task = new OneReplyFilterGeoCallable(this, fbReplyList.get(i), hasGeoList);
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle(geoCmdLine).getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </doFilterGeo_Replies>"));
        }
        return hasGeoList;
    }

    /**
     * 
     * This class is used for doing persistence for one Reply concurrently
     *
     */
    class OneReplyPersistenceCallable implements Callable<String> {

        private final FacebookRepliesProbeConcurrent fbRepliesProbConc;
        private final GeoCmdLine geoCmdLine;
        private final FacebookComment fbReply;

        public OneReplyPersistenceCallable(final FacebookRepliesProbeConcurrent fbRepliesProbConc,
                final GeoCmdLine geoCmdLine, final FacebookComment fbReply) {
            this.fbRepliesProbConc = fbRepliesProbConc;
            this.geoCmdLine = geoCmdLine;
            this.fbReply = fbReply;
        }

        @Override
        public String call() throws Exception {
            fbRepliesProbConc.doPersistenceOne(geoCmdLine, fbReply);
            fbRepliesProbConc.doPostPersistenceOne(geoCmdLine, fbReply);
            String id = fbReply.getId();
            log.debug(new StringBuilder().append("reply id [").append(id)
                    .append("] were inserted into social_media_record."));
            return id;
        }
    }

    /**
     * This class is used for doing FilterFeo for one Reply concurrently
     *
     */
    class OneReplyFilterGeoCallable implements Callable<String> {
        private final FacebookRepliesProbeConcurrent fbRepliesProbConc;
        private final FacebookComment fbReply;
        private List<FacebookComment> hasGeoList;

        public OneReplyFilterGeoCallable(final FacebookRepliesProbeConcurrent fbRepliesProbConc,
                final FacebookComment fbReply, List<FacebookComment> hasGeoList) {
            this.fbRepliesProbConc = fbRepliesProbConc;
            this.fbReply = fbReply;
            this.hasGeoList = hasGeoList;
        }

        public String call() throws Exception {
            fbRepliesProbConc.doFilterGeoOne(hasGeoList, fbReply);
            String id = fbReply.getId();
            log.debug(new StringBuilder().append("reply id [").append(id).append("] was appended into hasGeoList"));
            return id;
        }
    }

}
