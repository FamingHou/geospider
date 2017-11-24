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
     * if this object has no parentId, then parentId will be saved as char '_':
     * <ul>
     * <li>the parentId of a pageId is '_' which means pageId has no parentId
     * </li>
     * <li>the parentId of a postId is a pageId</li>
     * <li>the parentId of a commentId could be a postId</li>
     * <li>the parentId of a commentId could be a commendId which means this
     * comment is a reply of another comment</li>
     * </ul>
     */
    private String parentId;

    /**
     * 
     * @param id
     * @param parentId
     */
    public FacebookMessage(String id, String parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }



    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }



    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookMessage [id=" + id + ", parentId=" + parentId + ", toString()=" + super.toString() + "]";
    }

}
