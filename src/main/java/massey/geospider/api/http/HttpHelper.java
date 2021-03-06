/**
 * 
 */
package massey.geospider.api.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import massey.geospider.conf.PropReader;
import massey.geospider.global.GeoConstants;

/**
 * Do HTTP request and get response by HttpClients.createDefault and
 * ResponseHandler.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class HttpHelper implements GeoConstants {

    private static final Logger log = Logger.getLogger(HttpHelper.class);

    /**
     * 
     */
    private HttpHelper() {
    }

    /**
     * Does HTTP Get request. This is a asynchronized method by using
     * ResponseHandler<String>
     * 
     * @param url
     *            the request url of String type
     * @return the HTTP response with the type of JSON format
     */
    public static String doGetAsync(String urlString) {
        try {
            return doGetAsyncWithHeaders(urlString, null);
        } catch (GeoClientProtocolException e) {
            log.error(e, e);
            return "";
        }
    }

    /**
     * Does HTTP Get request with headers.
     * 
     * This is a asynchronized method by using ResponseHandler<String>
     * 
     * @param url
     *            the request url of String type
     * @param headerMap
     * @return the HTTP response with the type of JSON format
     * @throws GeoClientProtocolException
     */
    public static String doGetAsyncWithHeaders(String urlString, Map<String, String> headerMap)
            throws GeoClientProtocolException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(urlString);
            if (headerMap != null && !headerMap.isEmpty()) {
                for (String name : headerMap.keySet()) {
                    httpGet.addHeader(name, headerMap.get(name));
                }
            }
            log.info("Executing request: ===> " + httpGet.getRequestLine());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new GeoClientProtocolException("Unexpected response ", status);
                    }
                }
            };
            String responseBody = httpclient.execute(httpGet, responseHandler);
            log.info("----------------------------------------");
            log.info(responseBody);
            log.info("----------------------------------------");
            return responseBody;
        } catch (GeoClientProtocolException ex) {
            throw ex;
        } catch (ClientProtocolException ex) {
            log.error(ex, ex);
        } catch (IOException ex) {
            log.error(ex, ex);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(e, e);
            }
        }
        return "";
    }

    /**
     * Does Get request for Twitter APIs.
     * 
     * Reconnects the Twitter API with a different token when the application
     * reaches the rate limitation using current token.
     * 
     * @param urlString
     * @return
     */
    public static String doGetAsync4Twitter(String urlString) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(TW_HEADER_AUTHORIZATION_PROP_NAME, PropReader.getNextTwitterAccessToken());
        try {
            return doGetAsyncWithHeaders(urlString, headerMap);
        } catch (GeoClientProtocolException e) {
            log.info("GeoClientProtocolException of Twitter, statusCode = " + e.getStatusCode());
            log.error(e, e);
            if (e.getStatusCode() == 429) {
                // sleep 500ms and reconnect
                log.info("sleep 500ms...");
                try {
                    Thread.sleep(500);
                    log.info("reconnecting...");
                    String responseStr = doGetAsync4Twitter(urlString);
                    log.info("got response after sleeping.");
                    return responseStr;
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            }
        }
        return "";
    }
}
