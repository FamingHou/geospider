/**
 * 
 */
package massey.geospider.message.response.flickr;

import massey.geospider.message.flickr.FlickrPhoto;

/**
 * <pre>
 * {
    "photos": {
        "page": 3,
        "pages": 11033,
        "perpage": 50,
        "total": "551612",
        "photo": [
            {"id": "39314113544",
             "owner": "98403995@N08",
             ...
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
public class FlickrSearchResponse extends FlickrAbstractResponse {

    private FlickrPhoto[] photoArray;

    private int page; // current page number
    private int pages; // total number of pages
    private int total; // total photos

    /**
     * 
     */
    public FlickrSearchResponse(FlickrPhoto[] photoArray, int page, int pages, int total) {
        this.photoArray = photoArray;
        this.page = page;
        this.pages = pages;
        this.total = total;
    }

    /**
     * If photoArray is null or empty, return true, otherwise return false.
     */
    @Override
    public boolean isDataEmpty() {
        if (photoArray == null || photoArray.length == 0)
            return true;
        return false;
    }

    /**
     * If page >= pages, return false; otherwise return true
     */
    @Override
    public boolean hasNextPage() {
        if (page >= pages)
            return false;
        else
            return true;
    }

    /**
     * @return the photoArray
     */
    public FlickrPhoto[] getPhotoArray() {
        return photoArray;
    }

    /**
     * @param photoArray
     *            the photoArray to set
     */
    public void setPhotoArray(FlickrPhoto[] photoArray) {
        this.photoArray = photoArray;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * @param pages
     *            the pages to set
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

}
