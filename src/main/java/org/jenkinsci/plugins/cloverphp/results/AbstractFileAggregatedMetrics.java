package org.jenkinsci.plugins.cloverphp.results;

/**
 * Clover Coverage results for multiple files.
 * @author Stephen Connolly
 */
public abstract class AbstractFileAggregatedMetrics extends AbstractClassAggregatedMetrics {

    private int files;

    /** {@inheritDoc} */
    public int getFiles() {
        return files;
    }

    /** {@inheritDoc} */
    public void setFiles(int files) {
        this.files = files;
    }
}
