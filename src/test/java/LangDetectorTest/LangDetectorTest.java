package LangDetectorTest;

import junit.framework.TestCase;
import massey.geospider.language.LangDetector;

public class LangDetectorTest extends TestCase {

    public LangDetectorTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsEnglish() {
        assertEquals(LangDetector.isEnglish("hello world!"), true);
        assertEquals(LangDetector.isEnglish("Bonjour le monde!"), false);

    }

}
