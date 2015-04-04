package org.jenkinsci.plugins.cloverphp.results;

/**
 * Clover Coverage results for multiple files.
 *
 * @author Stephen Connolly
 */
public abstract class AbstractProjectMetrics extends AbstractFileMetrics {

    private int files;

    /**
     * {@inheritDoc}
     */
    public int getFiles() {
        return files;
    }

    /**
     * {@inheritDoc}
     */
    public void setFiles(int files) {
        this.files = files;
    }
}
