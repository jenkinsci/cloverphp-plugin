package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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
    
    public BaseCoverage getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
        return findChild(token);
    }

    @Override
    public BaseCoverage getPreviousResult() {
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
    public void setOwner(AbstractBuild owner) {
        super.setOwner(owner);    
        for (BaseCoverage child : getChildren()) {
            child.setOwner(owner);
        }
    }
}
