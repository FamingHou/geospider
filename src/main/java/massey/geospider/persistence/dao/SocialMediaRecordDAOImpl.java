/**
 * 
 */
package massey.geospider.persistence.dao;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import massey.geospider.persistence.dto.SocialMediaRecord;
import massey.geospider.persistence.mybatis.MyBatisHelper;

/**
 * This is the default implementation of interface SocialMediaRecordDao using
 * MySQL
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class SocialMediaRecordDAOImpl implements SocialMediaRecordDAO {

    private static final Logger log = Logger.getLogger(SocialMediaRecordDAOImpl.class);

    /**
     * 
     */
    public SocialMediaRecordDAOImpl() {
    }

    @Override
    public void insertOne(SocialMediaRecord smRecord) {
        String statement = "SocialMediaRecordMapper.insertOne";
        try {
            MyBatisHelper.getSingle().insertOne(statement, smRecord);
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    @Override
    public void insertList(List<SocialMediaRecord> smRecordList) {

    }

}
