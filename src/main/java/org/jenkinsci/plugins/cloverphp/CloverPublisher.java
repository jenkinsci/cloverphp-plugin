package org.jenkinsci.plugins.cloverphp;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Project;
import hudson.model.Result;
import org.jenkinsci.plugins.cloverphp.results.ProjectCoverage;
import org.jenkinsci.plugins.cloverphp.targets.CoverageMetric;
import org.jenkinsci.plugins.cloverphp.targets.CoverageTarget;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Clover {@link Publisher}.
 *
 * @author Stephen Connolly
 */
public class CloverPublisher extends Recorder {

    private final boolean publishHtmlReport;
    
    private final String reportDir;

    private final String xmlLocation;

    private final boolean disableArchiving;

    private CoverageTarget healthyTarget;

    private CoverageTarget unhealthyTarget;

    private CoverageTarget failingTarget;

    /**
     * @param reportDir
     * @param xmlLocation
     * @stapler-constructor
     */
    @DataBoundConstructor
    public CloverPublisher(String xmlLocation, String publishHtmlReport, String reportDir, boolean disableArchiving) {
        this.publishHtmlReport = publishHtmlReport != null;
        this.reportDir = reportDir;
        this.xmlLocation = xmlLocation;
        this.disableArchiving = disableArchiving;
        this.healthyTarget = new CoverageTarget();
        this.unhealthyTarget = new CoverageTarget();
        this.failingTarget = new CoverageTarget();
    }

    public boolean isPublishHtmlReport() {
        return publishHtmlReport;
    }
    
    public String getReportDir() {
        return reportDir;
    }

    public String getXmlLocation() {
        return xmlLocation;
    }

    public boolean isDisableArchiving() {
        return disableArchiving;
    }

    /**
     * Getter for property 'healthyTarget'.
     *
     * @return Value for property 'healthyTarget'.
     */
    public CoverageTarget getHealthyTarget() {
        return healthyTarget;
    }

    /**
     * Setter for property 'healthyTarget'.
     *
     * @param healthyTarget Value to set for property 'healthyTarget'.
     */
    public void setHealthyTarget(CoverageTarget healthyTarget) {
        this.healthyTarget = healthyTarget;
    }

    /**
     * Getter for property 'unhealthyTarget'.
     *
     * @return Value for property 'unhealthyTarget'.
     */
    public CoverageTarget getUnhealthyTarget() {
        return unhealthyTarget;
    }

    /**
     * Setter for property 'unhealthyTarget'.
     *
     * @param unhealthyTarget Value to set for property 'unhealthyTarget'.
     */
    public void setUnhealthyTarget(CoverageTarget unhealthyTarget) {
        this.unhealthyTarget = unhealthyTarget;
    }

    /**
     * Getter for property 'failingTarget'.
     *
     * @return Value for property 'failingTarget'.
     */
    public CoverageTarget getFailingTarget() {
        return failingTarget;
    }

    /**
     * Setter for property 'failingTarget'.
     *
     * @param failingTarget Value to set for property 'failingTarget'.
     */
    public void setFailingTarget(CoverageTarget failingTarget) {
        this.failingTarget = failingTarget;
    }

    /**
     * Gets the directory where the Clover Report is stored for the given build.
     */
    /*package*/
    static File getCloverXmlReport(AbstractBuild<?, ?> build) {
        return new File(build.getRootDir(), "clover.xml");
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException {

        final File buildRootDir = build.getRootDir(); // should this top level?
        final FilePath buildTarget = new FilePath(buildRootDir);
        final FilePath workspace = build.getWorkspace();
        FilePath coverageReportDir = workspace.child(reportDir);
        try {
            listener.getLogger().println("Publishing Clover coverage report...");

            // if the build has failed, then there's not
            // much point in reporting an error
            final boolean buildFailure = build.getResult().isWorseOrEqualTo(Result.FAILURE);
            final boolean missingReport = !coverageReportDir.exists();

            if (buildFailure && missingReport) {
                listener.getLogger().println("No Clover report will be published due to a "
                        + (buildFailure ? "Build Failure" : "missing report"));
                return true;
            }

            if (isPublishHtmlReport() && !isDisableArchiving()) {
                final boolean htmlExists = copyHtmlReport(coverageReportDir, buildTarget, listener);
                if (htmlExists) {
                    // only add the HTML build action, if the HTML report is available
                    build.getActions().add(new CloverHtmlBuildAction(buildTarget));
                }
            }

            final boolean xmlExists = copyXmlReport(workspace, buildTarget, listener);
            processCloverXml(build, listener, coverageReportDir, buildTarget);

        } catch (IOException e) {
            Util.displayIOException(e, listener);
            e.printStackTrace(listener.fatalError("Unable to copy coverage from "
                    + coverageReportDir + " to " + buildTarget));
            build.setResult(Result.FAILURE);
        }


        return true;
    }

    /**
     * Process the clover.xml from the build directory. The clover.xml must have been already copied to the build dir.
     *
     */
    private void processCloverXml(AbstractBuild<?, ?> build, BuildListener listener,
            FilePath coverageReport, FilePath buildTarget) throws InterruptedException {
        String workspacePath = build.getWorkspace().getRemote();
        if (!workspacePath.endsWith(File.separator)) {
            workspacePath += File.separator;
        }

        final File cloverXmlReport = getCloverXmlReport(build);
        if (cloverXmlReport.exists()) {
            listener.getLogger().println("Publishing Clover coverage results...");
            ProjectCoverage result = null;
            try {
                result = CloverCoverageParser.parse(cloverXmlReport, workspacePath);
            } catch (IOException e) {
                Util.displayIOException(e, listener);
                e.printStackTrace(listener.fatalError("Unable to copy coverage from "
                        + coverageReport + " to " + buildTarget));
                build.setResult(Result.FAILURE);
            }
            final CloverBuildAction action = CloverBuildAction.load(build, workspacePath, result, healthyTarget, unhealthyTarget);

            build.getActions().add(action);
            Set<CoverageMetric> failingMetrics = failingTarget.getFailingMetrics(result);
            if (!failingMetrics.isEmpty()) {
                listener.getLogger().println("Code coverage enforcement failed for the following metrics:");
                for (CoverageMetric metric : failingMetrics) {
                    listener.getLogger().println("    " + metric);
                }
                listener.getLogger().println("Setting Build to unstable.");
                build.setResult(Result.UNSTABLE);
            }

        } else {
            flagMissingCloverXml(listener, build);
        }
    }

    private boolean copyXmlReport(FilePath workspace, FilePath buildTarget, BuildListener listener)
            throws IOException, InterruptedException {
        // check one directory deep for a clover.xml, if there is not one in the coverageReport dir already
        // the clover auto-integration saves clover reports in: clover/${ant.project.name}/clover.xml
        final FilePath cloverXmlPath = workspace.child(getXmlLocation());
        final FilePath toFile = buildTarget.child("clover.xml");
        if (cloverXmlPath.exists()) {
            listener.getLogger().println("Publishing Clover XML report...");
            cloverXmlPath.copyTo(toFile);
            return true;
        }
        listener.getLogger().println("Clover xml file does not exist in: " + workspace
                + " called: " + getXmlLocation()
                + " and will not be copied to: " + toFile);
        return false;

    }

    private boolean copyHtmlReport(FilePath coverageReportDir, FilePath buildTarget, BuildListener listener)
            throws IOException, InterruptedException {
        // Copy the HTML coverage report
        final FilePath htmlIndexHtmlPath = coverageReportDir.child("index.html");
        if (htmlIndexHtmlPath.exists()) {
            final FilePath htmlDirPath = htmlIndexHtmlPath.getParent();
            listener.getLogger().println("Publishing Clover HTML report...");
            htmlDirPath.copyRecursiveTo("**/*", buildTarget);
            return true;
        }
        return false;
    }

    private void flagMissingCloverXml(BuildListener listener, AbstractBuild<?, ?> build) {
        listener.getLogger().println("Could not find '" + reportDir + "/" + getXmlLocation()
                + "'.  Did you generate " + "the XML report for Clover?");
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        if (project instanceof Project) {
            return new CloverProjectAction((Project) project, this);
        }
        return null;
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        // see Descriptor javadoc for more about what a descriptor is.
        return DESCRIPTOR;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Descriptor should be singleton.
     */
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * Descriptor for {@link CloverPublisher}. Used as a singleton. The class is marked as public so that it can be
     * accessed from views.
     * <p/>
     * <p/>
     * See <tt>views/hudson/plugins/clover/CloverPublisher/*.jelly</tt> for the actual HTML fragment for the
     * configuration screen.
     */
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        DescriptorImpl() {
            super(CloverPublisher.class);
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return Messages.CloverPublisher_DisplayName();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindParameters(this, "clover.");
            save();
            return super.configure(req, formData);
        }

        /**
         * Creates a new instance of {@link CloverPublisher} from a submitted form.
         */
        @Override
        public CloverPublisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            CloverPublisher instance = req.bindParameters(CloverPublisher.class, "clover.");
            req.bindParameters(instance.failingTarget, "cloverFailingTarget.");
            req.bindParameters(instance.healthyTarget, "cloverHealthyTarget.");
            req.bindParameters(instance.unhealthyTarget, "cloverUnhealthyTarget.");
            // start ugly hack
            if (instance.healthyTarget.isEmpty()) {
                instance.healthyTarget = new CoverageTarget(70, 80);
            }
            // end ugly hack
            return instance;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return FreeStyleProject.class.equals(jobType) || MatrixProject.class.equals(jobType);
        }
        
        public FormValidation doRequiredCheck(@QueryParameter String value) {
            String v = Util.fixEmptyAndTrim(value);
            return FormValidation.validateRequired(v);
        }
    }
}