package org.jenkinsci.plugins.cloverphp;

import org.apache.commons.digester3.Digester;
import org.jenkinsci.plugins.cloverphp.results.ClassCoverage;
import org.jenkinsci.plugins.cloverphp.results.FileCoverage;
import org.jenkinsci.plugins.cloverphp.results.PackageCoverage;
import org.jenkinsci.plugins.cloverphp.results.ProjectCoverage;
import hudson.util.IOException2;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

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
        trimPathPrefix(pathPrefix, result.getFileCoverages());

        for(PackageCoverage pc : result.getPackageCoverages()) {
            trimPathPrefix(pathPrefix, pc.getFileCoverages());
        }

        return result;
    }

    protected static void trimPathPrefix(String pathPrefix, List<FileCoverage> fileCoverages) {
        for (FileCoverage f : fileCoverages) {
            if (f.getName().startsWith(pathPrefix)) {
                f.setName(f.getName().substring(pathPrefix.length()));
            }
            f.setName(f.getName().replace('\\', '/'));
        }
    }

    public static ProjectCoverage parse(File inFile, String pathPrefix) throws IOException, SAXException {
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

    public static ProjectCoverage parse(InputStream in) throws IOException, SAXException {
        if (in == null) {
            throw new NullPointerException();
        }
        Digester digester = buildDigester();

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

    protected static Digester buildDigester() throws SAXException {
        Digester digester = new Digester();
        if (!Boolean.getBoolean(CloverCoverageParser.class.getName() + ".UNSAFE")) {
            digester.setXIncludeAware(false);
            try {
                digester.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                digester.setFeature("http://xml.org/sax/features/external-general-entities", false);
                digester.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                digester.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (ParserConfigurationException ex) {
                throw new SAXException("Failed to securely configure xml digester parser", ex);
            }
        }
        digester.setClassLoader(CloverCoverageParser.class.getClassLoader());

        addDigester(digester, "coverage/project", ProjectCoverage.class);
        addDigester(digester, "coverage/project/file", FileCoverage.class, "addFileCoverage");
        addDigester(digester, "coverage/project/file/class", ClassCoverage.class, "addClassCoverage");

        addDigester(digester, "coverage/project/package", PackageCoverage.class, "addPackageCoverage");
        addDigester(digester, "coverage/project/package/file", FileCoverage.class, "addFileCoverage");
        addDigester(digester, "coverage/project/package/file/class", ClassCoverage.class, "addClassCoverage");

        return digester;
    }

    private static void addDigester(Digester digester, String path, Class _class) {
        addDigester(digester, path, _class, null);
    }

    private static void addDigester(Digester digester, String path, Class _class, String next) {
        digester.addObjectCreate(path, _class);
        digester.addSetProperties(path);
        digester.addSetProperties(path + "/metrics");
        if(next != null) {
            digester.addSetNext(path, next, _class.getName());
        }
    }
}
