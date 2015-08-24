package org.jenkinsci.plugins.cloverphp.targets;

import org.jenkinsci.plugins.cloverphp.results.AbstractClassMetrics;

import java.io.Serializable;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Holds the target coverage for a specific condition;
 *
 * @author Stephen Connolly
 * @since 1.1
 */
public class CoverageTarget implements Serializable {

    private static final int RANGE_MAX = 100;

    private Integer methodCoverage;

    private Integer statementCoverage;

    private Integer elementCoverage;

    public CoverageTarget() {
    }

    public CoverageTarget(Integer methodCoverage, Integer statementCoverage) {
        this.methodCoverage = methodCoverage;
        this.statementCoverage = statementCoverage;
        this.elementCoverage = null;
    }

    public boolean isAlwaysMet() {
        return (methodCoverage == null || methodCoverage < 0)
                && (statementCoverage == null || statementCoverage < 0)
                && (elementCoverage == null || elementCoverage < 0);
    }

    public boolean isEmpty() {
        return methodCoverage == null
                && statementCoverage == null
                && elementCoverage == null;
    }

    public Set<CoverageMetric> getFailingMetrics(AbstractClassMetrics coverage) {
        Set<CoverageMetric> result = new HashSet<CoverageMetric>();

        if (methodCoverage != null && coverage.getMethodCoverage().getPercentage() < methodCoverage) {
            result.add(CoverageMetric.METHOD);
        }

        if (statementCoverage != null && coverage.getStatementCoverage().getPercentage() < statementCoverage) {
            result.add(CoverageMetric.STATEMENT);
        }

        if (elementCoverage != null && coverage.getElementCoverage().getPercentage() < elementCoverage) {
            result.add(CoverageMetric.ELEMENT);
        }

        return result;
    }

    public Map<CoverageMetric, Integer> getRangeScores(CoverageTarget min, AbstractClassMetrics coverage) {
        Integer j;
        Map<CoverageMetric, Integer> result = new HashMap<CoverageMetric, Integer>();

        j = calcRangeScore(methodCoverage, min.methodCoverage, coverage.getMethodCoverage().getPercentage());
        result.put(CoverageMetric.METHOD, j);
        j = calcRangeScore(statementCoverage, min.statementCoverage, coverage.getStatementCoverage().getPercentage());
        result.put(CoverageMetric.STATEMENT, j);
        j = calcRangeScore(elementCoverage, min.elementCoverage, coverage.getElementCoverage().getPercentage());
        result.put(CoverageMetric.ELEMENT, j);
        return result;
    }

    private static int calcRangeScore(Integer max, Integer min, int value) {
        if (min == null || min < 0) {
            min = 0;
        }
        if (max == null || max > RANGE_MAX) {
            max = RANGE_MAX;
        }
        if (min > max) {
            min = max - 1;
        }
        int result = (int) (100f * (value - min.floatValue()) / (max.floatValue() - min.floatValue()));
        if (result < 0) {
            return 0;
        }
        if (result > RANGE_MAX) {
            return RANGE_MAX;
        }
        return result;
    }

    /**
     * Getter for property 'methodCoverage'.
     *
     * @return Value for property 'methodCoverage'.
     */
    public Integer getMethodCoverage() {
        return methodCoverage;
    }

    /**
     * Setter for property 'methodCoverage'.
     *
     * @param methodCoverage Value to set for property 'methodCoverage'.
     */
    public void setMethodCoverage(Integer methodCoverage) {
        this.methodCoverage = methodCoverage;
    }

    /**
     * Getter for property 'statementCoverage'.
     *
     * @return Value for property 'statementCoverage'.
     */
    public Integer getStatementCoverage() {
        return statementCoverage;
    }

    /**
     * Setter for property 'statementCoverage'.
     *
     * @param statementCoverage Value to set for property 'statementCoverage'.
     */
    public void setStatementCoverage(Integer statementCoverage) {
        this.statementCoverage = statementCoverage;
    }

    /**
     * Getter for property 'elementCoverage'.
     *
     * @return Value for property 'elementCoverage'.
     */
    public Integer getElementCoverage() {
        return elementCoverage;
    }

    /**
     * Setter for property 'elementCoverage'.
     *
     * @param elementCoverage Value to set for property 'elementCoverage'.
     */
    public void setElementCoverage(Integer elementCoverage) {
        this.elementCoverage = elementCoverage;
    }
}
