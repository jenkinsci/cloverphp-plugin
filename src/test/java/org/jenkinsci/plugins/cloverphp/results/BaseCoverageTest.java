package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.Run;
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
 * Test for BaseCoverage.
 * 
 * @author Seiji Sogabe
 */
public class BaseCoverageTest {

    public BaseCoverageTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getParents method, of class BaseCoverage.
     */
    @Test
    public void testGetParents_ProjectCoverage() {
        ProjectCoverage target = new ProjectCoverage();

        List<BaseCoverage> result = target.getParents();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Test of getParents method, of class BaseCoverage.
     */
    @Test
    public void testGetParents_ClassCoverage() {
        ProjectCoverage p = new ProjectCoverage();
        FileCoverage f = new FileCoverage();
        ClassCoverage target = new ClassCoverage();
        target.setParent(f);
        f.setParent(p);

        List<BaseCoverage> result = target.getParents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(p, result.get(0));
        assertEquals(f, result.get(1));
    }

    /**
     * Test of addChild method, of class BaseCoverage.
     */
    @Test
    public void testAddChild() {
        FileCoverage target = new FileCoverage();
        ClassCoverage child = new ClassCoverage();

        boolean result = target.addChild(child);
        assertTrue(result);
        
        List<BaseCoverage> children = target.getChildren();

        assertEquals(target, child.getParent());
        assertNotNull(children);
        assertEquals(1, children.size());
        assertEquals(child, children.get(0));
    }

    /**
     * Test of addChild method, of class BaseCoverage.
     */
    @Test
    public void testAddChild_AddNull() {
        FileCoverage target = new FileCoverage();

        boolean result = target.addChild(null);
        assertFalse(result);
        
        List<BaseCoverage> children = target.getChildren();
        assertNotNull(children);
        assertEquals(0, children.size());
    }
    
    /**
     * Test of findChild method, of class BaseCoverage.
     */
    @Test
    public void testFindChild() {
        FileCoverage target = new FileCoverage();
        ClassCoverage child1 = new ClassCoverage();
        child1.setName("ClassCoverage1");
        ClassCoverage child2 = new ClassCoverage();
        child2.setName("ClassCoverage2");
        target.addChild(child1);
        target.addChild(child2);

        BaseCoverage result = target.findChild("ClassCoverage2");
        assertEquals(child2, result);
        result = target.findChild("Not Found");
        assertNull(result);
    }

    /**
     * Test of findChild method, of class BaseCoverage.
     */
    @Test
    public void testFindChild_Null() {
        FileCoverage target = new FileCoverage();
        BaseCoverage result = target.findChild(null);
        assertNull(result);
    }
    
    /**
     * Test of getURLSafeName method, of class BaseCoverage.
     */
    @Test
    public void testGetURLSafeName() {
        FileCoverage target = new FileCoverage();
        target.setName(" AA/+AA");

        String result = target.getURLSafeName();
        assertEquals("%20AA__AA", result);
    }

    /**
     * Test of getURLSafeName method, of class BaseCoverage.
     */
    @Test
    public void testGetURLSafeName_NameNull() {
        FileCoverage target = new FileCoverage();
        target.setName(null);

        String result = target.getURLSafeName();
        assertNull( result);
    }
    
    /**
     * Test of relativeUrl method, of class BaseCoverage.
     */
    @Test
    public void testRelativeUrl_FileCoverage() {
        ProjectCoverage p = new ProjectCoverage();
        FileCoverage target = new FileCoverage();
        p.addChild(target);

        String result = target.relativeUrl(p);

        assertEquals("..", result);
    }

    /**
     * Test of relativeUrl method, of class BaseCoverage.
     */
    @Test
    public void testRelativeUrl_ClassCoverage() {
        ProjectCoverage p = new ProjectCoverage();
        FileCoverage f = new FileCoverage();
        ClassCoverage target = new ClassCoverage();
        p.addChild(f);
        f.addChild(target);

        String result = target.relativeUrl(p);
        assertEquals("../..", result);
        
        result = target.relativeUrl(f);
        assertEquals("..", result);
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class BaseCoverage.
     */
    @Test
    public void testGetPreviousCloverBuildAction_OwnerNull() {
        ProjectCoverage target = new ProjectCoverage();
        target.setOwner(null);

        CloverBuildAction result = target.getPreviousCloverBuildAction();

        assertNull(result);
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class BaseCoverage.
     */
    @Test
    public void testGetPreviousCloverBuildAction_NoPreviousBuild() {
        AbstractBuild owner = mock(AbstractBuild.class);
        when(owner.getPreviousBuild()).thenReturn(null);

        ProjectCoverage target = new ProjectCoverage();
        target.setOwner(owner);

        CloverBuildAction result = target.getPreviousCloverBuildAction();

        assertNull(result);
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class BaseCoverage.
     */
    @Test
    public void testGetPreviousCloverBuildAction_PreviousBuildHasAction() {
        // previoudBuild -> action
        CloverBuildAction action = mock(CloverBuildAction.class);
        Run previousBuild = mock(Run.class);
        when(previousBuild.getAction(CloverBuildAction.class)).thenReturn(action);

        AbstractBuild owner = mock(AbstractBuild.class);
        when(owner.getPreviousBuild()).thenReturn(previousBuild);

        ProjectCoverage target = new ProjectCoverage();
        target.setOwner(owner);

        CloverBuildAction result = target.getPreviousCloverBuildAction();

        assertEquals(action, result);
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class BaseCoverage.
     */
    @Test
    public void testGetPreviousCloverBuildAction_PreviousBuildHasNoAction() {
        // previoudBuild -> previousBuild2 -> action
        CloverBuildAction action = mock(CloverBuildAction.class);

        Run previousBuild2 = mock(Run.class);
        when(previousBuild2.getAction(CloverBuildAction.class)).thenReturn(action);

        Run previousBuild = mock(Run.class);
        when(previousBuild.getPreviousBuild()).thenReturn(previousBuild2);
        when(previousBuild.getAction(CloverBuildAction.class)).thenReturn(null);

        AbstractBuild owner = mock(AbstractBuild.class);
        when(owner.getPreviousBuild()).thenReturn(previousBuild);

        ProjectCoverage target = new ProjectCoverage();
        target.setOwner(owner);

        CloverBuildAction result = target.getPreviousCloverBuildAction();

        assertEquals(action, result);
    }

    /**
     * Test of getDynamic method, of class BaseCoverage.
     */
    @Test
    public void testGetDynamic() throws Exception {
        ClassCoverage child = new ClassCoverage();
        FileCoverage target = spy(new FileCoverage());
        doReturn(child).when(target).findChild(anyString());

        BaseCoverage result = target.getDynamic("anystring", mock(StaplerRequest.class), mock(StaplerResponse.class));

        assertEquals(child, result);
    }
}
