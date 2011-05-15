package org.jenkinsci.plugins.cloverphp;

import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Roundtrip Test
 * 
 * @author Seiji Sogabe
 */
public class ConfigurationRoundtripTest extends HudsonTestCase {

    public void testRoundtrip() throws Exception {

        FreeStyleProject p = createFreeStyleProject();

        CloverPublisher publisher = new CloverPublisher("reports", "coverage.xml", false);
        p.getPublishersList().add(publisher);

        submit(new WebClient().getPage(p, "configure").getFormByName("config"));

        assertEquals("reports", publisher.getReportDir());
        assertEquals("coverage.xml", publisher.getReportFileName());
        assertFalse(publisher.isDisableArchiving());

    }

    /**
     * check default value of report filename.
     * 
     * @throws Exception 
     */
    public void testRoundtrip2() throws Exception {

        FreeStyleProject p = createFreeStyleProject();

        CloverPublisher publisher = new CloverPublisher("reports", "", true);
        p.getPublishersList().add(publisher);

        submit(new WebClient().getPage(p, "configure").getFormByName("config"));

        assertEquals("reports", publisher.getReportDir());
        assertEquals("clover.xml", publisher.getReportFileName());
        assertTrue(publisher.isDisableArchiving());

    }

}
