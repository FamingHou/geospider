/**
 * 
 */
package massey.geospider.message.twitter;

/**
 * <pre>
 * "place": {
                "id": "01215ca860c04522",
                "url": "https://api.twitter.com/1.1/geo/id/01215ca860c04522.json",
                "place_type": "neighborhood",
                "name": "Albany",
                "full_name": "Albany, Auckland",
                "country_code": "NZ",
                "country": "New Zealand",
                "contained_within": [],
                "bounding_box": {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [
                                174.678726,
                                -36.751624
                            ],
                            [
                                174.719655,
                                -36.751624
                            ],
                            [
                                174.719655,
                                -36.717618
                            ],
                            [
                                174.678726,
                                -36.717618
                            ]
                        ]
                    ]
                },
                "attributes": {}
            },
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterPlace {

    private String id;
    private String name;
    private String country;

    /**
     * 
     */
    public TwitterPlace() {
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TwitterPlace [id=" + id + ", name=" + name + ", country=" + country + "]";
    }

}
