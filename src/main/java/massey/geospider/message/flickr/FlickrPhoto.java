/**
 * 
 */
package massey.geospider.message.flickr;

import java.sql.Timestamp;

/**
 * <pre>
 * {
        "id": "39202382945",
        "owner": "63848257@N06",
        "secret": "ffe844df3f",
        "server": "4674",
        "farm": 5,
        "title": "Albania day trip - Butrint, Albania - UNESCO World Heritage Centre",
        "ispublic": 1,
        "isfriend": 0,
        "isfamily": 0,
        "description": {
            "_content": "Buthrotum (Albanian: Butrint; Latin: Buthrōtum; from Ancient Greek: Βουθρωτόν, translit. Bouthrōtón) was an ancient Greek and later Roman city and bishopric in Epirus. Inhabited since prehistoric times, Buthrotum was a city of the Greek tribe of the Chaonians, later a Roman colony and a bishopric. It entered into decline in Late Antiquity, before being abandoned during the Middle Ages after a major earthquake flooded most of the city. In modern times it is an archeological site in Vlorë County, Albania, some 14 kilometres south of Sarandë and close to the Greek border. It is located on a hill overlooking the Vivari Channel and is part of the Butrint National Park. Today Bouthrotum is a Latin Catholic titular see."
        },
        "dateupload": "1517851935",
        "lastupdate": "1517874560",
        "datetaken": "2017-09-10 08:21:10",
        "datetakengranularity": "0",
        "datetakenunknown": "0",
        "tags": "butrint albania unesco world heritage centre buthrotum greek city roman",
        "latitude": "39.746002",
        "longitude": "20.020437",
        "accuracy": "14",
        "context": 0,
        "place_id": "OOHjzhVVVLNZQl4",
        "woeid": "469417",
        "geo_is_family": 0,
        "geo_is_friend": 0,
        "geo_is_contact": 0,
        "geo_is_public": 1
    }
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FlickrPhoto extends FlickrMessage {

    private String id; // id
    private String text; // description/_content
    private String tags; // tags
    private Timestamp createdAt; // dateupload
    private double latitude; // latitude
    private double longitude; // longitude

    /**
     * 
     */
    public FlickrPhoto() {
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
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
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

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     *            the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     *            the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FlickrPhoto [id=" + id + ", text=" + text + ", tags=" + tags + ", createdAt=" + createdAt
                + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }

}
