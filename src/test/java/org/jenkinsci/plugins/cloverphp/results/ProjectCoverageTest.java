package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.AbstractBuild;
import java.util.List;
import org.jenkinsci.plugins.cloverphp.CloverBuildAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test of ProjectCoverage
 * 
 * @author Seiji Sogabe
 */
public class ProjectCoverageTest {

    private ProjectCoverage target;

    public ProjectCoverageTest() {
    }

    @Before
    public void setUp() {
        target = new ProjectCoverage();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getChildren method, of class ProjectCoverage.
     */
    @Test
    public void testGetChildren() {
        FileCoverage fc = mock(FileCoverage.class);
        target.addFileCoverage(fc);
        List<FileCoverage> list = target.getChildren();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(fc, list.get(0));
    }

    /**
     * Test of addFileCoverage method, of class ProjectCoverage.
     */
    @Test
    public void testAddFileCoverage() {
        FileCoverage fc = mock(FileCoverage.class);
        target.addFileCoverage(fc);
    }

    /**
     * Test of getFileCoverages method, of class ProjectCoverage.
     */
    @Test
    public void testGetFileCoverages() {
        FileCoverage fc1 = mock(FileCoverage.class);
        FileCoverage fc2 = mock(FileCoverage.class);
        target.addFileCoverage(fc1);
        target.addFileCoverage(fc1);
        List<FileCoverage> list = target.getFileCoverages();
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    /**
     * Test of findFileCoverage method, of class ProjectCoverage.
     */
    @Test
    public void testFindFileCoverage() {
        FileCoverage fc1 = mock(FileCoverage.class);
        when(fc1.getURLSafeName()).thenReturn("org");
        FileCoverage fc2 = mock(FileCoverage.class);
        when(fc2.getURLSafeName()).thenReturn("org.jenkinsci");

        target.addFileCoverage(fc1);
        target.addFileCoverage(fc2);

        assertEquals(fc2, target.findFileCoverage("org.jenkinsci"));
        assertNull(target.findFileCoverage("null"));
    }

    /**
     * Test of getDynamic method, of class ProjectCoverage.
     */
    @Test
    public void testGetDynamic() throws Exception {
        StaplerRequest req = mock(StaplerRequest.class);
        StaplerResponse res = mock(StaplerResponse.class);

        FileCoverage fc1 = mock(FileCoverage.class);
        when(fc1.getURLSafeName()).thenReturn("org");
        FileCoverage fc2 = mock(FileCoverage.class);
        when(fc2.getURLSafeName()).thenReturn("org.jenkinsci");

        target.addFileCoverage(fc1);
        target.addFileCoverage(fc2);

        assertEquals(fc2, (FileCoverage)target.getDynamic("org.jenkinsci", req, res));
    }

    /**
     * Test of getPreviousResult method, of class ProjectCoverage.
     */
    @Test
    public void testGetPreviousResult() {
        CloverBuildAction cba = mock(CloverBuildAction.class);
        ProjectCoverage p = mock(ProjectCoverage.class);
        when(cba.getResult()).thenReturn(p);

        ProjectCoverage coverage = spy(target);
        doReturn(cba).when(coverage).getPreviousCloverBuildAction();

        AbstractClassMetrics result = coverage.getPreviousResult();

        assertNotNull(result);
        assertEquals(p, result);
    }

    /**
     * Test of getPreviousResult method, of class ProjectCoverage.
     */
    @Test
    public void testGetPreviousResult_ActionNull() {
        ProjectCoverage coverage = spy(target);
        doReturn(null).when(coverage).getPreviousCloverBuildAction();

        AbstractClassMetrics result = coverage.getPreviousResult();

        assertNull(result);
    }
    
    /**
     * Test of setOwner method, of class ProjectCoverage.
     */
    @Test
    public void testSetOwner() {
        AbstractBuild owner = mock(AbstractBuild.class);
        FileCoverage fc1 = spy(new FileCoverage());
        doReturn("org").when(fc1).getURLSafeName();

        target.addFileCoverage(fc1);
        target.setOwner(owner);

        assertEquals(owner, fc1.getOwner());
    }
}
