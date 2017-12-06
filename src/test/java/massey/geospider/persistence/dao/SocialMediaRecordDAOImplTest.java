/**
 * 
 */
package massey.geospider.persistence.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import junit.framework.TestCase;
import massey.geospider.persistence.dto.SocialMediaRecord;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class SocialMediaRecordDAOImplTest extends TestCase {

    private static SqlSessionFactory factory = null;

    /**
     * @param name
     */
    public SocialMediaRecordDAOImplTest(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsertOne() throws Exception {

        String resource = "mybatis/mybatis-config.xml";
        Reader reader = null;
        SqlSession session = null;

        reader = Resources.getResourceAsReader(resource);

        factory = new SqlSessionFactoryBuilder().build(reader);

        reader.close();

        // Create a new object
        // SocialMediaRecord smRecord = new SocialMediaRecord("333", "444",
        // "test message 333 444", 1, 2);
        // create a new object with vendor_record_created_time
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("01/01/2007");
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        SocialMediaRecord smRecord = new SocialMediaRecord();
        smRecord.setVendorRecordId("111");
        smRecord.setVendorRecordParentId("222");
        smRecord.setMessage("test message with vendor_record_created_time");
        smRecord.setRecordType(1);
        smRecord.setVendorType(2);
        smRecord.setPlaceId("108148499213006");
        smRecord.setPlaceName("Oaxaca City, Mexico");
        smRecord.setPlaceCity("Oaxaca de Juárez");
        smRecord.setPlaceCountry("Mexico");
        smRecord.setPlaceZip("68000");
        smRecord.setPlaceLatitude(17.05);
        smRecord.setPlaceLongitude(-96.7167);
        smRecord.setVendorRecordCreatedTime(ts);

        try {
            session = factory.openSession();
            session.insert("SocialMediaRecordMapper.insertOne", smRecord);
            session.commit();
            System.out.println("All done!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (session != null) {
                session.close();
            }
        }
    }
}
