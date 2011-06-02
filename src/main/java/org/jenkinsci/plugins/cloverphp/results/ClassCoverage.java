package org.jenkinsci.plugins.cloverphp.results;

import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

/**
 * Clover Coverage results for a specific class.
 * @author Stephen Connolly
 */
public class ClassCoverage extends BaseCoverage {

    /**
     * exposed to jelly. 
     */
    public String relativeUrl(BaseCoverage parent) {
        StringBuilder url = new StringBuilder("..");
        BaseCoverage p = getParent();
        while (p != null && p != parent) {
            url.append("/..");
            p = p.getParent();
        }
        return url.toString();
    }

    @Override
    public BaseCoverage getPreviousResult() {
        CloverBuildAction action = getPreviousCloverBuildAction();
        if (action == null) {
            return null;
        }
        ProjectCoverage pc = action.getResult();
        if (pc == null) {
            return null;
        }
        BaseCoverage fc = pc.findChild(getParent().getURLSafeName());
        if (fc == null) {
            return null;
        }
        return fc.findChild(getURLSafeName());
    }
}
