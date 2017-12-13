/**
 * 
 */
package massey.geospider.persistence.dao;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import massey.geospider.persistence.dto.StatsPage;
import massey.geospider.persistence.mybatis.MyBatisHelper;

/**
 * This is the default implementation of interface StatsPageDAO using
 * MySQL
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class StatsPageDAOImpl implements StatsPageDAO {

    private static final Logger log = Logger.getLogger(StatsPageDAOImpl.class);

    /**
     * 
     */
    public StatsPageDAOImpl() {
    }

    @Override
    public void insertOne(StatsPage sPage) {
        String statement = "StatsPageMapper.insertOne";
        try {
            MyBatisHelper.getSingle().insertOne(statement, sPage);
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    @Override
    public void insertList(List<StatsPage> sPageList) {
        //@TODO
    }

}
