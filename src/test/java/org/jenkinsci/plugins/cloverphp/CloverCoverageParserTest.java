package org.jenkinsci.plugins.cloverphp;

import org.junit.Test;
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

    @Test
    public void testTrimPaths_NullResult() {
        try {
            CloverCoverageParser.trimPaths(null, "prefix");
        } catch (NullPointerException e) {
            assertTrue("Expected exception thrown", true);
        }
    }

    @Test
    public void testTrimPaths_NullPrefix() {
        ProjectCoverage pc = new ProjectCoverage();
        ProjectCoverage result = CloverCoverageParser.trimPaths(pc, null);
        assertEquals(result, pc);
    }
    
    @Test(expected = NullPointerException.class)
    public void testParse_FileString_NullFile() throws Exception {
        CloverCoverageParser.parse(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void testParse_File_NullFile() throws Exception {
        CloverCoverageParser.parse(null);
    }

    @Test
    public void testParse() throws Exception {
        ProjectCoverage result = CloverCoverageParser.parse(getClass().getResourceAsStream("clover.xml"));
        assertNotNull(result);
        assertEquals(ProjectCoverage.class, result.getClass());
        assertEquals("StringUtilTest", result.getName());
        assertEquals(1, result.getFiles());        
        assertEquals(14, result.getNcloc());        
        assertEquals(16, result.getLoc());
        assertEquals(1, result.getClasses());
        assertEquals(2, result.getMethods());
        assertEquals(1, result.getCoveredmethods());
        assertEquals(2, result.getStatements());
        assertEquals(1, result.getCoveredstatements());
        assertEquals(4, result.getElements());
        assertEquals(2, result.getCoveredelements());
        
        assertEquals(1, result.getFileCoverages().size());
        FileCoverage fileResult = result.getFileCoverages().get(0);
        assertEquals("/var/lib/hudson/jobs/php-sample/workspace/phpsample/src/StringUtil.php", fileResult.getName());
        assertEquals(14, fileResult.getNcloc());        
        assertEquals(16, fileResult.getLoc());
        assertEquals(1, fileResult.getClasses());
        assertEquals(2, fileResult.getMethods());
        assertEquals(1, fileResult.getCoveredmethods());
        assertEquals(2, fileResult.getStatements());
        assertEquals(1, fileResult.getCoveredstatements());
        assertEquals(4, fileResult.getElements());
        assertEquals(2, fileResult.getCoveredelements());
        
        
        assertEquals(1, fileResult.getClassCoverages().size());
        ClassCoverage classResult = fileResult.getClassCoverages().get(0);
        assertEquals("StringUtil", classResult.getName());
        assertEquals(2, classResult.getMethods());
        assertEquals(1, classResult.getCoveredmethods());
        assertEquals(2, classResult.getStatements());
        assertEquals(1, classResult.getCoveredstatements());
        assertEquals(4, classResult.getElements());
        assertEquals(2, classResult.getCoveredelements());
    }

}
