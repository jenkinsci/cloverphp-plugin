package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

/**
 * Clover Coverage results for a specific file.
 * @author Stephen Connolly
 */
public class FileCoverage extends BaseCoverage {

    private int classes;

    private int loc;

    private int ncloc;

    public int getClasses() {
        return classes;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getNcloc() {
        return ncloc;
    }

    public void setNcloc(int ncloc) {
        this.ncloc = ncloc;
    }
    
    /**
     * exposed to jelly. 
     */
    @Override
    public BaseCoverage getPreviousCoverage() {
        CloverBuildAction action = getPreviousCloverBuildAction();
        if (action == null) {
            return null;
        }
        ProjectCoverage projectCoverage = action.getResult();
        if (projectCoverage == null) {
            return null;
        }
        return projectCoverage.findChild(getURLSafeName());
    }

    @Override
    public void setOwner(AbstractBuild owner) {
        super.setOwner(owner);    
        for (BaseCoverage child : getChildren()) {
            child.setOwner(owner);
        }
    }
}
