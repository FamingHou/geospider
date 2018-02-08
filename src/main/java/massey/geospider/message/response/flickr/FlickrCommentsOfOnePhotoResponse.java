/**
 * 
 */
package massey.geospider.message.response.flickr;

import massey.geospider.message.flickr.FlickrComment;

/**
 * The response of fetching all comments of one photo.
 * 
 * request url:
 * 
 * <pre>
 * https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList&api_key=fd6841a5030427fb74060d876fe7d580&photo_id=4105054854&format=json&nojsoncallback=1
 * </pre>
 * 
 * response json:
 * 
 * <pre>
 * {
    "comments": {
        "photo_id": "4105054854",
        "comment": [
            {
                "id": "2149990-4105054854-72157622680956043",
                "author": "7359335@N02",
                "author_is_deleted": 0,
                "authorname": "Lee Sie",
                "iconserver": "8126",
                "iconfarm": 9,
                "datecreate": "1258260705",
                "permalink": "https://www.flickr.com/photos/stuckincustoms/4105054854/#comment72157622680956043",
                "path_alias": "lee_sie",
                "realname": "Lee Sie",
                "_content": "Wow this place looks amazing!"
            },
            ...
        ]
    },
    "stat": "ok"
}
 * </pre>
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class FlickrCommentsOfOnePhotoResponse extends FlickrAbstractResponse {

    private String photoId;
    private FlickrComment[] commentArray;

    /**
     * 
     * @param photoId
     * @param commentArray
     */
    public FlickrCommentsOfOnePhotoResponse(String photoId, FlickrComment[] commentArray) {
        this.commentArray = commentArray;
        this.photoId = photoId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.message.response.flickr.FlickrAbstractResponse#
     * hasNextPage()
     */
    @Override
    public boolean hasNextPage() {
        // there is only one page when fetching all comments of one photo on
        // Flickr
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see massey.geospider.message.response.GeoResponse#isDataEmpty()
     */
    @Override
    public boolean isDataEmpty() {
        if (commentArray == null || commentArray.length == 0)
            return true;
        return false;
    }

    /**
     * @return the photoId
     */
    public String getPhotoId() {
        return photoId;
    }

    /**
     * @return the commentArray
     */
    public FlickrComment[] getCommentArray() {
        return commentArray;
    }

    /**
     * @param commentArray
     *            the commentArray to set
     */
    public void setCommentArray(FlickrComment[] commentArray) {
        this.commentArray = commentArray;
    }

    /**
     * @param photoId
     *            the photoId to set
     */
    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

}
