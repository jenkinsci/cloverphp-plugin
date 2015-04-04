package org.jenkinsci.plugins.cloverphp;

import org.jenkinsci.plugins.cloverphp.results.ClassCoverage;
import org.jenkinsci.plugins.cloverphp.results.FileCoverage;
import org.jenkinsci.plugins.cloverphp.results.ProjectCoverage;
import hudson.util.IOException2;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since 03-Jul-2007 09:03:30
 */
public final class CloverCoverageParser {

    /**
     * Do not instantiate CloverCoverageParser.
     */
    private CloverCoverageParser() {
    }

    public static ProjectCoverage trimPaths(ProjectCoverage result, String pathPrefix) {
        if (result == null) {
            throw new NullPointerException();
        }
        if (pathPrefix == null) {
            return result;
        }
        for (FileCoverage f : result.getFileCoverages()) {
            if (f.getName().startsWith(pathPrefix)) {
                f.setName(f.getName().substring(pathPrefix.length()));
            }
            f.setName(f.getName().replace('\\', '/'));
        }
        return result;
    }

    public static ProjectCoverage parse(File inFile, String pathPrefix) throws IOException {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(inFile);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            CloverCoverageParser parser = new CloverCoverageParser();
            ProjectCoverage pc = trimPaths(parse(bufferedInputStream), pathPrefix);
            pc.setName(Messages.CloverCoverageParser_ProjectName());
            return pc;
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static ProjectCoverage parse(InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException();
        }
        Digester digester = new Digester();
        digester.setClassLoader(CloverCoverageParser.class.getClassLoader());
        digester.addObjectCreate("coverage/project", ProjectCoverage.class);
        digester.addSetProperties("coverage/project");
        digester.addSetProperties("coverage/project/metrics");

        digester.addObjectCreate("coverage/project/file", FileCoverage.class);
        digester.addSetProperties("coverage/project/file");
        digester.addSetProperties("coverage/project/file/metrics");
        digester.addSetNext("coverage/project/file", "addFileCoverage", FileCoverage.class.getName());

        digester.addObjectCreate("coverage/project/file/class", ClassCoverage.class);
        digester.addSetProperties("coverage/project/file/class");
        digester.addSetProperties("coverage/project/file/class/metrics");
        digester.addSetNext("coverage/project/file/class", "addClassCoverage", ClassCoverage.class.getName());

        try {
            ProjectCoverage coverage = (ProjectCoverage) digester.parse(in);
            if (coverage == null) {
                throw new IOException("coverage report seems to be incompatible with clover style.");
            }
            return coverage;
        } catch (SAXException e) {
            throw new IOException2("Cannot parse coverage results", e);
        }
    }
}
