/**
 * 
 */
package massey.geospider.message.facebook;

/**
 * The class FacebookLocation represents the class <a href=
 * "https://developers.facebook.com/docs/graph-api/reference/location/">Location</a>
 * which is defined on Facebook platform
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookLocation {

    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private String zip;

    /**
     * 
     */
    public FacebookLocation() {
    }

    /**
     * @param city
     * @param country
     * @param latitude
     * @param longitude
     * @param zip
     */
    public FacebookLocation(String city, String country, double latitude, double longitude, String zip) {
        super();
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zip = zip;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public void setCountry(String country) {
        this.country = country;
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

    /**
     * @return the zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * @param zip
     *            the zip to set
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookLocation [city=" + city + ", country=" + country + ", latitude=" + latitude + ", longitude="
                + longitude + ", zip=" + zip + "]";
    }

}
