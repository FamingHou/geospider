package massey.geospider.util;

import junit.framework.TestCase;

public class StringHelperTest extends TestCase {

    public StringHelperTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHasKeyword() {
        boolean ret = StringHelper.hasKeyword(null, "a");
        System.out.println(ret);
        assertEquals(false, ret);

        ret = StringHelper.hasKeyword("Auckland", null);
        System.out.println(ret);
        assertEquals(false, ret);

        ret = StringHelper.hasKeyword("Auckland", "A");
        System.out.println(ret);
        assertEquals(true, ret);

        ret = StringHelper.hasKeyword("Auckland", "a");
        System.out.println(ret);
        assertEquals(true, ret);
        
        ret = StringHelper.hasKeyword("China", "a");
        System.out.println(ret);
        assertEquals(true, ret);

        ret = StringHelper.hasKeyword("Sara Houston", "ara h");
        System.out.println(ret);
        assertEquals(true, ret);

    }
}
