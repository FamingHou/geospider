/**
 * 
 */
package massey.geospider.message.flickr;

import java.sql.Timestamp;

/**
 * One comment object in Flickr
 * 
 * <pre>
    {
        "id": "2149990-4105054854-72157622680956043",
        "author": "7359335@N02",
        "author_is_deleted": 0,
        "authorname": "Lee Sie",
        "iconserver": "8126",
        "iconfarm": 9,
        "datecreate": "1258260705",
        "permalink": "https://www.flickr.com/photos/stuckincustoms/4105054854/#comment72157622680956043",
        "path_alias": "lee_sie",
        "realname": "Lee Sie",
        "_content": "Wow this place looks amazing!"
    }
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FlickrComment extends FlickrMessage {

    private String id;
    private String text; // _content
    private Timestamp createdAt; // datecreate

    /**
     * 
     */
    public FlickrComment() {
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the createdAt
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     *            the createdAt to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FlickrComment [id=" + id + ", text=" + text + ", createdAt=" + createdAt + "]";
    }

}
