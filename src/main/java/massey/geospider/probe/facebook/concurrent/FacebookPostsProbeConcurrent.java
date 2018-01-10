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
import massey.geospider.message.facebook.FacebookPage;
import massey.geospider.message.facebook.FacebookPost;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.facebook.FacebookPostsResponse;
import massey.geospider.probe.facebook.FacebookPostsProbe;
import massey.geospider.util.GeoExecutorService;

/**
 * The concurrent implementation of class FacebookPostsProbe
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPostsProbeConcurrent extends FacebookPostsProbe {

    private static final Logger log = Logger.getLogger(FacebookPostsProbeConcurrent.class);

    /**
     * @param fbPage
     */
    public FacebookPostsProbeConcurrent(FacebookPage fbPage) {
        super(fbPage);
    }

    /*
     * This is an override method to implement concurrent solution using
     * multi-threading for collecting comments of one post.
     * 
     * @see
     * massey.geospider.probe.facebook.FacebookPostsProbe#doPostCollect(massey.
     * geospider.boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        // super.doPostCollect(geoCmdLine, inputGeoResponse);
        log.debug("FacebookPostsProbeConcurrent#doPostCollect()_start");
        // FacebookPosts level
        FacebookPostsResponse fbPostsRsp = (FacebookPostsResponse) inputGeoResponse;
        if (fbPostsRsp != null) {
            String nextURL = fbPostsRsp.getPaging() == null ? "N/A" : fbPostsRsp.getPaging().getNextURL();
            FacebookPost[] fbPostArray = fbPostsRsp.getDatas();
            int length = fbPostArray.length;
            log.info(new StringBuilder().append("<fetching_comments> fork <<< ").append(nextURL));
            log.info(new StringBuilder().append("input size: <<").append(length));
            List<Callable<String>> listIn = new ArrayList<Callable<String>>(length);
            for (int i = 0; i < length; i++) {
                CollectingCommentsCallable task = new CollectingCommentsCallable(this, geoCmdLine, fbPostArray[i]);
                listIn.add(task);
            }
            // invokeAll tasks
            try {
                List<Future<String>> listOut = GeoExecutorService.getSingle().getService().invokeAll(listIn);
                log.info(new StringBuilder().append("output size: >> ").append(listOut.size()));
            } catch (InterruptedException e) {
                log.error(e, e);
            }
            log.info(new StringBuilder().append(">>> join. </fetching_comments>").append(nextURL));
        }
        log.debug("FacebookPostsProbeConcurrent#doPostCollect()_end.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.facebook.FacebookPostsProbe#
     * doCollectAllCommentsOfOnePost(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.facebook.FacebookPost)
     */
    @Override
    protected void doCollectAllCommentsOfOnePost(GeoCmdLine geoCmdLine, FacebookPost fbPost) {
        if (fbPost == null)
            return;
        FacebookCommentsProbeConcurrent fbCommentsProbeConcurrent = new FacebookCommentsProbeConcurrent(fbPost);
        // inputGeoResponse is null as this is the first query, not next paging
        // query
        fbCommentsProbeConcurrent.collect(geoCmdLine, null);
    }

    /**
     * The method which is used for collecting comments of one post
     * concurrently.
     *
     */
    class CollectingCommentsCallable implements Callable<String> {

        private final FacebookPostsProbeConcurrent fbPostsProbConc;
        private final GeoCmdLine geoCmdLine;
        final FacebookPost fbPost;

        public CollectingCommentsCallable(final FacebookPostsProbeConcurrent fbPostsProbConc,
                final GeoCmdLine geoCmdLine, final FacebookPost fbPost) {
            this.fbPostsProbConc = fbPostsProbConc;
            this.geoCmdLine = geoCmdLine;
            this.fbPost = fbPost;
        }

        @Override
        public String call() throws Exception {
            fbPostsProbConc.doCollectAllCommentsOfOnePost(geoCmdLine, fbPost);
            String id = fbPost.getId();
            log.debug(new StringBuilder().append("Comments of post id [").append(id).append("] were collected."));
            return id; // return comment id for validation.
        }

    }

}
