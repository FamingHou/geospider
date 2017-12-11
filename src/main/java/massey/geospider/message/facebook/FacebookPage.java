/**
 * 
 */
package massey.geospider.message.facebook;

/**
 * <pre>
 * sizeOfPostsHasKeyword = sizeOfPostsHasKeywordAndGeo + sizeOfPostsHasKeywordNoGeo
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPage extends FacebookMessage {

    /** The name of this FacebookPage */
    private String name;

    /** The number of posts in total */
    protected int sizeOfPostsInTotal = 0;
    /** The number of posts that has keyword. */
    protected int sizeOfPostsHasKeyword = 0;
    /** The number of posts that has both keyword and geoplaces */
    protected int sizeOfPostsHasKeywordAndGeo = 0;

    /** The number of comments in total */
    protected int sizeOfCommentsInTotal = 0;
    /** The number of comments that has keyword. */
    protected int sizeOfCommentsHasKeyword = 0;
    /** The number of comments that has both keyword and geoplaces */
    protected int sizeOfCommentsHasKeywordAndGeo = 0;

    /** The number of replies in total */
    protected int sizeOfRepliesInTotal = 0;
    /** The number of replies that has keyword. */
    protected int sizeOfRepliesHasKeyword = 0;
    /** The number of replies that has both keyword and geoplaces */
    protected int sizeOfRepliesHasKeywordAndGeo = 0;

    /**
     * 
     * @param id
     * @param parent
     * @param name
     */
    public FacebookPage(String id, FacebookMessage parent, String name) {
        super(id, parent);
        this.name = name;
    }

    /**
     * @return the sizeOfPostsInTotal
     */
    public int getSizeOfPostsInTotal() {
        return sizeOfPostsInTotal;
    }

    /**
     * @param sizeOfPostsInTotal
     *            the sizeOfPostsInTotal to set
     */
    public void setSizeOfPostsInTotal(int sizeOfPostsInTotal) {
        this.sizeOfPostsInTotal = sizeOfPostsInTotal;
    }

    /**
     * @return the sizeOfPostsHasKeyword
     */
    public int getSizeOfPostsHasKeyword() {
        return sizeOfPostsHasKeyword;
    }

    /**
     * @param sizeOfPostsHasKeyword
     *            the sizeOfPostsHasKeyword to set
     */
    public void setSizeOfPostsHasKeyword(int sizeOfPostsHasKeyword) {
        this.sizeOfPostsHasKeyword = sizeOfPostsHasKeyword;
    }

    /**
     * @return the sizeOfPostsHasKeywordAndGeo
     */
    public int getSizeOfPostsHasKeywordAndGeo() {
        return sizeOfPostsHasKeywordAndGeo;
    }

    /**
     * @param sizeOfPostsHasKeywordAndGeo
     *            the sizeOfPostsHasKeywordAndGeo to set
     */
    public void setSizeOfPostsHasKeywordAndGeo(int sizeOfPostsHasKeywordAndGeo) {
        this.sizeOfPostsHasKeywordAndGeo = sizeOfPostsHasKeywordAndGeo;
    }

    /**
     * @return the sizeOfCommentsInTotal
     */
    public int getSizeOfCommentsInTotal() {
        return sizeOfCommentsInTotal;
    }

    /**
     * @param sizeOfCommentsInTotal
     *            the sizeOfCommentsInTotal to set
     */
    public void setSizeOfCommentsInTotal(int sizeOfCommentsInTotal) {
        this.sizeOfCommentsInTotal = sizeOfCommentsInTotal;
    }

    /**
     * @return the sizeOfCommentsHasKeyword
     */
    public int getSizeOfCommentsHasKeyword() {
        return sizeOfCommentsHasKeyword;
    }

    /**
     * @param sizeOfCommentsHasKeyword
     *            the sizeOfCommentsHasKeyword to set
     */
    public void setSizeOfCommentsHasKeyword(int sizeOfCommentsHasKeyword) {
        this.sizeOfCommentsHasKeyword = sizeOfCommentsHasKeyword;
    }

    /**
     * @return the sizeOfCommentsHasKeywordAndGeo
     */
    public int getSizeOfCommentsHasKeywordAndGeo() {
        return sizeOfCommentsHasKeywordAndGeo;
    }

    /**
     * @param sizeOfCommentsHasKeywordAndGeo
     *            the sizeOfCommentsHasKeywordAndGeo to set
     */
    public void setSizeOfCommentsHasKeywordAndGeo(int sizeOfCommentsHasKeywordAndGeo) {
        this.sizeOfCommentsHasKeywordAndGeo = sizeOfCommentsHasKeywordAndGeo;
    }

    /**
     * @return the sizeOfRepliesInTotal
     */
    public int getSizeOfRepliesInTotal() {
        return sizeOfRepliesInTotal;
    }

    /**
     * @param sizeOfRepliesInTotal
     *            the sizeOfRepliesInTotal to set
     */
    public void setSizeOfRepliesInTotal(int sizeOfRepliesInTotal) {
        this.sizeOfRepliesInTotal = sizeOfRepliesInTotal;
    }

    /**
     * @return the sizeOfRepliesHasKeyword
     */
    public int getSizeOfRepliesHasKeyword() {
        return sizeOfRepliesHasKeyword;
    }

    /**
     * @param sizeOfRepliesHasKeyword
     *            the sizeOfRepliesHasKeyword to set
     */
    public void setSizeOfRepliesHasKeyword(int sizeOfRepliesHasKeyword) {
        this.sizeOfRepliesHasKeyword = sizeOfRepliesHasKeyword;
    }

    /**
     * @return the sizeOfRepliesHasKeywordAndGeo
     */
    public int getSizeOfRepliesHasKeywordAndGeo() {
        return sizeOfRepliesHasKeywordAndGeo;
    }

    /**
     * @param sizeOfRepliesHasKeywordAndGeo
     *            the sizeOfRepliesHasKeywordAndGeo to set
     */
    public void setSizeOfRepliesHasKeywordAndGeo(int sizeOfRepliesHasKeywordAndGeo) {
        this.sizeOfRepliesHasKeywordAndGeo = sizeOfRepliesHasKeywordAndGeo;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookPage [name=" + name + ", toString()=" + super.toString() + "]";
    }

}
