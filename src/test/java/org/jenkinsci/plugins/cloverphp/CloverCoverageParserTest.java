package org.jenkinsci.plugins.cloverphp;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.jenkinsci.plugins.cloverphp.results.ClassCoverage;
import org.jenkinsci.plugins.cloverphp.results.FileCoverage;
import org.jenkinsci.plugins.cloverphp.results.ProjectCoverage;


/**
 * CloverCoverageParser Tester.
 *
 * @author Stephen Connolly
 * @author Seiji Sogabe
 * @version 1.0
 */
public class CloverCoverageParserTest extends TestCase {
    public CloverCoverageParserTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFailureMode1() throws Exception {
        try {
            CloverCoverageParser.parse(null, "");
        } catch (NullPointerException e) {
            assertTrue("Expected exception thrown", true);
        }
    }

    public void testParse() throws Exception {
        ProjectCoverage result = CloverCoverageParser.parse(getClass().getResourceAsStream("clover.xml"));
        assertNotNull(result);
        assertEquals(ProjectCoverage.class, result.getClass());
        assertEquals("StringUtilTest", result.getName());
        assertEquals(2, result.getMethods());
        
        assertEquals(1, result.getFileCoverages().size());
        FileCoverage fileResult = result.getFileCoverages().get(0);
        assertEquals("/var/lib/hudson/jobs/php-sample/workspace/phpsample/src/StringUtil.php", fileResult.getName());
        assertEquals(14, fileResult.getNcloc());        
        
        assertEquals(1, fileResult.getClassCoverages().size());
        ClassCoverage classResult = fileResult.getClassCoverages().get(0);
        assertEquals("StringUtil", classResult.getName());
        assertEquals(2, classResult.getStatements());
    }

    public static Test suite() {
        return new TestSuite(CloverCoverageParserTest.class);
    }
}
