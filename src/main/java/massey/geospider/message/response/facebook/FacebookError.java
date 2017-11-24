/**
 * 
 */
package massey.geospider.message.response.facebook;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookError {

    private String message;
    private String type;
    private int code;
    private int errorSubcode;
    private String fbTraceId;

    /**
     * 
     * @param message
     * @param type
     * @param code
     * @param errorSubcode
     * @param fbTraceId
     */
    public FacebookError(String message, String type, int code, int errorSubcode, String fbTraceId) {
        this.message = message;
        this.type = type;
        this.code = code;
        this.errorSubcode = errorSubcode;
        this.fbTraceId = fbTraceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookError [message=" + message + ", type=" + type + ", code=" + code + ", errorSubcode="
                + errorSubcode + ", fbTraceId=" + fbTraceId + "]";
    }

}
