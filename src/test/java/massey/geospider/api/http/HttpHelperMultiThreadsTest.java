package massey.geospider.api.http;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import junit.framework.TestCase;

public class HttpHelperMultiThreadsTest extends TestCase {

    public static final int URI_COUNT = 50;
    public static final int THREAD_COUNT = 100;
    private static final Logger log = Logger.getLogger(HttpHelperMultiThreadsTest.class);

    public HttpHelperMultiThreadsTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public String runOneTask() {
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("https://graph.facebook.com/v2.11/974239479254393/posts");
        try {
            // using URIBuilder to solve URISyntax issues
            URIBuilder builder = new URIBuilder(sb.toString());
            builder.addParameter("fields", "id,message,created_time,place");
            builder.addParameter("access_token", "2058156934406300%7CNmjKHgsdzUQV05v2nQpogKVh3OU");
            builder.addParameter("pretty", "0");
            builder.addParameter("limit", "100");

            // String result =
            // HttpHelperPoolingClient.doGetAsync(builder.toString());
            // String result =
            // HttpHelperPoolingClient.doGetSync(builder.toString());
            String result = HttpHelper.doGetAsync(builder.toString());

            // log.info("result= "+result);
            return result;
            // long stop = System.currentTimeMillis();
            // log.info("time for one single task: " + (stop - start) + "ms.");
        } catch (URISyntaxException ue) {
            ue.printStackTrace();
            return "";
        }
    }

    public void testSingleThread() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < URI_COUNT; ++i) {
            String ret = runOneTask();
            log.info("result= " + ret);

        }
        long stop = System.currentTimeMillis();
        log.info(Thread.currentThread().getName() + ":testSingleThread: " + (stop - start) + "ms in total.");
        // HttpHelperPoolingClient.shutdown();
    }

    public void testMultiThreads_NewFixedThreadPool() {
        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> list = new ArrayList<Future<String>>();
        for (int i = 0; i < URI_COUNT; ++i) {
            Callable<String> task = new MyCallable("Thread-" + i);
            Future<String> submit = executor.submit(task);
            list.add(submit);
        }
        System.out.println(Thread.currentThread().getName() + ": the size of future list: " + list.size());
        for (Future<String> future : list) {
            // this is not only a print, it can also block threads to make join
            // works.
            try {
                log.info("result= " + Thread.currentThread().getName() + ": " + future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // executor.shutdown();

        long stop = System.currentTimeMillis();
        log.info(Thread.currentThread().getName() + ":testMultiThreads_NewFixedThreadPool: " + (stop - start)
                + "ms in total.");
        // HttpHelperPoolingClient.shutdown();
    }

    // public void testMultiThreadForkJoinPool() {
    // final ForkJoinPool pool = new ForkJoinPool();
    //
    // }

    class MyCallable implements Callable<String> {

        private String name;

        public MyCallable(String name) {
            this.name = name;
        }

        @Override
        public String call() throws Exception {
            String ret = runOneTask();
            System.out.println(Thread.currentThread().getName() + ":call:" + name + " ^|^ " + ret);
            return name + ret;
        }
    }
}
