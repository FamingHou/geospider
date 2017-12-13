/**
 * 
 */
package massey.geospider.persistence.dao;

import java.util.List;

import massey.geospider.persistence.dto.StatsPage;

/**
 * 
 * This interface defines the standard operations to be performed on a object of
 * class type StatsPage.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public interface StatsPageDAO {

    /**
     * Inserts one object of class type StatsPage into database
     * 
     * @param sPage
     *            an object of StatsPage
     */
    public void insertOne(StatsPage sPage);

    /**
     * Inserts a list of objects of class type StatsPage into database
     * 
     * @param sPageList
     *            a list of objects of class type StatsPage
     */
    public void insertList(List<StatsPage> sPageList);

}
