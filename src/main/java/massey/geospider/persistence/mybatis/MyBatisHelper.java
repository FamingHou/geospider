/**
 * 
 */
package massey.geospider.persistence.mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

/**
 * All operations using MyBatis library should use this class. This is a
 * singleton class so as to make sure the file mybatis-config.xml is loaded only
 * one time and only one sqlSessionFactory object of class type
 * SqlSessionFactory is created.
 * 
 * It is also a thread-safe class as database operations can be processed
 * concurrently.
 * 
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class MyBatisHelper {

    private static final Logger log = Logger.getLogger(MyBatisHelper.class);
    /** the singleton object of class MyBatisHelper */
    private static MyBatisHelper helper;
    /**
     * the singleton object of class SqlSessionFactory used for creating session
     * object of SqlSession
     */
    private SqlSessionFactory sqlSessionFactory;

    /**
     * @throws IOException
     * 
     */
    private MyBatisHelper() throws IOException {
        log.debug("MyBatisHelper#constructor()");
        loadConfiguration();
    }

    /**
     * Creates the singleton object of class SqlSessionFactory with the property
     * values defined in mybatis-config.xml
     * 
     * @throws IOException
     */
    private void loadConfiguration() throws IOException {
        final String resource = "mybatis/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    /**
     * Creates a singleton object of class MyBatisHelper
     * 
     * @return the singleton object of class MyBatisHelper
     * @throws IOException
     */
    public static MyBatisHelper getSingle() throws IOException {
        if (helper == null) {
            synchronized (log) {
                if (helper == null) { // double check whether helper is created?
                    helper = new MyBatisHelper();
                }
            }
        }
        return helper;
    }

    /**
     * Executes a insert SQL with the identifier statement and passes the object
     * parameter to this statement
     * 
     * @param statement
     *            Unique identifier matching the statement to execute
     * @param parameter
     *            A parameter object to pass to the statement
     * @return the number of rows affected. -1 will be returned if error
     *         happens.
     */
    public int insertOne(String statement, Object parameter) {
        int rowsAffected = -1;
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            rowsAffected = session.insert(statement, parameter);
            session.commit();
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (session != null)
                session.close();
        }
        return rowsAffected;
    }

    /**
     * Retrieve a single row mapped from the statement key and parameter.
     * 
     * @param <T>
     *            the returned object type
     * @param statement
     *            Unique identifier matching the statement to use.
     * @param parameter
     *            A parameter object to pass to the statement.
     * @return Mapped object
     */
    public <T> T selectOne(String statement, Object parameter) {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return session.selectOne(statement, parameter);
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (session != null)
                session.close();
        }
        return null;
    }

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     * 
     * @param statement
     *            Unique identifier matching the statement to execute
     * @param parameter
     *            A parameter object to pass to the statement
     * @return the number of rows affected. -1 will be returned if error
     *         happens.
     */
    public int delete(String statement, Object parameter) {
        int rowsAffected = -1;
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            rowsAffected = session.delete(statement, parameter);
            session.commit();
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (session != null)
                session.close();
        }
        return rowsAffected;
    }

    /**
     * Execute a update statement. The number of rows affected will be returned.
     * 
     * @param statement
     *            Unique identifier matching the statement to execute
     * @param parameter
     *            A parameter object to pass to the statement
     * @return the number of rows affected. -1 will be returned if error
     *         happens.
     */
    public int update(String statement, Object parameter) {
        int rowsAffected = -1;
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            rowsAffected = session.update(statement, parameter);
            session.commit();
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (session != null)
                session.close();
        }
        return rowsAffected;
    }
}
