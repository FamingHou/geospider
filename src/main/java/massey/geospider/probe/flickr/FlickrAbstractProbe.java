/**
 * 
 */
package massey.geospider.probe.flickr;

import org.apache.log4j.Logger;

import massey.geospider.boot.GeoCmdLine;
import massey.geospider.message.flickr.FlickrComment;
import massey.geospider.message.flickr.FlickrPhoto;
import massey.geospider.message.response.GeoResponse;
import massey.geospider.message.response.flickr.FlickrAbstractResponse;
import massey.geospider.persistence.dao.SocialMediaRecordDAO;
import massey.geospider.persistence.dao.SocialMediaRecordDAOImpl;
import massey.geospider.persistence.dto.SocialMediaRecord;
import massey.geospider.probe.AbstractProbe;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public abstract class FlickrAbstractProbe extends AbstractProbe {

    private static final Logger log = Logger.getLogger(FlickrAbstractProbe.class);

    /*
     * The template processor for Flickr
     * 
     * @see
     * massey.geospider.probe.Probe#collect(massey.geospider.boot.GeoCmdLine,
     * massey.geospider.message.response.GeoResponse)
     */
    @Override
    public void collect(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse) {
        boolean isProcessing = doPreCollect(geoCmdLine, inputGeoResponse);
        if (isProcessing) {
            FlickrAbstractResponse flickrRsp = (FlickrAbstractResponse) doRequest(geoCmdLine, inputGeoResponse);
            // if the value of data in response is empty, it also means
            // there is no need to doNextPageCollect
            if (flickrRsp != null && !flickrRsp.isDataEmpty()) {
                doProcessResponse(geoCmdLine, flickrRsp);
                doPostCollect(geoCmdLine, flickrRsp);
                // only call doNextPageCollect when the returned value of method
                // hasNextPage of FlickrAbstractResponse is true in Flickr
                if (flickrRsp.hasNextPage()) {
                    doNextPageCollect(geoCmdLine, flickrRsp);
                } else {
                    log.info("flickrRsp is not null, but it has no hasNextPage, call method onCollectEnd()");
                    onCollectEnd(geoCmdLine, flickrRsp);
                }
            } else {
                log.info("flickrResponse is null, which means this is the last page or the request url is invalid;");
                log.info("or geoResponse has a empty data list.");
                log.info("The end of this searching, call method onCollectEnd()");
                onCollectEnd(geoCmdLine, flickrRsp);
            }
        } else {
            log.info("stop processing Flickr.");
        }
    }

    /**
     * A hook method when collecting is finished. In other word, it will be
     * invoked when the last page was fetched.
     * 
     * @param geoCmdLine
     *            the values of the arguments which were filled by user.
     * @param inputGeoResponse
     *            an input GeoResponse object
     * @return
     */
    protected abstract void onCollectEnd(GeoCmdLine geoCmdLine, GeoResponse inputGeoResponse);

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.probe.AbstractProbe#getVendorType()
     */
    @Override
    protected int getVendorType() {
        return VENDOR_TYPE_FLICKR;
    }

    /**
     * Inserts one FlickrPhoto object into database.
     * 
     * @param geoCmdLine
     * @param flickrPhoto
     * @param recordType
     * @param hasKeyword
     * @param hasGeo
     */
    protected void doPersistenceOne(GeoCmdLine geoCmdLine, FlickrPhoto flickrPhoto, int recordType, boolean hasKeyword,
            boolean hasGeo) {
        SocialMediaRecord smRecord = new SocialMediaRecord();
        smRecord.setKeyword(geoCmdLine.getKeywordOptionValue());
        smRecord.setVendorRecordId(flickrPhoto.getId());
        smRecord.setMessage(flickrPhoto.getText());
        smRecord.setTags(flickrPhoto.getTags()); // tags
        smRecord.setVendorType(getVendorType());
        smRecord.setVendorRecordCreatedTime(flickrPhoto.getCreatedAt());
        smRecord.setPlaceLatitude(flickrPhoto.getLatitude());
        smRecord.setPlaceLongitude(flickrPhoto.getLongitude());
        smRecord.setRecordType(recordType);
        smRecord.setHasKeyword(hasKeyword);
        smRecord.setHasGeo(hasGeo);
        SocialMediaRecordDAO smrDao = new SocialMediaRecordDAOImpl();
        smrDao.insertOne(smRecord);
    }

    /**
     * Inserts one FlickrComment object into database.
     * 
     * @param geoCmdLine
     * @param flickrComment
     * @param recordType
     * @param hasKeyword
     * @param hasGeo
     */
    protected void doPersistenceOne(GeoCmdLine geoCmdLine, FlickrComment flickrComment, int recordType,
            boolean hasKeyword, boolean hasGeo) {
        SocialMediaRecord smRecord = new SocialMediaRecord();
        smRecord.setKeyword(geoCmdLine.getKeywordOptionValue());
        smRecord.setVendorRecordId(flickrComment.getId());
        smRecord.setMessage(flickrComment.getText());
        smRecord.setVendorType(getVendorType());
        smRecord.setVendorRecordCreatedTime(flickrComment.getCreatedAt());
        smRecord.setRecordType(recordType);
        smRecord.setHasKeyword(hasKeyword);
        smRecord.setHasGeo(hasGeo);
        SocialMediaRecordDAO smrDao = new SocialMediaRecordDAOImpl();
        smrDao.insertOne(smRecord);
    }
}
