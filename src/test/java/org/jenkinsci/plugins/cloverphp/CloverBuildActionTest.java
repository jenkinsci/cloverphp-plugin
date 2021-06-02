package org.jenkinsci.plugins.cloverphp;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.Result;
import org.jenkinsci.plugins.cloverphp.results.ProjectCoverage;
import org.jenkinsci.plugins.cloverphp.targets.CoverageTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CloverBuildAction
 * 
 * @author Seiji Sogabe
 */
public class CloverBuildActionTest {

    public CloverBuildActionTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetBuildHealth_HealthyTargetNull() {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = null;
        CoverageTarget unhealthyTarget = new CoverageTarget();

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);
        assertNull(action.getBuildHealth());
    }

    @Test
    public void testGetBuildHealth_UnHealthyTargetNull() {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = new CoverageTarget();
        CoverageTarget unhealthyTarget = null;

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);
        assertNull(action.getBuildHealth());
    }

    @Test
    public void testGetResult_NotNullCoverage() {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = null;
        CoverageTarget unhealthyTarget = new CoverageTarget();

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);

        assertEquals(prjCoverage, action.getResult());
    }

    @Test
    public void testTarget() {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = null;
        CoverageTarget unhealthyTarget = new CoverageTarget();

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);

        assertEquals(prjCoverage, action.getTarget());
    }

    @Test
    public void testGetPreviousResult_FirstBuild() {
        AbstractBuild<?, ?> b = mock(AbstractBuild.class);
        when(b.getPreviousBuild()).thenReturn(null);

        assertNull(CloverBuildAction.getPreviousResult(b));
    }

    @Test
    public void testGetPreviousResult_SuccessBuild() {
        AbstractBuild b = mock(AbstractBuild.class);
        AbstractBuild p = mock(AbstractBuild.class);
        CloverBuildAction pa = mock(CloverBuildAction.class);

        when(p.getResult()).thenReturn(Result.SUCCESS);
        when(p.getAction(CloverBuildAction.class)).thenReturn(pa);
        when(b.getPreviousBuild()).thenReturn(p);

        assertNotNull(CloverBuildAction.getPreviousResult(b));
        assertEquals(pa, CloverBuildAction.getPreviousResult(b));
    }

    @Test
    public void testGetPreviousResult_FailBuild() {
        AbstractBuild b = mock(AbstractBuild.class);
        AbstractBuild p1 = mock(AbstractBuild.class);
        AbstractBuild p2 = mock(AbstractBuild.class);
        CloverBuildAction pa = mock(CloverBuildAction.class);

        when(p2.getResult()).thenReturn(Result.SUCCESS);
        when(p2.getAction(CloverBuildAction.class)).thenReturn(pa);
        when(p1.getResult()).thenReturn(Result.FAILURE);

        when(b.getPreviousBuild()).thenReturn(p1);
        when(p1.getPreviousBuild()).thenReturn(p2);

        assertNotNull(CloverBuildAction.getPreviousResult(b));
        assertEquals(pa, CloverBuildAction.getPreviousResult(b));
    }

    @Test
    public void testPreviousResult() {
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = null;
        CoverageTarget unhealthyTarget = new CoverageTarget();
        
        when(build.getPreviousBuild()).thenReturn(null);

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);

        assertNull(action.getPreviousResult());
    }

    @Test
    public void testAllTargetsReached() {
        // GIVEN a build with some coverage metrics
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = new CoverageTarget();
        CoverageTarget unhealthyTarget = new CoverageTarget();

        prjCoverage.setCoveredelements(100);
        prjCoverage.setElements(100);
        healthyTarget.setElementCoverage(100);

        prjCoverage.setCoveredstatements(100);
        prjCoverage.setStatements(100);
        healthyTarget.setStatementCoverage(100);

        prjCoverage.setCoveredmethods(100);
        prjCoverage.setMethods(100);
        healthyTarget.setMethodCoverage(100);

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);

        // WHEN calculating the health report
        HealthReport healthReport = action.getBuildHealth();

        // IT shoud
        assertNotNull(healthReport);
        assertEquals(100, healthReport.getScore());

    }


    @Test
    public void testHealthReportAmplification() {
        // GIVEN a build with some coverage metrics
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        String workspacePath = "/tmp/workpath";
        ProjectCoverage prjCoverage = new ProjectCoverage();
        CoverageTarget healthyTarget = new CoverageTarget();
        CoverageTarget unhealthyTarget = new CoverageTarget();

        prjCoverage.setCoveredelements(300);
        prjCoverage.setElements(1000);
        healthyTarget.setElementCoverage(30); // 300/1000 = 30%, target 30% => 100% match

        prjCoverage.setCoveredstatements(300);
        prjCoverage.setStatements(1000);
        healthyTarget.setStatementCoverage(40); // 300/1000 = 30%, target 40% => 75% match

        prjCoverage.setCoveredmethods(300);
        prjCoverage.setMethods(1000);
        healthyTarget.setMethodCoverage(50); // 300/1000 = 30%, target 50% => 60% match

        CloverBuildAction action = new CloverBuildAction(build, workspacePath, prjCoverage, healthyTarget, unhealthyTarget);

        // WHEN calculating the health report
        HealthReport healthReport = action.getBuildHealth();

        // IT shoud
        assertEquals("return 60% score, as this is the lowest value", 60, healthReport.getScore());
    }

}
