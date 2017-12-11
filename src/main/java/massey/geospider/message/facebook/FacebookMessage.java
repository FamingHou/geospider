/**
 * 
 */
package massey.geospider.message.facebook;

import massey.geospider.message.GeoMessage;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class FacebookMessage extends GeoMessage {

    /** the id of page, post or comment */
    private String id;

    /**
     * The parent message object of current message object:
     * <ul>
     * <li>parent is null if this object is an object of class FacebookPage</li>
     * <li>parent is an object of class FacebookPage if this object is an object
     * of class FacebookPost</li>
     * <li>parent is an object of class FacebookPost or FacebookComment if this
     * object is an object of class FacebookComment</li>
     * </ul>
     */
    private FacebookMessage parent;

    /**
     * 
     * @param id
     * @param parentId
     */
    public FacebookMessage(String id, FacebookMessage parent) {
        this.id = id;
        this.parent = parent;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the parent
     */
    public FacebookMessage getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(FacebookMessage parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookMessage [id=" + id + ", parent=" + parent + ", toString()=" + super.toString() + "]";
    }

}
