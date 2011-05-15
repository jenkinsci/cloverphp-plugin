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

        CloverPublisher publisher = new CloverPublisher("reports", "clover.xml", false);
        p.getPublishersList().add(publisher);

        submit(new WebClient().getPage(p, "configure").getFormByName("config"));

    }
}
