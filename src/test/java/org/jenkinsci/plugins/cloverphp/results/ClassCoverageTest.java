package org.jenkinsci.plugins.cloverphp.results;

import org.jenkinsci.plugins.cloverphp.CloverBuildAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test of ClassCoverage
 * 
 * @author Seiji Sogabe
 */
public class ClassCoverageTest {
    
    ClassCoverage target;
    
    public ClassCoverageTest() {
    }

    @Before
    public void setUp() {
        target = new ClassCoverage();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getPreviousCoverage method, of class ClassCoverage.
     */
    @Test
    public void testGetPreviousResult_NullAction() {
        ClassCoverage mock = spy(target);
        doReturn(null).when(mock).getPreviousCloverBuildAction();
        assertNull(mock.getPreviousCoverage());
    }

    /**
     * Test of getPreviousCoverage method, of class ClassCoverage.
     */
    @Test
    public void testGetPreviousResult_NullProjectCoverage() {
        CloverBuildAction cba = mock(CloverBuildAction.class);
        when(cba.getResult()).thenReturn(null);
        
        ClassCoverage mock = spy(target);
        doReturn(cba).when(mock).getPreviousCloverBuildAction();
        
        BaseCoverage result = mock.getPreviousCoverage();
        assertNull(result);
    }

    /**
     * Test of getPreviousCoverage method, of class ClassCoverage.
     */
    @Test
    public void testGetPreviousResult_NullFileCoverage() {
        FileCoverage fc = mock(FileCoverage.class);
        when(fc.getURLSafeName()).thenReturn("anyString");
        
        ProjectCoverage pc = mock(ProjectCoverage.class);
        when(pc.findChild(anyString())).thenReturn(null);
        
        CloverBuildAction cba = mock(CloverBuildAction.class);
        when(cba.getResult()).thenReturn(pc);
        
        ClassCoverage mock = spy(target);
        doReturn(fc).when(mock).getParent();
        doReturn(cba).when(mock).getPreviousCloverBuildAction();
        
        BaseCoverage result = mock.getPreviousCoverage();
        assertNull(result);
    }

    /**
     * Test of getPreviousCoverage method, of class ClassCoverage.
     */
    @Test
    public void testGetPreviousResult() {
        ClassCoverage cc = mock(ClassCoverage.class);
        
        FileCoverage fc = mock(FileCoverage.class);
        when(fc.getURLSafeName()).thenReturn("anyString");
        when(fc.findChild(anyString())).thenReturn(cc);
        
        ProjectCoverage pc = mock(ProjectCoverage.class);
        when(pc.findChild(anyString())).thenReturn(fc);
        
        CloverBuildAction cba = mock(CloverBuildAction.class);
        when(cba.getResult()).thenReturn(pc);
        
        ClassCoverage mock = spy(target);
        doReturn("anyString").when(mock).getURLSafeName();
        doReturn(fc).when(mock).getParent();
        doReturn(cba).when(mock).getPreviousCloverBuildAction();
        
        BaseCoverage result = mock.getPreviousCoverage();
        assertNotNull(result);
    }
    
}
