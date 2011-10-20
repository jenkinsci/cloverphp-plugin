package org.jenkinsci.plugins.cloverphp;

import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Roundtrip Test
 * 
 * @author Seiji Sogabe
 */
public class ConfigurationRoundtripTest extends HudsonTestCase {

    public void testRoundtrip() throws Exception {

        FreeStyleProject p = createFreeStyleProject();

        CloverPublisher publisher = new CloverPublisher("coverage.xml", "true", "reports", false);
        p.getPublishersList().add(publisher);

        submit(new WebClient().getPage(p, "configure").getFormByName("config"));

        assertEquals("reports", publisher.getReportDir());
        assertEquals("coverage.xml", publisher.getXmlLocation());
        assertFalse(publisher.isDisableArchiving());
        assertTrue(publisher.isPublishHtmlReport());
    }

    /**
     * check default value of report filename.
     * 
     * @throws Exception 
     */
    public void testRoundtrip2() throws Exception {

        FreeStyleProject p = createFreeStyleProject();

        CloverPublisher publisher = new CloverPublisher("coverage.xml", null, "reports", true);
        p.getPublishersList().add(publisher);

        submit(new WebClient().getPage(p, "configure").getFormByName("config"));

        assertEquals("reports", publisher.getReportDir());
        assertTrue(publisher.isDisableArchiving());
        assertFalse(publisher.isPublishHtmlReport());
    }

    /**
     * check default value of report filename.
     * 
     * @throws Exception 
     */
    @Bug(11408)
    public void testNotPublishAndReportDirIsNotSet() throws Exception {

        FreeStyleProject p = createFreeStyleProject();

        CloverPublisher publisher = new CloverPublisher("coverage.xml", null, null, false);
        p.getPublishersList().add(publisher);

        submit(new WebClient().getPage(p, "configure").getFormByName("config"));
        
        assertBuildStatusSuccess(p.scheduleBuild2(0, new Cause.UserCause()).get());

        assertNull(publisher.getReportDir());
        assertFalse(publisher.isDisableArchiving());
        assertFalse(publisher.isPublishHtmlReport());
    }
    
}
