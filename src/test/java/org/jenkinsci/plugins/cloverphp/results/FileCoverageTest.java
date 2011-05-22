package org.jenkinsci.plugins.cloverphp.results;

import org.jenkinsci.plugins.cloverphp.CloverBuildAction;
import hudson.model.AbstractBuild;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test of FileCoverage
 * 
 * @author Seiji Sogabe
 */
public class FileCoverageTest {

    private FileCoverage target;
    
    public FileCoverageTest() {
    }

    @Before
    public void setUp() {
        target = new FileCoverage();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getChildren method, of class FileCoverage.
     */
    @Test
    public void testGetChildren() {
        ClassCoverage cc1 = new ClassCoverage();
        target.addClassCoverage(cc1);
        ClassCoverage cc2 = new ClassCoverage();
        target.addClassCoverage(cc2);
        List<ClassCoverage> list = target.getChildren();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.contains(cc1));
        assertTrue(list.contains(cc2));
    }

    /**
     * Test of getDynamic method, of class ClassCoverage.
     */
    @Test
    public void testGetDynamic() throws Exception {
        StaplerRequest req = mock(StaplerRequest.class);
        StaplerResponse res = mock(StaplerResponse.class);

        ClassCoverage cc1 = mock(ClassCoverage.class);
        when(cc1.getURLSafeName()).thenReturn("org");
        ClassCoverage cc2 = mock(ClassCoverage.class);
        when(cc2.getURLSafeName()).thenReturn("org.jenkinsci");

        target.addClassCoverage(cc1);
        target.addClassCoverage(cc2);

        assertEquals(cc2, target.getDynamic("org.jenkinsci", req, res));
    }

    /**
     * Test of addClassCoverage method, of class FileCoverage.
     */
    @Test
    public void testAddClassCoverage() {
        ClassCoverage cc = new ClassCoverage();
        target.addClassCoverage(cc);
        assertEquals(target, cc.getParent());
    }

    /**
     * Test of getClassCoverages method, of class FileCoverage.
     */
    @Test
    public void testGetClassCoverages() {
        ClassCoverage cc1 = new ClassCoverage();
        target.addClassCoverage(cc1);
        ClassCoverage cc2 = new ClassCoverage();
        target.addClassCoverage(cc2);
        List<ClassCoverage> list = target.getClassCoverages();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.contains(cc1));
        assertTrue(list.contains(cc2));
    }

    /**
     * Test of findClassCoverage method, of class FileCoverage.
     */
    @Test
    public void testFindClassCoverage() {
        ClassCoverage cc1 = spy(new ClassCoverage());
        doReturn("org").when(cc1).getURLSafeName();
        ClassCoverage cc2 = spy(new ClassCoverage());
        doReturn("org.jenkinsci").when(cc2).getURLSafeName();
        target.addClassCoverage(cc1);
        target.addClassCoverage(cc2);
        
        ClassCoverage result = target.findClassCoverage("org.jenkinsci");
        
        assertNotNull(result);
        assertEquals(cc2, result);
    }

    /**
     * Test of findClassCoverage method, of class FileCoverage.
     */
    @Test
    public void testFindClassCoverage_NotFound() {
        ClassCoverage cc1 = spy(new ClassCoverage());
        doReturn("org").when(cc1).getURLSafeName();
        ClassCoverage cc2 = spy(new ClassCoverage());
        doReturn("org.jenkinsci").when(cc2).getURLSafeName();
        target.addClassCoverage(cc1);
        target.addClassCoverage(cc2);
        
        ClassCoverage result = target.findClassCoverage("org.jenkinsci.plugins");
        
        assertNull(result);
    }
    
    /**
     * Test of getPreviousResult method, of class FileCoverage.
     */
    @Test
    public void testGetPreviousResult() {
        CloverBuildAction cba = mock(CloverBuildAction.class);
        ProjectCoverage pc = mock(ProjectCoverage.class);
        FileCoverage fc = new FileCoverage();
        when(pc.findFileCoverage(anyString())).thenReturn(fc);
        when(cba.getResult()).thenReturn(pc);

        FileCoverage coverage = spy(target);
        doReturn(cba).when(coverage).getPreviousCloverBuildAction();
        doReturn("org").when(coverage).getURLSafeName();

        AbstractClassMetrics result = coverage.getPreviousResult();

        assertNotNull(result);
        assertEquals(fc, result);
    }

    /**
     * Test of getPreviousResult method, of class FileCoverage.
     */
    @Test
    public void testGetPreviousResult_NoCoverage() {
        CloverBuildAction cba = mock(CloverBuildAction.class);
        when(cba.getResult()).thenReturn(null);

        FileCoverage coverage = spy(target);
        doReturn(cba).when(coverage).getPreviousCloverBuildAction();
        doReturn("org").when(coverage).getURLSafeName();

        AbstractClassMetrics result = coverage.getPreviousResult();

        assertNull(result);
    }

    /**
     * Test of getPreviousResult method, of class FileCoverage.
     */
    @Test
    public void testGetPreviousResult_NoAction() {
        FileCoverage coverage = spy(target);
        doReturn(null).when(coverage).getPreviousCloverBuildAction();
        AbstractClassMetrics result = coverage.getPreviousResult();
        assertNull(result);
    }
    
    /**
     * Test of setOwner method, of class FileCoverage.
     */
    @Test
    public void testSetOwner() {
        AbstractBuild owner = mock(AbstractBuild.class);
        ClassCoverage cc = spy(new ClassCoverage());
        doReturn("org").when(cc).getURLSafeName();

        target.addClassCoverage(cc);
        target.setOwner(owner);

        assertEquals(owner, cc.getOwner());
    }
}
