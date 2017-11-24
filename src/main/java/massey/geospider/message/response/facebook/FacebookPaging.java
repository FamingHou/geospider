/**
 * 
 */
package massey.geospider.message.response.facebook;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPaging {

    private String nextURL;
    private String previousURL;

    /**
     * 
     * @param nextURL
     * @param previousURL
     */
    public FacebookPaging(String nextURL, String previousURL) {
        this.nextURL = nextURL;
        this.previousURL = previousURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookPaging [nextURL=" + nextURL + ", previousURL=" + previousURL + "]";
    }

    /**
     * @return the nextURL
     */
    public String getNextURL() {
        return nextURL;
    }

    /**
     * @return the previousURL
     */
    public String getPreviousURL() {
        return previousURL;
    }

}
