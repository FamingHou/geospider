/**
 * 
 */
package massey.geospider.message.twitter;

import java.sql.Timestamp;

/**
 * {@link TwitterStatus} can be a {@link TwitterTweet} or a {@link TwitterReply}
 * 
 * <pre>
 * {
            "created_at": "Tue Jan 16 23:06:29 +0000 2018",
            "id": 953403110239563776,
            "id_str": "953403110239563776",
            "full_text": "Reply 5 earthquakefrank",
            "truncated": false,
            "display_text_range": [
                0,
                23
            ],
            "metadata": {
                "iso_language_code": "en",
                "result_type": "recent"
            },
            "source": "<a href=\"http://twitter.com/download/iphone\" rel=\"nofollow\">Twitter for iPhone</a>",
            "in_reply_to_status_id": 953402601923424256,
            "in_reply_to_status_id_str": "953402601923424256",
            "in_reply_to_user_id": 46051557,
            "in_reply_to_user_id_str": "46051557",
            "in_reply_to_screen_name": "faminghou",
            "user": {
                "id": 46051557,
                "id_str": "46051557",
                "name": "Frank Hou",
                "screen_name": "faminghou",
                "location": "",
                "description": "",
                "url": null,
                "entities": {
                    "description": {
                        "urls": []
                    }
                },
                "protected": false,
                "followers_count": 6,
                "friends_count": 4,
                "listed_count": 0,
                "created_at": "Wed Jun 10 06:14:08 +0000 2009",
                "favourites_count": 1,
                "utc_offset": 46800,
                "time_zone": "Auckland",
                "geo_enabled": true,
                "verified": false,
                "statuses_count": 11,
                "lang": "en-gb",
                "contributors_enabled": false,
                "is_translator": false,
                "is_translation_enabled": false,
                "profile_background_color": "C0DEED",
                "profile_background_image_url": "http://abs.twimg.com/images/themes/theme1/bg.png",
                "profile_background_image_url_https": "https://abs.twimg.com/images/themes/theme1/bg.png",
                "profile_background_tile": false,
                "profile_image_url": "http://abs.twimg.com/sticky/default_profile_images/default_profile_normal.png",
                "profile_image_url_https": "https://abs.twimg.com/sticky/default_profile_images/default_profile_normal.png",
                "profile_link_color": "1DA1F2",
                "profile_sidebar_border_color": "C0DEED",
                "profile_sidebar_fill_color": "DDEEF6",
                "profile_text_color": "333333",
                "profile_use_background_image": true,
                "has_extended_profile": false,
                "default_profile": true,
                "default_profile_image": true,
                "following": null,
                "follow_request_sent": null,
                "notifications": null,
                "translator_type": "none"
            },
            "geo": {
                "type": "Point",
                "coordinates": [
                    -36.73333452,
                    174.70057725
                ]
            },
            "coordinates": {
                "type": "Point",
                "coordinates": [
                    174.70057725,
                    -36.73333452
                ]
            },
            "place": {
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
            "contributors": null,
            "is_quote_status": false,
            "retweet_count": 0,
            "favorite_count": 0,
            "favorited": false,
            "retweeted": false,
            "lang": "en"
        },
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterStatus extends TwitterMessage {

    private String id; // id_str
    private String text; // full_text
    private Timestamp createdAt; // created_at
    private String inReplyToStatusId; // in_reply_to_status_id

    private double latitude;
    private double longitude;

    private TwitterPlace place;
    
    /**
     * 
     */
    public TwitterStatus() {
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

    /**
     * @return the inReplyToStatusId
     */
    public String getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    /**
     * @param inReplyToStatusId
     *            the inReplyToStatusId to set
     */
    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
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
     * @return the place
     */
    public TwitterPlace getPlace() {
        return place;
    }

    /**
     * @param place the place to set
     */
    public void setPlace(TwitterPlace place) {
        this.place = place;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TwitterStatus [id=" + id + ", text=" + text + ", createdAt=" + createdAt + ", inReplyToStatusId="
                + inReplyToStatusId + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }

}
