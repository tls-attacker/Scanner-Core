/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.probe.result;

import java.io.Serializable;

public class DetailedResult<T extends Serializable> implements SummarizableTestResult {

    public static <T extends Serializable> DetailedResult<T> TRUE() {
        return new DetailedResult<>(TestResults.TRUE);
    }

    public static <T extends Serializable> DetailedResult<T> TRUE(T details) {
        return new DetailedResult<>(TestResults.TRUE, details);
    }

    public static <T extends Serializable> DetailedResult<T> FALSE() {
        return new DetailedResult<>(TestResults.FALSE);
    }

    public static <T extends Serializable> DetailedResult<T> FALSE(T details) {
        return new DetailedResult<>(TestResults.FALSE, details);
    }

    private final T details;
    private final TestResults summary;

    public DetailedResult(TestResults summary, T details) {
        this.details = details;
        this.summary = summary;
    }

    public DetailedResult(TestResults summary) {
        this(summary, null);
    }

    public T getDetails() {
        return details;
    }

    @Override
    public TestResults getSummarizedResult() {
        return summary;
    }

    @Override
    public boolean isExplicitSummary() {
        return true;
    }

    @Override
    public String toString() {
        return "" + summary + ", " + details;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        result = prime * result + ((summary == null) ? 0 : summary.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DetailedResult other = (DetailedResult) obj;
        if (details == null) {
            if (other.details != null)
                return false;
        } else if (!details.equals(other.details))
            return false;
        if (summary != other.summary)
            return false;
        return true;
    }

}
