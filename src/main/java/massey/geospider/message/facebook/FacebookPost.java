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

    private Timestamp createdTime;
    private FacebookPlace fbPlace;

    /**
     * 
     * @param id
     * @param parent
     * @param message
     * @param createdTime
     * @param fbPlace
     */
    public FacebookPost(String id, FacebookMessage parent, String message, Timestamp createdTime,
            FacebookPlace fbPlace) {
        super(id, parent);
        this.message = message;
        this.createdTime = createdTime;
        this.fbPlace = fbPlace;
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
        return "FacebookPost [createdTime=" + createdTime + ", fbPlace=" + fbPlace + ", toString()=" + super.toString()
                + "]";
    }

}
