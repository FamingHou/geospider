/**
 * 
 */
package massey.geospider.persistence.dao;

import java.io.Reader;
import java.sql.Timestamp;

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
        String resource = "mybatis/mybatis-config.xml";
        Reader reader = null;

        reader = Resources.getResourceAsReader(resource);

        factory = new SqlSessionFactoryBuilder().build(reader);

        reader.close();
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
        SqlSession session = null;
        StatsPage statsPage = new StatsPage();
        statsPage.setKeyword("earthquaketest");
        statsPage.setVendorType(1);
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

        // statsPage.setNeedRefresh(true);

        try {
            session = factory.openSession();
            int rows = session.insert("StatsPageMapper.insertOne", statsPage);
            session.commit();
            System.out.println(rows + " rows inserted.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void testDelete() throws Exception {
        SqlSession session = null;
        try {
            session = factory.openSession();
            int rows = session.delete("StatsPageMapper.deleteByPageId", "111");
            session.commit();
            System.out.println(rows + " rows deleted!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void testSelectOne() throws Exception {
        SqlSession session = null;
        try {
            session = factory.openSession();
            StatsPage statsPage = session.selectOne("StatsPageMapper.selectOneByPageId", "111");
            System.out.println(statsPage == null ? "null" : statsPage.toString());
            System.out.println("All done!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void testUpdateOne() throws Exception {
        SqlSession session = null;
        StatsPage statsPage = new StatsPage();
        statsPage.setPageId("111");

        statsPage.setSizeOfPostsInTotal(9);
        statsPage.setSizeOfPostsHasKeyword(8);
        statsPage.setSizeOfPostsHasKeywordAndGeo(7);

        statsPage.setSizeOfCommentsInTotal(6);
        statsPage.setSizeOfCommentsHasKeyword(5);
        statsPage.setSizeOfCommentsHasKeywordAndGeo(4);

        statsPage.setSizeOfRepliesInTotal(3);
        statsPage.setSizeOfRepliesHasKeyword(2);
        statsPage.setSizeOfRepliesHasKeywordAndGeo(1);

        statsPage.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        statsPage.setNeedRefresh(true);

        try {
            session = factory.openSession();
            int rows = session.update("StatsPageMapper.updateByPageId", statsPage);
            session.commit();
            System.out.println(rows + " rows updated.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
