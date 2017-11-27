/**
 * 
 */
package massey.geospider.api.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class HttpHelper {

    private static final Logger log = Logger.getLogger(HttpHelper.class);

    /**
     * 
     */
    private HttpHelper() {
    }

    /**
     * Does HTTP Get request
     * 
     * @param url
     *            the request url of String type
     * @return the HTTP response with the type of JSON format
     */
    public static String doGet(String urlString) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(urlString);
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
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error(e, e);
            }
        }
        return "";
    }

}
