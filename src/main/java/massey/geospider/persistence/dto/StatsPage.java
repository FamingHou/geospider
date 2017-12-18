/**
 * 
 */
package massey.geospider.persistence.dto;

import java.sql.Timestamp;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class StatsPage {

    private long id;
    private String keyword;
    private int vendorType;
    private String pageId;
    private String pageName;

    private int sizeOfPostsInTotal;
    private int sizeOfPostsHasKeyword;
    private int sizeOfPostsHasKeywordAndGeo;

    private int sizeOfCommentsInTotal;
    private int sizeOfCommentsHasKeyword;
    private int sizeOfCommentsHasKeywordAndGeo;

    private int sizeOfRepliesInTotal;
    private int sizeOfRepliesHasKeyword;
    private int sizeOfRepliesHasKeywordAndGeo;

    private Timestamp createdTime;
    private Timestamp updatedTime;
    private boolean isNeedRefresh;

    /**
     * 
     */
    public StatsPage() {
    }

    /**
     * @return the pageId
     */
    public String getPageId() {
        return pageId;
    }

    /**
     * @param pageId
     *            the pageId to set
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * @return the pageName
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * @param pageName
     *            the pageName to set
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
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
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword
     *            the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the vendorType
     */
    public int getVendorType() {
        return vendorType;
    }

    /**
     * @param vendorType
     *            the vendorType to set
     */
    public void setVendorType(int vendorType) {
        this.vendorType = vendorType;
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

    /**
     * @return the updatedTime
     */
    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    /**
     * @param updatedTime
     *            the updatedTime to set
     */
    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * @return the isNeedRefresh
     */
    public boolean isNeedRefresh() {
        return isNeedRefresh;
    }

    /**
     * @param isNeedRefresh
     *            the isNeedRefresh to set
     */
    public void setNeedRefresh(boolean isNeedRefresh) {
        this.isNeedRefresh = isNeedRefresh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "StatsPage [id=" + id + ", keyword=" + keyword + ", vendorType=" + vendorType + ", pageId=" + pageId
                + ", pageName=" + pageName + ", sizeOfPostsInTotal=" + sizeOfPostsInTotal + ", sizeOfPostsHasKeyword="
                + sizeOfPostsHasKeyword + ", sizeOfPostsHasKeywordAndGeo=" + sizeOfPostsHasKeywordAndGeo
                + ", sizeOfCommentsInTotal=" + sizeOfCommentsInTotal + ", sizeOfCommentsHasKeyword="
                + sizeOfCommentsHasKeyword + ", sizeOfCommentsHasKeywordAndGeo=" + sizeOfCommentsHasKeywordAndGeo
                + ", sizeOfRepliesInTotal=" + sizeOfRepliesInTotal + ", sizeOfRepliesHasKeyword="
                + sizeOfRepliesHasKeyword + ", sizeOfRepliesHasKeywordAndGeo=" + sizeOfRepliesHasKeywordAndGeo
                + ", createdTime=" + createdTime + ", updatedTime=" + updatedTime + ", isNeedRefresh=" + isNeedRefresh
                + "]";
    }

}
