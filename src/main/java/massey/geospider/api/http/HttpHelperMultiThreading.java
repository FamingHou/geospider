///**
// * 
// */
//package massey.geospider.api.http;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//
///**
// * @author Frank Hou (faming.hou@gmail.com)
// *
// */
//public class HttpHelperMultiThreading {
//
//    private static final Logger log = Logger.getLogger(HttpHelperMultiThreading.class);
//
//    static final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//
//    static {
//        cm.setMaxTotal(1000);
//        log.debug("PoolingHttpClientConnectionManager.setMaxTotal(1000)");
//    }
//
//    static final CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
//
//    /**
//     * 
//     */
//    private HttpHelperMultiThreading() {
//    }
//
//    /**
//     * Does HTTP Get request
//     * 
//     * @param urisToGet
//     *            an array of the request urls of String type
//     * @return a list of Future objects with String type.
//     */
//    public static List<Future<String>> doGet(String[] urisToGet) {
//        try {
//            // create a GetCallable thread for each URI
//            GetCallable[] tasks = new GetCallable[urisToGet.length];
//            for (int i = 0; i < tasks.length; i++) {
//                HttpGet httpget = new HttpGet(urisToGet[i]);
//                tasks[i] = new GetCallable(httpclient, httpget, i + 1);
//            }
//
//            //
//            ExecutorService executor = Executors.newFixedThreadPool(1000);
//            // ExecutorService executor = Executors.newCachedThreadPool();
//            // ExecutorService executor = Executors.newSingleThreadExecutor();
//
//            List<Future<String>> results = executor.invokeAll(Arrays.asList(tasks));
//            // for (Future<String> result : results) {
//            // log.info("result: " + result.get());
//            // }
//
//            log.info("Tasks of CallableExecutedByFixedThreadPool were done.");
//            executor.shutdown();
//            log.info("Executor was shut down.");
//            return results;
//        } catch (Exception e) {
//            log.error(e, e);
//        } finally {
//            try {
//                httpclient.close();
//            } catch (IOException e) {
//                log.error(e, e);
//            }
//        }
//        return null;
//    }
//
//    static class GetCallable implements Callable<String> {
//        private final CloseableHttpClient httpClient;
//        private final HttpContext context;
//        private final HttpGet httpget;
//        private final int id;
//
//        public GetCallable(final CloseableHttpClient httpClient, final HttpGet httpget, int id) {
//            this.httpClient = httpClient;
//            this.context = new BasicHttpContext();
//            this.httpget = httpget;
//            this.id = id;
//        }
//
//        @Override
//        public String call() throws Exception {
//            final StringBuilder sb = new StringBuilder();
//            try {
//                log.info(id + " - about to get something from " + httpget.getURI());
//                CloseableHttpResponse response = httpClient.execute(httpget, context);
//                try {
//                    log.info(id + " - get executed");
//                    // get the response body as a String
//                    HttpEntity entity = response.getEntity();
//                    if (entity != null) {
//                        sb.append(entity != null ? EntityUtils.toString(entity) : "");
//                    }
//                } finally {
//                    response.close();
//                }
//            } catch (Exception e) {
//                log.error(id + " - error: " + e);
//            }
//            return sb.toString();
//        }
//
//    }
//
//}
