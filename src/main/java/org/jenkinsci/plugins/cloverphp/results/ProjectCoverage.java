package org.jenkinsci.plugins.cloverphp.results;

import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.cloverphp.CloverBuildAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Clover Coverage results for the entire project.
 *
 * @author Stephen Connolly
 * @author Seiji Sogabe
 */
public class ProjectCoverage extends AbstractProjectMetrics {

    private List<FileCoverage> fileCoverages = new ArrayList<FileCoverage>();

    public List<FileCoverage> getChildren() {
        return getFileCoverages();
    }

    public boolean addFileCoverage(FileCoverage result) {
        result.setParent(this);
        return fileCoverages.add(result);
    }

    public List<FileCoverage> getFileCoverages() {
        return fileCoverages;
    }

    public FileCoverage findFileCoverage(String token) {
        for (FileCoverage i : fileCoverages) {
            if (token.equals(i.getURLSafeName())) {
                return i;
            }
        }
        return null;
    }

    public FileCoverage getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
        return findFileCoverage(token);
    }

    @Override
    public AbstractClassMetrics getPreviousResult() {
        CloverBuildAction action = getPreviousCloverBuildAction();
        if (action == null) {
            return null;
        }
        return action.getResult();
    }

    @Override
    public void setOwner(AbstractBuild owner) {
        super.setOwner(owner);
        for (FileCoverage p : fileCoverages) {
            p.setOwner(owner);
        }
    }
}
