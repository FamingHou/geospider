/**
 * 
 */
package massey.geospider.message.facebook;

import java.sql.Timestamp;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookComment extends FacebookMessage {

    private String message;
    private Timestamp createdTime;

    /**
     * 
     * @param id
     * @param parentId
     * @param message
     * @param createdTime
     */
    public FacebookComment(String id, String parentId, String message, Timestamp createdTime) {
        super(id, parentId);
        this.message = message;
        this.createdTime = createdTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookComment [message=" + message + ", createdTime=" + createdTime + ", toString()="
                + super.toString() + "]";
    }

}
