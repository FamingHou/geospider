/**
 * 
 */
package massey.geospider.persistence.dao;

import java.io.IOException;
import java.io.Reader;

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

    public void testInsertOne() throws IOException {

        String resource = "mybatis/mybatis-config.xml";
        Reader reader = null;
        SqlSession session = null;

        reader = Resources.getResourceAsReader(resource);

        factory = new SqlSessionFactoryBuilder().build(reader);

        reader.close();

        // Create a new object
        SocialMediaRecord smRecord = new SocialMediaRecord("333", "444", "test message 333 444", 1, 2);

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
