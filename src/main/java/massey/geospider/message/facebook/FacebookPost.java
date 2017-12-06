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
    private FacebookPlace fbPlace;

    /**
     * 
     * @param id
     * @param parentId
     * @param message
     * @param createdTime
     * @param fbPlace
     */
    public FacebookPost(String id, String parentId, String message, Timestamp createdTime, FacebookPlace fbPlace) {
        super(id, parentId);
        this.message = message;
        this.createdTime = createdTime;
        this.fbPlace = fbPlace;
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

    /**
     * @return the fbPlace
     */
    public FacebookPlace getFbPlace() {
        return fbPlace;
    }

    /**
     * @param fbPlace
     *            the fbPlace to set
     */
    public void setFbPlace(FacebookPlace fbPlace) {
        this.fbPlace = fbPlace;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
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
        return "FacebookPost [message=" + message + ", createdTime=" + createdTime + ", fbPlace=" + fbPlace
                + ", toString()=" + super.toString() + "]";
    }

}
