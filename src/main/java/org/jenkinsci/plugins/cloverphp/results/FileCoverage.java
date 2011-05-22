package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Clover Coverage results for a specific file.
 * @author Stephen Connolly
 */
public class FileCoverage extends AbstractFileMetrics {

    private List<ClassCoverage> classCoverages = new ArrayList<ClassCoverage>();

    public List<ClassCoverage> getChildren() {
        return getClassCoverages();
    }

    public ClassCoverage getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
        return findClassCoverage(token);
    }

    public boolean addClassCoverage(ClassCoverage result) {
        result.setParent(this);
        return classCoverages.add(result);
    }

    public List<ClassCoverage> getClassCoverages() {
        return classCoverages;
    }

    public ClassCoverage findClassCoverage(String token) {
        for (ClassCoverage i : classCoverages) {
            if (token.equals(i.getURLSafeName())) {
                return i;
            }
        }
        return null;
    }

    public AbstractClassMetrics getPreviousResult() {
        CloverBuildAction action = getPreviousCloverBuildAction();
        if (action == null) {
            return null;
        }
        ProjectCoverage projectCoverage = action.getResult();
        if (projectCoverage == null) {
            return null;
        }
        return projectCoverage.findFileCoverage(getURLSafeName());
    }

    @Override
    public void setOwner(AbstractBuild owner) {
        super.setOwner(owner);    //To change body of overridden methods use File | Settings | File Templates.
        for (ClassCoverage classCoverage : classCoverages) {
            classCoverage.setOwner(owner);
        }
    }
}
