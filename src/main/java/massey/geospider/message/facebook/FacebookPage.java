/**
 * 
 */
package massey.geospider.message.facebook;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FacebookPage extends FacebookMessage {

    private String name;

    /**
     * 
     * @param id
     * @param parentId
     * @param name
     */
    public FacebookPage(String id, String parentId, String name) {
        super(id, parentId);
        this.name = name;
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
