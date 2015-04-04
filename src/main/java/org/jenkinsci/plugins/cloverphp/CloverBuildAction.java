package org.jenkinsci.plugins.cloverphp;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import org.jenkinsci.plugins.cloverphp.results.ProjectCoverage;
import org.jenkinsci.plugins.cloverphp.targets.CoverageMetric;
import org.jenkinsci.plugins.cloverphp.targets.CoverageTarget;
import org.kohsuke.stapler.StaplerProxy;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jvnet.localizer.Localizable;

/**
 * A health reporter for the individual build page.
 *
 *
 * @author connollys
 * @since 03-Jul-2007 08:43:08
 */
public class CloverBuildAction implements HealthReportingAction, StaplerProxy {

    private final AbstractBuild owner;

    private String buildBaseDir;

    private CoverageTarget healthyTarget;

    private CoverageTarget unhealthyTarget;

    private transient WeakReference<ProjectCoverage> report;

    @Override
    public HealthReport getBuildHealth() {
        if (healthyTarget == null || unhealthyTarget == null) {
            return null;
        }
        ProjectCoverage projectCoverage = getResult();
        Map<CoverageMetric, Integer> scores = healthyTarget.getRangeScores(unhealthyTarget, projectCoverage);
        int minValue = 100;
        CoverageMetric minKey = null;
        for (Map.Entry<CoverageMetric, Integer> e : scores.entrySet()) {
            if (e.getValue() < minValue) {
                minKey = e.getKey();
                minValue = e.getValue();
            }
        }
        if (minKey == null) {
            return null;
        }

        Localizable description;
        switch (minKey) {
            case METHOD:
                description = Messages._CloverBuildAction_MethodCoverage(
                        projectCoverage.getMethodCoverage().getPercentage(),
                        projectCoverage.getMethodCoverage().toString());
                break;
            case STATEMENT:
                description = Messages._CloverBuildAction_StatementCoverage(
                        projectCoverage.getStatementCoverage().getPercentage(),
                        projectCoverage.getStatementCoverage().toString());
                break;
            case ELEMENT:
                description = Messages._CloverBuildAction_ElementCoverage(
                        projectCoverage.getElementCoverage().getPercentage(),
                        projectCoverage.getElementCoverage().toString());
                break;
            default:
                return null;
        }
        return new HealthReport(minValue, description);
    }

    @Override
    public String getIconFileName() {
        return CloverProjectAction.ICON;
    }

    @Override
    public String getDisplayName() {
        return Messages.CloverBuildAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "cloverphp-report";
    }

    @Override
    public Object getTarget() {
        return getResult();
    }

    public CloverBuildAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    /** Gets the previous {@link CloverBuildAction} of the given build. */
    /*package*/
    static CloverBuildAction getPreviousResult(AbstractBuild start) {
        AbstractBuild<?, ?> b = start;
        while (true) {
            b = b.getPreviousBuild();
            if (b == null) {
                return null;
            }
            if (b.getResult() == Result.FAILURE) {
                continue;
            }
            CloverBuildAction r = b.getAction(CloverBuildAction.class);
            if (r != null) {
                return r;
            }
        }
    }

    CloverBuildAction(AbstractBuild owner, String workspacePath, ProjectCoverage r, CoverageTarget healthyTarget,
            CoverageTarget unhealthyTarget) {
        this.owner = owner;
        this.report = new WeakReference<ProjectCoverage>(r);
        this.buildBaseDir = workspacePath;
        if (this.buildBaseDir == null) {
            this.buildBaseDir = File.separator;
        } else if (!this.buildBaseDir.endsWith(File.separator)) {
            this.buildBaseDir += File.separator;
        }
        this.healthyTarget = healthyTarget;
        this.unhealthyTarget = unhealthyTarget;
        r.setOwner(owner);
    }

    /** Obtains the detailed {@link CoverageReport} instance. */
    public synchronized ProjectCoverage getResult() {
        if (report != null) {
            ProjectCoverage r = report.get();
            if (r != null) {
                return r;
            }
        }

        File reportFile = CloverPHPPublisher.getCloverXmlReport(owner);
        try {
            ProjectCoverage r = CloverCoverageParser.parse(reportFile, buildBaseDir);
            r.setOwner(owner);
            report = new WeakReference<ProjectCoverage>(r);
            return r;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load " + reportFile, e);
            return null;
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(CloverBuildAction.class.getName());

    public static CloverBuildAction load(AbstractBuild<?, ?> build, String workspacePath, ProjectCoverage result,
            CoverageTarget healthyTarget, CoverageTarget unhealthyTarget) {
        return new CloverBuildAction(build, workspacePath, result, healthyTarget, unhealthyTarget);
    }
}
