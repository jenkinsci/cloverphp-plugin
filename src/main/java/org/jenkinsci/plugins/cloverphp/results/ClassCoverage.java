package org.jenkinsci.plugins.cloverphp.results;

import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

/**
 * Clover Coverage results for a specific class.
 * @author Stephen Connolly
 */
public class ClassCoverage extends AbstractCloverMetrics {

    public AbstractCloverMetrics getPreviousResult() {
        CloverBuildAction action = getPreviousCloverBuildAction();
        if (action == null) {
            return null;
        }
        ProjectCoverage pc = action.getResult();
        if (pc == null) {
            return null;
        }
        FileCoverage fc = pc.findFileCoverage(getParent().getURLSafeName());
        if (fc == null) {
            return null;
        }
        return fc.findClassCoverage(getURLSafeName());
    }

    /**
     *  set classname only. 
     * @param name 
     */
    @Override
    public void setName(String name) {
        if (name != null) {
            int idx = name.lastIndexOf('.');
            if (idx > 0) {
                name = name.substring(idx + 1);
            }
        }
        super.setName(name);
    }
 
}
