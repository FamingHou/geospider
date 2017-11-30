package massey.geospider.global;

/**
 * All global variables are defined in this class
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public interface GeoConstants {

    /** the option name of keyword for searching usage */
    public static final String KEYWORD_OPTION = "keyword";

    /** the name of the social media vendors */
    public static final String FACEBOOK_OPTION = "facebook";
    public static final String TWITTER_OPTION = "twitter";
    public static final String REDDIT_OPTION = "reddit";
    public static final String INSTAGRAM_OPTION = "instagram";
    public static final String YOUTUBE_OPTION = "youtube";
    public static final String FLICKR_OPTION = "flickr";

    /** Facebook constants */
    public static final String FB_DOMAIN_NAME_PROP_NAME = "FB_DOMAIN_NAME";
    public static final String FB_VERSION_PROP_NAME = "FB_VERSION";
    public static final String FB_ACCESS_TOKEN_PROP_NAME = "FB_ACCESS_TOKEN";
    public static final String SEPARATOR = "/";

    /**
     * the number of records for each Facebook query, which is shared by page,
     * post and comment for the time being.
     * 
     * According to the response message of Facebook API, the 'limit' parameter
     * should not exceed 100.
     * 
     * <pre>
     * "error": {
    "message": "(#100) The 'limit' parameter should not exceed 100",
    "type": "OAuthException",
    "code": 100,
    "fbtrace_id": "HugxxAHiBVf"
    }
     * </pre>
     */
    public static final String FB_PAGE_LIMIT_PROP_NAME = "FB_PAGE_LIMIT";

    public static final int RECORD_TYPE_PAGE = 1;
    public static final int RECORD_TYPE_POST = 2;
    public static final int RECORD_TYPE_COMMENT = 3;

    public static final int VENDOR_TYPE_FACEBOOK = 1;
    public static final int VENDOR_TYPE_TWITTER = 2;
    public static final int VENDOR_TYPE_REDDIT = 3;
    public static final int VENDOR_TYPE_INSTAGRAM = 4;
    public static final int VENDOR_TYPE_YOUTUBE = 5;
    public static final int VENDOR_TYPE_FLICKR = 6;
    
}