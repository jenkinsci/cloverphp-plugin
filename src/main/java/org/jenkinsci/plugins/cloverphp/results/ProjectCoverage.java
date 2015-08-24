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
    private List<PackageCoverage> packageCoverages = new ArrayList<PackageCoverage>();

    public List<FileCoverage> getChildren() {
        return getFileCoverages();
    }

    public List<PackageCoverage> getPackageCoverages() {
        return packageCoverages;
    }

    public boolean addFileCoverage(FileCoverage result) {
        result.setParent(this);
        return fileCoverages.add(result);
    }

    public boolean addPackageCoverage(PackageCoverage result) {
        result.setParent(this);
        return packageCoverages.add(result);
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
    
    public PackageCoverage findPackageCoverage(String token) {
        for (PackageCoverage i : packageCoverages) {
            if (token.equals(i.getURLSafeName())) {
                return i;
            }
        }
        return null;
    }

    /**
     * Method to expose subpages to the Stapler HTTP server.
     * It tries from the list of files first and then tries from the list of packages.
     *
     * @param token
     * @param req
     * @param rsp
     * @return AbstractClassMetrics can be any of FileCoverage or PackageCoverage
     * @throws IOException
     */
    public AbstractClassMetrics getDynamic(String token, StaplerRequest req, StaplerResponse rsp) throws IOException {
        AbstractClassMetrics candidate = findFileCoverage(token);
        if(candidate != null) {
            return candidate;
        }

        candidate = findPackageCoverage(token);
        return candidate;
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
        for (PackageCoverage p: packageCoverages) {
            p.setOwner(owner);
        }
    }

    @Override
    public String getName() {
        String name = super.getName();
        if(name == null && getOwner() != null) {
            name = getOwner().getDisplayName();
        }
        return name;
    }
}
