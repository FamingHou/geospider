/**
 * 
 */
package massey.geospider.message.facebook;

import java.sql.Timestamp;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPost extends FacebookMessage {

    private String message;
    private Timestamp createdTime;

    /**
     * 
     * @param id
     * @param parentId
     * @param message
     * @param createdTime
     */
    public FacebookPost(String id, String parentId, String message, Timestamp createdTime) {
        super(id, parentId);
        this.message = message;
        this.createdTime = createdTime;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the createdTime
     */
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookPost [message=" + message + ", createdTime=" + createdTime + ", toString()=" + super.toString()
                + "]";
    }

}
