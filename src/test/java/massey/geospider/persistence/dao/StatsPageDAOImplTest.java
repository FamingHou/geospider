/**
 * 
 */
package massey.geospider.persistence.dao;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import junit.framework.TestCase;
import massey.geospider.persistence.dto.StatsPage;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class StatsPageDAOImplTest extends TestCase {

    private static SqlSessionFactory factory = null;

    /**
     * @param name
     */
    public StatsPageDAOImplTest(String name) {
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

        StatsPage statsPage = new StatsPage();
        statsPage.setPageId("111");
        statsPage.setPageName("test page name 111");
        statsPage.setSizeOfPostsInTotal(200);
        statsPage.setSizeOfPostsHasKeyword(20);
        statsPage.setSizeOfPostsHasKeywordAndGeo(4);

        statsPage.setSizeOfCommentsInTotal(300);
        statsPage.setSizeOfCommentsHasKeyword(60);
        statsPage.setSizeOfCommentsHasKeywordAndGeo(3);

        statsPage.setSizeOfRepliesInTotal(40);
        statsPage.setSizeOfRepliesHasKeyword(10);
        statsPage.setSizeOfRepliesHasKeywordAndGeo(2);

        try {
            session = factory.openSession();
            session.insert("StatsPageMapper.insertOne", statsPage);
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
