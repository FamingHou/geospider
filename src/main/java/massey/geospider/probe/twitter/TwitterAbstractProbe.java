/**
 * 
 */
package massey.geospider.probe.twitter;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.twitter.TwitterSearchResponse;
import massey.geospider.message.twitter.TwitterPlace;
import massey.geospider.message.twitter.TwitterSearchMetaData;
import massey.geospider.message.twitter.TwitterStatus;
import massey.geospider.persistence.dao.SocialMediaRecordDAO;
import massey.geospider.persistence.dao.SocialMediaRecordDAOImpl;
import massey.geospider.persistence.dto.SocialMediaRecord;
import massey.geospider.probe.AbstractProbe;
import massey.geospider.util.DateHelper;
import massey.geospider.util.JSONHelper;

/**
 * Common methods of TwitterProbe.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class TwitterAbstractProbe extends AbstractProbe {

    private static final Logger log = Logger.getLogger(TwitterAbstractProbe.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * massey.geospider.probe.Probe#collect(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.response.GeoResponse)
     */
    @Override
    public void collect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        boolean isProcessing = doPreCollect(geoCmdLine, inputGeoResponse);
        if (isProcessing) {
            TwitterSearchResponse twSearchRsp = (TwitterSearchResponse) doRequest(geoCmdLine, inputGeoResponse);
            // if the value of datas in response is empty, it also means that
            // there
            // is no need to doNextPageCollect
            if (twSearchRsp != null && !twSearchRsp.isDatasEmpty()) {
                doProcessResponse(geoCmdLine, twSearchRsp);
                doPostCollect(geoCmdLine, twSearchRsp);
                // only call doNextPageCollect when the response has
                // hasNextResults
                // in Twitter
                if (twSearchRsp.hasNextResults()) {
                    doNextPageCollect(geoCmdLine, twSearchRsp);
                } else {
                    log.info("twSearchRsp is not null, but it has no nextResults, call method onCollectEnd()");
                    onCollectEnd(geoCmdLine, twSearchRsp);
                }
            } else {
                log.info("twitterResponse is null, which means this is the last page or the request url is invalid;");
                log.info("or geoResponse has a empty data list.");
                log.info("The end of this searching, call method onCollectEnd()");
                onCollectEnd(geoCmdLine, twSearchRsp);
            }
        } else {
            log.info("stop processing.");
        }
    }

    /**
     * Checks whether the text of fbMessage has valid geoplace(s)
     * 
     * @param twStatus
     * @return true - if twStatus has valid geoplaces; false -otherwise
     */
    protected boolean hasGeoPlace(TwitterStatus twStatus) {
        return super.hasGeoPlace(twStatus.getText());
    }

    /**
     * A hook method when collecting is finished. In other word, it will be
     * invoked when the last page was fetched (next_results is null).
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     * @return
     */
    protected abstract void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse);

    /**
     * Converts JSONObject into a TwitterStatus Object
     * 
     * @param statusObj
     * @return
     */
    protected TwitterStatus parseOneStatus(JSONObject statusObj) {
        String id = JSONHelper.get(statusObj, "id_str");
        String text = JSONHelper.get(statusObj, "full_text");
        String createdAtStr = JSONHelper.get(statusObj, "created_at");
        String inReplyToStatusId = JSONHelper.get(statusObj, "in_reply_to_status_id_str");
        Timestamp createdAt = DateHelper.parse(createdAtStr, DATETIME_FORMAT_TW);
        // latitude & longitude
        double latitude = parseLatitude(statusObj);
        double longitude = parseLongitude(statusObj);
        TwitterPlace place = parsePlace(statusObj);
        TwitterStatus twStatus = new TwitterStatus();
        twStatus.setId(id);
        twStatus.setText(text);
        twStatus.setCreatedAt(createdAt);
        twStatus.setInReplyToStatusId(inReplyToStatusId);
        twStatus.setLatitude(latitude);
        twStatus.setLongitude(longitude);
        twStatus.setPlace(place);
        return twStatus;
    }

    /**
     * <pre>
            "coordinates": {
                "type": "Point",
                "coordinates": [
                    174.70057725,
                    -36.73333452
                ]
            },
     * </pre>
     * 
     * @param statusObj
     * @return -36.73333452
     */
    protected double parseLatitude(JSONObject statusObj) {
        double latitude = 0d;
        JSONObject coordinateOuterObj = JSONHelper.getJSONObj(statusObj, "coordinates");
        JSONArray coordinateInnerArray = JSONHelper.getJSONArray(coordinateOuterObj, "coordinates");
        try {
            if (coordinateInnerArray != null)
                latitude = coordinateInnerArray.getDouble(1);
        } catch (Exception e) {
            log.error(e, e);
        }
        return latitude;
    }

    /**
     * <pre>
            "coordinates": {
                "type": "Point",
                "coordinates": [
                    174.70057725,
                    -36.73333452
                ]
            },
     * </pre>
     * 
     * @param statusObj
     * @return 174.70057725
     */
    protected double parseLongitude(JSONObject statusObj) {
        double longitude = 0d;
        JSONObject coordinateOuterObj = JSONHelper.getJSONObj(statusObj, "coordinates");
        JSONArray coordinateInnerArray = JSONHelper.getJSONArray(coordinateOuterObj, "coordinates");
        try {
            if (coordinateInnerArray != null)
                longitude = coordinateInnerArray.getDouble(0);
        } catch (Exception e) {
            log.error(e, e);
        }
        return longitude;
    }

    /**
     * 
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
     * @param statusObj
     * @return an object of class TwitterPlace
     */
    protected TwitterPlace parsePlace(JSONObject statusObj) {
        JSONObject placeObj = JSONHelper.getJSONObj(statusObj, "place");
        String id = JSONHelper.get(placeObj, "id"); // "id"
        String name = JSONHelper.get(placeObj, "full_name"); // "full_name"
        String country = JSONHelper.get(placeObj, "country"); // "country"
        TwitterPlace place = new TwitterPlace();
        place.setId(id);
        place.setName(name);
        place.setCountry(country);
        return place;
    }

    /**
     * Parses JSON data array of response and creates an object of class type
     * TwitterSearchMetaData
     * 
     * <pre>
     *     "search_metadata": {
        "completed_in": 0.044,
        "max_id": 953403110239563776,
        "max_id_str": "953403110239563776",
        "next_results": "?max_id=953038141715226623&q=%22earthquakefrank%22&lang=en&count=2",
        "query": "%22earthquakefrank%22",
        "refresh_url": "?since_id=953403110239563776&q=%22earthquakefrank%22&lang=en",
        "count": 2,
        "since_id": 0,
        "since_id_str": "0"
    }
     * </pre>
     * 
     * @param jsonObj
     * @return TwitterSearchMetaData
     */
    protected TwitterSearchMetaData parseSearchMetaData(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("search_metadata"))
            return null;
        JSONObject metaObj = jsonObj.getJSONObject("search_metadata");
        String maxId = JSONHelper.get(metaObj, "max_id_str");
        String nextResults = JSONHelper.get(metaObj, "next_results");
        String refreshUrl = JSONHelper.get(metaObj, "refresh_url");
        String sinceId = JSONHelper.get(metaObj, "since_id_str");
        // TwitterSearchMetaData
        TwitterSearchMetaData meta = new TwitterSearchMetaData();
        meta.setMaxId(maxId);
        meta.setNextResults(nextResults);
        meta.setRefreshUrl(refreshUrl);
        meta.setSinceId(sinceId);
        return meta;
    }

    /**
     * Parses JSON data array of response and creates an array of class type
     * TwitterStatus
     * 
     * @param jsonObj
     * @return TwitterStatus[]
     */
    protected TwitterStatus[] parseStatuses(JSONObject jsonObj) {
        if (jsonObj == null || jsonObj.isNull("statuses"))
            return null;
        JSONArray dataArray = jsonObj.getJSONArray("statuses");
        int n = dataArray.length();
        TwitterStatus[] tweetArray = new TwitterStatus[n];
        for (int i = 0; i < n; ++i) {
            JSONObject statusObj = dataArray.getJSONObject(i);
            tweetArray[i] = parseOneStatus(statusObj);
        }
        return tweetArray;
    }

    /**
     * Inserts one TwitterStatus object into database.
     * 
     * @param geoCmdLine
     * @param twStatus
     */
    protected void doPersistenceOne(GeoCmdLine geoCmdLine, TwitterStatus twStatus) {
        doPersistenceOne(geoCmdLine, twStatus, true, true);
    }

    /**
     * Inserts one TwitterStatus object into database.
     * 
     * @param geoCmdLine
     * @param twStatus
     * @param hasKeyword
     * @param hasGeo
     */
    protected void doPersistenceOne(GeoCmdLine geoCmdLine, TwitterStatus twStatus, boolean hasKeyword, boolean hasGeo) {
        SocialMediaRecord smRecord = new SocialMediaRecord();
        smRecord.setKeyword(geoCmdLine.getKeywordOptionValue());
        smRecord.setVendorRecordId(twStatus.getId());
        smRecord.setMessage(twStatus.getText());
        smRecord.setVendorType(VENDOR_TYPE_TWITTER);
        smRecord.setVendorRecordCreatedTime(twStatus.getCreatedAt());
        smRecord.setPlaceLatitude(twStatus.getLatitude());
        smRecord.setPlaceLongitude(twStatus.getLongitude());
        if (twStatus.getInReplyToStatusId() != null) { // TwitterReply
            smRecord.setVendorRecordParentId(twStatus.getInReplyToStatusId());
            smRecord.setRecordType(RECORD_TYPE_REPLY);
        } else { // TwitterTweet
            smRecord.setRecordType(RECORD_TYPE_POST);
        }
        // Task_20180119_1
        TwitterPlace place = twStatus.getPlace();
        if (place != null) {
            smRecord.setPlaceId(place.getId());
            smRecord.setPlaceName(place.getName());
            smRecord.setPlaceCountry(place.getCountry());
        }
        smRecord.setHasKeyword(hasKeyword);
        smRecord.setHasGeo(hasGeo);
        SocialMediaRecordDAO smrDao = new SocialMediaRecordDAOImpl();
        smrDao.insertOne(smRecord);
    }
}
