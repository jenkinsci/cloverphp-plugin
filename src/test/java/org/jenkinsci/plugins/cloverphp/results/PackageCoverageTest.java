package org.jenkinsci.plugins.cloverphp.results;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Patrick Brückner on 28.08.15.
 */
public class PackageCoverageTest extends TestCase {

    public void testGetPreviousResult() throws Exception {

        // GIVEN a package inside a project with a previous coverage result too
        ProjectCoverage previousProjectCoverage = new ProjectCoverage();
        PackageCoverage previousPackageCoverage = new PackageCoverage();
        previousPackageCoverage.setName("NamespaceTest");
        previousProjectCoverage.addPackageCoverage(previousPackageCoverage);

        ProjectCoverage projectCoverage = mock(ProjectCoverage.class);
        PackageCoverage packageCoverage = new PackageCoverage();
        packageCoverage.setName("NamespaceTest");
        projectCoverage.addPackageCoverage(packageCoverage);
        packageCoverage.setParent(projectCoverage);

        when(projectCoverage.getPreviousResult()).thenReturn(previousProjectCoverage);

        // WHEN getting the previous results
        AbstractClassMetrics result = packageCoverage.getPreviousResult();

        // IT should
        assertNotNull("not be null", result);
        assertEquals("be the coverage object of the previous build", previousPackageCoverage, result);
    }
}