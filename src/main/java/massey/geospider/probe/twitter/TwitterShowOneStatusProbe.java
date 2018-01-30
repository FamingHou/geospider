/**
 * 
 */
package massey.geospider.probe.twitter;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import massey.geospider.api.http.HttpHelper;
import massey.geospider.boot.GeoCmdLine;
import massey.geospider.conf.PropReader;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.twitter.TwitterOneStatusResponse;
import massey.geospider.message.response.twitter.TwitterSearchResponse;
import massey.geospider.message.twitter.TwitterStatus;
import massey.geospider.util.JSONHelper;
import massey.geospider.util.StringHelper;

/**
 * This class is responsible for requesting, parsing and persistence for
 * fetching one Twitter status. It is important to know that this class will
 * fetch one and only one status, no parent status will be fetched. If you want
 * to fetch its parent status recursively, the class
 * {@link TwitterShowOneStatusRecursiveProbe} should be used.
 * 
 * <pre>
 * Request: https://api.twitter.com/1.1/statuses/show.json?id=957809075869372417&tweet_mode=extended
 * 
 * Response:
 * {
    "created_at": "Mon Jan 29 02:54:13 +0000 2018",
    "id": 957809075869372417,
    "id_str": "957809075869372417",
    "full_text": "#GeoEngineering\n.@ENENews 11/21/16\n➖➖➖\n#Fukushima Major M7.4R quake causes Emergency Evacuation [VIDEO]\n\nhttps://t.co/S3lwM5Hz6B",
    "truncated": false,
    "display_text_range": [
        0,
        128
    ],
    "entities": {
        "hashtags": [
            {
                "text": "GeoEngineering",
                "indices": [
                    0,
                    15
                ]
            },
            {
                "text": "Fukushima",
                "indices": [
                    39,
                    49
                ]
            }
        ],
        "symbols": [],
        "user_mentions": [
            {
                "screen_name": "ENENews",
                "name": "Energy News",
                "id": 268271491,
                "id_str": "268271491",
                "indices": [
                    17,
                    25
                ]
            }
        ],
        "urls": [
            {
                "url": "https://t.co/S3lwM5Hz6B",
                "expanded_url": "https://www.pscp.tv/w/1yoKMDampVRxQ",
                "display_url": "pscp.tv/w/1yoKMDampVRxQ",
                "indices": [
                    105,
                    128
                ]
            }
        ]
    },
    "source": "<a href=\"https://about.twitter.com/products/tweetdeck\" rel=\"nofollow\">TweetDeck</a>",
    "in_reply_to_status_id": null,
    "in_reply_to_status_id_str": null,
    "in_reply_to_user_id": null,
    "in_reply_to_user_id_str": null,
    "in_reply_to_screen_name": null,
    "user": {
        "id": 3130914869,
        "id_str": "3130914869",
        "name": "#SixthMassExtinction",
        "screen_name": "LivingOnChi",
        "location": "Massachusetts, USA",
        "description": "#Fukushima and #GeoEngineering are ACCELERATING #ClimateChange and the extinction of humanity Steal my content and create new tweets.  Spread awareness!",
        "url": null,
        "entities": {
            "description": {
                "urls": []
            }
        },
        "protected": false,
        "followers_count": 14989,
        "friends_count": 30,
        "listed_count": 321,
        "created_at": "Tue Mar 31 23:13:26 +0000 2015",
        "favourites_count": 61084,
        "utc_offset": -18000,
        "time_zone": "Eastern Time (US & Canada)",
        "geo_enabled": false,
        "verified": false,
        "statuses_count": 319557,
        "lang": "en",
        "contributors_enabled": false,
        "is_translator": false,
        "is_translation_enabled": false,
        "profile_background_color": "C6E2EE",
        "profile_background_image_url": "http://abs.twimg.com/images/themes/theme2/bg.gif",
        "profile_background_image_url_https": "https://abs.twimg.com/images/themes/theme2/bg.gif",
        "profile_background_tile": false,
        "profile_image_url": "http://pbs.twimg.com/profile_images/896112045204131840/AzzTG4oZ_normal.jpg",
        "profile_image_url_https": "https://pbs.twimg.com/profile_images/896112045204131840/AzzTG4oZ_normal.jpg",
        "profile_banner_url": "https://pbs.twimg.com/profile_banners/3130914869/1478971276",
        "profile_link_color": "1F98C7",
        "profile_sidebar_border_color": "C6E2EE",
        "profile_sidebar_fill_color": "DAECF4",
        "profile_text_color": "663B12",
        "profile_use_background_image": true,
        "has_extended_profile": false,
        "default_profile": false,
        "default_profile_image": false,
        "following": null,
        "follow_request_sent": null,
        "notifications": null,
        "translator_type": "none"
    },
    "geo": null,
    "coordinates": null,
    "place": null,
    "contributors": null,
    "is_quote_status": false,
    "retweet_count": 1,
    "favorite_count": 0,
    "favorited": false,
    "retweeted": false,
    "possibly_sensitive": false,
    "possibly_sensitive_appealable": false,
    "lang": "en"
}
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class TwitterShowOneStatusProbe extends TwitterAbstractProbe {

    private static final Logger log = Logger.getLogger(TwitterShowOneStatusProbe.class);

    /** id_str */
    protected String id;

    /**
     * 
     */
    public TwitterShowOneStatusProbe(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.twitter.TwitterAbstractProbe#onCollectEnd(massey.
     * geospider.boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info(new StringBuilder().append("status ").append(id).append(" was fetched successfully."));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doRequest(massey.geospider.boot.
     * GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected GeoResponse doRequest(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.debug("TwitterShowOneStatusProbe#doRequest()");
        TwitterSearchResponse twSearchRsp = (TwitterSearchResponse) inputGeoResponse;
        String urlString = buildRequestURL(geoCmdLine, twSearchRsp);
        if (urlString == null || urlString.trim().equalsIgnoreCase("")) {
            log.info("url of searching Twitter is null or an empty string");
            return null;
        } else {
            // do http request
            String responseString = HttpHelper.doGetAsync4Twitter(urlString);
            return createTwitterShowResponse(responseString);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doProcessResponse(massey.geospider.
     * boot.GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doProcessResponse(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        TwitterOneStatusResponse response = (TwitterOneStatusResponse) inputGeoResponse;
        TwitterStatus twStatus = response.getTwStatus();
        boolean hasKeyword = StringHelper.hasKeyword(twStatus.getText(), geoCmdLine.getKeywordOptionValue());
        boolean hasGeo = super.hasGeoPlace(twStatus);
        doPersistenceOne(geoCmdLine, twStatus, hasKeyword, hasGeo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.AbstractProbe#doPostCollect(massey.geospider.boot.
     * GeoCmdLine, massey.geospider.message.response.GeoResponse)
     */
    @Override
    protected void doPostCollect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        log.info("This class will only fetch the details of one status, do nothing in the post method.");
    }

    /**
     * Creates the request url for show.json API of Twitter.
     * 
     * <pre>
     * The URL is: <BR>
     * https://api.twitter.com/1.1/statuses/show.json?id=957809075869372417&tweet_mode=extended
     * </pre>
     * 
     * @param geoCmdLine
     *            GeoCmdLine
     * @param twSearchRsp
     *            TwitterSearchResponse object.
     * @return if twSearchRsp is null, URL is built from geoCmdLine; otherwise,
     *         URL is from twSearchRsp
     */
    private String buildRequestURL(final GeoCmdLine geoCmdLine, final TwitterSearchResponse twSearchRsp) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PropReader.get(TW_DOMAIN_NAME_PROP_NAME)).append(SEPARATOR);
            sb.append(PropReader.get(TW_VERSION_PROP_NAME)).append(SEPARATOR);
            sb.append("statuses/show.json");
            // use URIBuilder to solve URISyntax issues
            URIBuilder uriBuilder = new URIBuilder(sb.toString());
            uriBuilder.addParameter("id", id);
            uriBuilder.addParameter("tweet_mode", "extended");
            log.info(uriBuilder.toString());
            return uriBuilder.toString();
        } catch (URISyntaxException e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Parses response JSON String and creates a TwitterOneStatusResponse object
     * 
     * @param responseString
     *            the HTTP response JSON String
     * @return an object of class type TwitterOneStatusResponse
     */
    private TwitterOneStatusResponse createTwitterShowResponse(String responseString) {
        JSONObject jsonObj = JSONHelper.createAJSONObject(responseString);
        TwitterStatus status = parseOneStatus(jsonObj);
        return new TwitterOneStatusResponse(status);
    }

}
