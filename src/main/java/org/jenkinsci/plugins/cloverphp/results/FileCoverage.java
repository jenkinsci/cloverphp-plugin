package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Clover Coverage results for a specific file.
 *
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

    @Override
    public AbstractClassMetrics getPreviousResult() {
        CloverBuildAction action = getPreviousCloverBuildAction();
        if (action == null) {
            return null;
        }
        ProjectCoverage projectCoverage = action.getResult();
        if (projectCoverage == null) {
            return null;
        }
        FileCoverage fileCoverage = null;
        if(getParent() instanceof PackageCoverage) {
            PackageCoverage packageCoverage = projectCoverage.findPackageCoverage(getParent().getURLSafeName());
            if (packageCoverage != null) {
                fileCoverage = packageCoverage.findFileCoverage(getURLSafeName());
            }
        }
        if (fileCoverage == null) {
            fileCoverage = projectCoverage.findFileCoverage(getURLSafeName());
        }
        return fileCoverage;
    }

    @Override
    public void setOwner(Run owner) {
        super.setOwner(owner);
        //To change body of overridden methods use File | Settings | File Templates.
        for (ClassCoverage classCoverage : classCoverages) {
            classCoverage.setOwner(owner);
        }
    }
}
