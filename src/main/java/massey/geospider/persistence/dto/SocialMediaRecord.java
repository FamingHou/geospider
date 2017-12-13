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

    private long id;
    private String vendorRecordId;
    private String vendorRecordParentId;
    private String message;
    private int recordType;
    private int vendorType;
    private Timestamp vendorRecordCreatedTime;
    private Timestamp createTime;
    private String placeId;
    private String placeName;
    private String placeCity;
    private String placeCountry;
    private String placeZip;
    private double placeLatitude;
    private double placeLongitude;

    /**
     * 
     */
    public SocialMediaRecord() {
    }

    // /**
    // * @param vendorRecordId
    // * @param vendorRecordParentId
    // * @param message
    // * @param recordType
    // * @param vendorType
    // * @param vendorRecordCreatedTime
    // */
    // public SocialMediaRecord(String vendorRecordId, String
    // vendorRecordParentId, String message, int recordType,
    // int vendorType, Timestamp vendorRecordCreatedTime) {
    // this.vendorRecordId = vendorRecordId;
    // this.vendorRecordParentId = vendorRecordParentId;
    // this.message = message;
    // this.recordType = recordType;
    // this.vendorType = vendorType;
    // this.vendorRecordCreatedTime = vendorRecordCreatedTime;
    // }

    /**
     * @return the id
     */
    public long getId() {
        return id;
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
     * @return the vendorRecordCreatedTime
     */
    public Timestamp getVendorRecordCreatedTime() {
        return vendorRecordCreatedTime;
    }

    /**
     * @param vendorRecordCreatedTime
     *            the vendorRecordCreatedTime to set
     */
    public void setVendorRecordCreatedTime(Timestamp vendorRecordCreatedTime) {
        this.vendorRecordCreatedTime = vendorRecordCreatedTime;
    }

    /**
     * @return the placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * @param placeId
     *            the placeId to set
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    /**
     * @return the placeName
     */
    public String getPlaceName() {
        return placeName;
    }

    /**
     * @param placeName
     *            the placeName to set
     */
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    /**
     * @return the placeCity
     */
    public String getPlaceCity() {
        return placeCity;
    }

    /**
     * @param placeCity
     *            the placeCity to set
     */
    public void setPlaceCity(String placeCity) {
        this.placeCity = placeCity;
    }

    /**
     * @return the placeCountry
     */
    public String getPlaceCountry() {
        return placeCountry;
    }

    /**
     * @param placeCountry
     *            the placeCountry to set
     */
    public void setPlaceCountry(String placeCountry) {
        this.placeCountry = placeCountry;
    }

    /**
     * @return the placeZip
     */
    public String getPlaceZip() {
        return placeZip;
    }

    /**
     * @param placeZip
     *            the placeZip to set
     */
    public void setPlaceZip(String placeZip) {
        this.placeZip = placeZip;
    }

    /**
     * @return the placeLatitude
     */
    public double getPlaceLatitude() {
        return placeLatitude;
    }

    /**
     * @param placeLatitude
     *            the placeLatitude to set
     */
    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    /**
     * @return the placeLongitude
     */
    public double getPlaceLongitude() {
        return placeLongitude;
    }

    /**
     * @param placeLongitude
     *            the placeLongitude to set
     */
    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SocialMediaRecord [id=" + id + ", vendorRecordId=" + vendorRecordId + ", vendorRecordParentId="
                + vendorRecordParentId + ", message=" + message + ", recordType=" + recordType + ", vendorType="
                + vendorType + ", vendorRecordCreatedTime=" + vendorRecordCreatedTime + ", createTime=" + createTime
                + ", placeId=" + placeId + ", placeName=" + placeName + ", placeCity=" + placeCity + ", placeCountry="
                + placeCountry + ", placeZip=" + placeZip + ", placeLatitude=" + placeLatitude + ", placeLongitude="
                + placeLongitude + "]";
    }

}
