/**
 * 
 */
package massey.geospider.persistence.dao;

import java.util.List;

import massey.geospider.persistence.dto.SocialMediaRecord;

/**
 * 
 * This interface defines the standard operations to be performed on a object of
 * class type SocialMediaRecord.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public interface SocialMediaRecordDAO {

    /**
     * Inserts one object of class type SocialMediaRecord into database
     * 
     * @param smRecord
     *            an object of SocialMediaRecord
     */
    public void insert(SocialMediaRecord smRecord);

    /**
     * Inserts a list of objects of class type SocialMediaRecord into database
     * 
     * @param smRecordList
     *            a list of objects of class type SocialMediaRecord
     */
    public void insert(List<SocialMediaRecord> smRecordList);

}
