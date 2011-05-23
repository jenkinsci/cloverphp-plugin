package org.jenkinsci.plugins.cloverphp.results;

import org.jenkinsci.plugins.cloverphp.CloverBuildAction;
import hudson.model.Run;
import hudson.model.AbstractBuild;
import java.util.List;
import org.jenkinsci.plugins.cloverphp.Ratio;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * AbstractClassMetricsTest 
 * 
 * @author Seiji Sogabe
 */
public class AbstractClassMetricsTest {

    AbstractClassMetrics target;

    public AbstractClassMetricsTest() {
    }

    @Before
    public void setUp() {
        target = new AbstractClassMetrics() {

            @Override
            public AbstractClassMetrics getPreviousResult() {
                return null;
            }
        };
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMethodCoverage method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetMethodCoverage() {
        target.setCoveredmethods(10);
        target.setMethods(1);
        assertEquals(Ratio.create(10, 1), target.getMethodCoverage());
    }

    /**
     * Test of getStatementCoverage method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetStatementCoverage() {
        target.setCoveredstatements(10);
        target.setStatements(1);
        assertEquals(Ratio.create(10, 1), target.getStatementCoverage());
    }

    /**
     * Test of getElementCoverage method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetElementCoverage() {
        target.setCoveredelements(10);
        target.setElements(1);
        assertEquals(Ratio.create(10, 1), target.getElementCoverage());
    }

    /**
     * Test of getURLSafeName method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetURLSafeName() {
        target.setName(" /org+/jenkins");
        assertEquals("%20_org__jenkins", target.getURLSafeName());
    }

    /**
     * Test of getParents method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetParents() {
        // p -> p1 -> p2
        AbstractClassMetrics p2 = mock(AbstractClassMetrics.class);
        AbstractClassMetrics p1 = mock(AbstractClassMetrics.class);
        when(p1.getParent()).thenReturn(p2);
        AbstractClassMetrics acm = spy(target);
        doReturn(p1).when(acm).getParent();

        List<AbstractClassMetrics> result = acm.getParents();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(p2, result.get(0));
        assertEquals(p1, result.get(1));
    }

    /**
     * Test of relativeUrl method, of class AbstractClassMetrics.
     */
    @Test
    public void testRelativeUrl() {
        // p -> p1 -> p2
        AbstractClassMetrics p2 = mock(AbstractClassMetrics.class);
        AbstractClassMetrics p1 = mock(AbstractClassMetrics.class);
        when(p1.getParent()).thenReturn(p2);
        AbstractClassMetrics acm = spy(target);
        doReturn(p1).when(acm).getParent();

        assertEquals("../..", acm.relativeUrl(p2));
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetPreviousCloverBuildAction_OwnerNull() {
        target.setOwner(null);
        assertNull(target.getPreviousCloverBuildAction());
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetPreviousCloverBuildAction_PreviousBuildNull() {
        AbstractBuild b = mock(AbstractBuild.class);
        when(b.getPreviousBuild()).thenReturn(null);
        target.setOwner(b);
        assertNull(target.getPreviousCloverBuildAction());
    }

    /**
     * Test of getPreviousCloverBuildAction method, of class AbstractClassMetrics.
     */
    @Test
    public void testGetPreviousCloverBuildAction_ActionNull() {
        CloverBuildAction cba = mock(CloverBuildAction.class);
        Run<?, ?> r = mock(Run.class);
        when(r.getAction(CloverBuildAction.class)).thenReturn(cba);
        AbstractBuild b = mock(AbstractBuild.class);
        when(b.getPreviousBuild()).thenReturn(r);
        target.setOwner(b);
        assertEquals(cba, target.getPreviousCloverBuildAction());
    }
}
