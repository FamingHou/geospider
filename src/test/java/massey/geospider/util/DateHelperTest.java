/**
 * 
 */
package massey.geospider.util;

import java.sql.Timestamp;

import junit.framework.TestCase;

/**
 * @author Frank Hou (faming.hou@gmail.com)
 *
 */
public class DateHelperTest extends TestCase {

    /**
     * @param name
     */
    public DateHelperTest(String name) {
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

    public void testEpochTime() {
        long epochTime = 1517851935;
        Timestamp ts = DateHelper.parse(epochTime * 1000);
        System.out.println(ts);

        epochTime = 1517874560;
        ts = DateHelper.parse(epochTime * 1000);
        System.out.println(ts);
    }

}
