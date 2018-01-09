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
    public static final String ISCONCURRENT_OPTION = "concurrent";

    /** Facebook constants */
    public static final String FB_DOMAIN_NAME_PROP_NAME = "FB_DOMAIN_NAME";
    public static final String FB_VERSION_PROP_NAME = "FB_VERSION";
    public static final String FB_ACCESS_TOKEN_PROP_NAME = "FB_ACCESS_TOKEN";
    public static final String SEPARATOR = "/";

    /**
     * According to the response message of Facebook API, the 'limit' parameter
     * should not exceed 100 when fetching posts in one page, there is no
     * limitation for pages/comments/replies.
     * 
     * <pre>
     * "error": {
    "message": "(#100) The 'limit' parameter should not exceed 100",
    "type": "OAuthException",
    "code": 100,
    "fbtrace_id": "HugxxAHiBVf"
    }
     * </pre>
     * 
     * <pre>
     * #The record values of page 'Vancouver Island Earthquake Watch' of table stats_page: 
    #'26', NULL, NULL, '212734475453950', 'Vancouver Island Earthquake Watch', '284', '46', '46', '13043', '557', '557', '1083', '94', '94', '2017-12-14 15:14:39'
    #
    #In the previous version, FB_PAGE_LIMIT property value is used for URL1, URL3 and URL4 besides URL2. 
    #It means that the system will call Facebook API 3 times for posts pagination, 131 times for comments pagination 
    #and 11 times for replies pagination.
    
    #Adding three new properties (FB_PAGE_LIMIT=20000,FB_POST_LIMIT=100,FB_COMMENT_LIMIT=20000,FB_REPLY_LIMIT=20000)  
    #in the properties file, and reduce the HTTP connections (from 145 times to only 4 times in the 'Vancouver Island Earthquake Watch' case).
    #URL1: Fetching 20000 pages with keyword 'earthquake'  
    #   search?pretty=0&q='earthquake'&type=page&limit=20000
    #URL2: Fetching 100 posts of the page with id = 743883005806524  
    #   743883005806524/posts?fields=id,message,created_time,place&limit=100
    #URL3: Fetching 20000 comments of the post with id = 743883005806524_743884095806415  
    #   743883005806524_743884095806415/comments?limit=20000
    #URL4: Fetching 20000 replies of the comment with id = 743883005806524_743884095806415  
    #   743884095806415_744208492440642/comments?limit=20000
     * </pre>
     */
    public static final String FB_PAGE_LIMIT_PROP_NAME = "FB_PAGE_LIMIT";
    public static final String FB_POST_LIMIT_PROP_NAME = "FB_POST_LIMIT";
    public static final String FB_COMMENT_LIMIT_PROP_NAME = "FB_COMMENT_LIMIT";
    public static final String FB_REPLY_LIMIT_PROP_NAME = "FB_REPLY_LIMIT";

    public static final int RECORD_TYPE_PAGE = 1;
    public static final int RECORD_TYPE_POST = 2;
    public static final int RECORD_TYPE_COMMENT = 3;
    public static final int RECORD_TYPE_REPLY = 4;

    public static final int VENDOR_TYPE_FACEBOOK = 1;
    public static final int VENDOR_TYPE_TWITTER = 2;
    public static final int VENDOR_TYPE_REDDIT = 3;
    public static final int VENDOR_TYPE_INSTAGRAM = 4;
    public static final int VENDOR_TYPE_YOUTUBE = 5;
    public static final int VENDOR_TYPE_FLICKR = 6;

    public static final String DATETIME_FORMAT_FB = "yyyy-MM-dd'T'HH:mm:ssZ";

}