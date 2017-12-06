/**
 * 
 */
package massey.geospider.message.facebook;

/**
 * The class FacebookPlace represents a class <a
 * href=https://developers.facebook.com/docs/graph-api/reference/place>Place</a>
 * which is defined on Facebook platform
 * 
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPlace {

    private String id;
    private String name;
    private FacebookLocation location;

    /**
     * 
     */
    public FacebookPlace() {
    }

    /**
     * @param id
     * @param name
     * @param location
     */
    public FacebookPlace(String id, String name, FacebookLocation location) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the location
     */
    public FacebookLocation getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(FacebookLocation location) {
        this.location = location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FacebookPlace [id=" + id + ", name=" + name + ", location=" + location + "]";
    }

}
