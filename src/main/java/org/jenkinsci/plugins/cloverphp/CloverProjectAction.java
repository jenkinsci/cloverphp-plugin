package org.jenkinsci.plugins.cloverphp;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Project;
import hudson.model.ProminentProjectAction;
import hudson.model.Build;
import hudson.model.Result;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Actionable;
import hudson.model.Failure;
import hudson.util.Graph;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Project level action.
 *
 * TODO: refactor this action in a similar manner to JavadocArchiver and BaseJavadocAction etc to avoid duplication.
 *
 * @author Stephen Connolly
 */
public class CloverProjectAction extends Actionable implements ProminentProjectAction {

    static final String ICON = "/plugin/cloverphp/clover_48x48.png";

    private final Project<?, ?> project;
    
    private final CloverPublisher publisher;

    public CloverProjectAction(Project project, CloverPublisher publisher) {
        this.project = project;
        this.publisher = publisher;
    }

    public String getUrlName() {
        return "cloverphp";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public String getIconFileName() {
        if (publisher.isPublishHtmlReport()) {
            FilePath r = getWorkspaceReportDir();
            if (exists(r, "index.html")) {
                return ICON;
            }
        }
        File reportDir = getLastBuildReportDir();
        if (reportDir != null && exists(new FilePath(reportDir), "clover.xml")) {
            return ICON;
        }
        return null;
    }

    public String getDisplayName() {
        if (publisher.isPublishHtmlReport()) {
            FilePath r = getWorkspaceReportDir();
            if (exists(r, "index.html")) {
                return Messages.CloverProjectAction_HTML_DisplayName();
            }
        }
        final File reportDir = getLastBuildReportDir();
        if (exists(new FilePath(reportDir), "clover.xml")) {
            return Messages.CloverProjectAction_XML_DisplayName();
        }
        return null;
    }

    /**
     * Returns the last Result that was successful.
     *
     * WARNING: this method is invoked dynamically from CloverProjectAction/floatingBox.jelly
     * @return the last successful build result
     */
    public CloverBuildAction getLastSuccessfulResult() {
        for (Build<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            if (b.getResult() == Result.FAILURE) {
                continue;
            }
            CloverBuildAction r = b.getAction(CloverBuildAction.class);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    public Graph getTrendGraph() {
        CloverBuildAction action = getLastSuccessfulResult();
        if (action != null) {
            return action.getResult().getTrendGraph();
        }
        return null;
    }

    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException,
            InterruptedException {

        if (publisher.isPublishHtmlReport()) {
            FilePath r = getWorkspaceReportDir();
            if (exists(r, "index.html")) {
                return new DirectoryBrowserSupport(
                        this, r, "Clover Html Report", "/cloverphp/clover.gif", false);
            }
        }

        File reportDir = getLastBuildReportDir();
        if (reportDir == null || getDisplayName() == null) {
            throw new Failure(Messages.CloverProjectAction_InvalidConfiguration());
        }
        if (new File(reportDir, "clover.xml").exists()) {
            if (project.getLastBuild() != null) {
                int buildNumber = project.getLastBuild().getNumber();
                rsp.sendRedirect2("../" + buildNumber + "/cloverphp-report");
            }
        }
        
        throw new Failure(Messages.CloverProjectAction_HTML_NoCloverReportFound());
    }

    private boolean exists(FilePath base, String file) {
        if (base == null) {
            return false;
        }
        try {
            return base.child(file).exists();
        } catch (IOException ex) {
            LOGGER.warning(ex.getMessage());
        } catch (InterruptedException ex) {
            LOGGER.warning(ex.getMessage());
        }
        return false;
    } 
    
    private FilePath getWorkspaceReportDir() {
        AbstractBuild<?, ?> lb = project.getLastBuild();
        if (lb == null) {
            return null;
        }
        FilePath workspace = lb.getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.child(publisher.getReportDir());
    }
    
    private File getLastBuildReportDir() {
        if (project.getLastBuild() == null) {
            // no clover report links, until there is at least one build
            return null;
        }
        final File reportDir = project.getLastBuild().getRootDir();
        return reportDir;
    }
    
    private static final Logger LOGGER = Logger.getLogger(CloverProjectAction.class.getName());
}
