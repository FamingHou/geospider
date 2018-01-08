/**
 * 
 */
package massey.geospider.api.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Do HTTP request and get response by a cached pooling client connection
 * manager.
 * 
 * 
 * @deprecated In multi threading environment, it is tested that both method
 *             {@link doGetSync} and {@link doGetASync} are much slower than the
 *             method of {@link doGetAsync} of class {@link HttpHelper}
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class HttpHelperPoolingClient {

    private static final Logger log = Logger.getLogger(HttpHelperPoolingClient.class);

    static final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    static {
        cm.setMaxTotal(1000);
        log.debug("PoolingHttpClientConnectionManager.setMaxTotal(1000)");
    }

    static final CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

    /**
     * 
     */
    private HttpHelperPoolingClient() {
    }

    /**
     * Does HTTP Get request by a polling clients connection manager. This is a
     * synchronized method.
     * 
     * @param url
     *            the request url of String type
     * @return the HTTP response with the type of JSON format
     */
    public static String doGetSync(String urlString) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpget = new HttpGet(urlString);
            response = httpclient.execute(httpget, new BasicHttpContext());
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                // get the response body as a String
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    log.info("----------------------------------------");
                    log.info(responseBody);
                    log.info("----------------------------------------");
                    return responseBody;
                }
            } else {
                log.error("Unexpected response status: " + status);
            }
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e, e);
                }
            }
        }
        return "";
    }

    /**
     * Does HTTP Get request by a polling clients connection manager. This is a
     * asynchronized method by using ResponseHandler<String>
     * 
     * @param url
     *            the request url of String type
     * @return the HTTP response with the type of JSON format
     */
    public static String doGetAsync(String urlString) {
        try {
            HttpGet httpGet = new HttpGet(urlString);
            // log.debug("Executing request: ===> " + httpGet.getRequestLine());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = httpclient.execute(httpGet, responseHandler);
            log.info("----------------------------------------");
            log.info(responseBody);
            log.info("----------------------------------------");
            return responseBody;
        } catch (Exception ex) {
            log.error(ex, ex);
        }

        return "";
    }

    /**
     * The hook method which will be called before the application is going to
     * exist for closing HttpClient and PoolingHttpClientConnectionManager
     */
    public static void shutdown() {
        try {
            if (httpclient != null)
                httpclient.close();
            if (cm != null)
                cm.close();
        } catch (IOException e) {
            log.error(e, e);
        }
    }
}
