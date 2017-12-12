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

    private Timestamp createdTime;

    /**
     * 
     * @param id
     * @param parent
     * @param message
     * @param createdTime
     */
    public FacebookComment(String id, FacebookMessage parent, String message, Timestamp createdTime) {
        super(id, parent);
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
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the createdTime
     */
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    /**
     * @param createdTime
     *            the createdTime to set
     */
    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookComment [createdTime=" + createdTime + ", toString()=" + super.toString() + "]";
    }

}
