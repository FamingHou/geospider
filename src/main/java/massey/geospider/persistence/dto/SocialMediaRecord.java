/**
 * 
 */
package massey.geospider.persistence.dto;

import java.sql.Timestamp;

/**
 * The model class of a record of table social_media_record
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class SocialMediaRecord {

    private int id;
    private String vendorRecordId;
    private String vendorRecordParentId;
    private String message;
    private int recordType;
    private int vendorType;
    private Timestamp createTime;

    /**
     * 
     */
    public SocialMediaRecord() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param vendorRecordId
     * @param vendorRecordParentId
     * @param message
     * @param recordType
     * @param vendorType
     */
    public SocialMediaRecord(String vendorRecordId, String vendorRecordParentId, String message, int recordType,
            int vendorType) {
        this.vendorRecordId = vendorRecordId;
        this.vendorRecordParentId = vendorRecordParentId;
        this.message = message;
        this.recordType = recordType;
        this.vendorType = vendorType;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the vendorRecordId
     */
    public String getVendorRecordId() {
        return vendorRecordId;
    }

    /**
     * @param vendorRecordId
     *            the vendorRecordId to set
     */
    public void setVendorRecordId(String vendorRecordId) {
        this.vendorRecordId = vendorRecordId;
    }

    /**
     * @return the vendorRecordParentId
     */
    public String getVendorRecordParentId() {
        return vendorRecordParentId;
    }

    /**
     * @param vendorRecordParentId
     *            the vendorRecordParentId to set
     */
    public void setVendorRecordParentId(String vendorRecordParentId) {
        this.vendorRecordParentId = vendorRecordParentId;
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
     * @return the recordType
     */
    public int getRecordType() {
        return recordType;
    }

    /**
     * @param recordType
     *            the recordType to set
     */
    public void setRecordType(int recordType) {
        this.recordType = recordType;
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
     * @return the createTime
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

}
