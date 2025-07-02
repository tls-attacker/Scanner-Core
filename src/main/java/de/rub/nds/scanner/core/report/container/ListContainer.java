/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.container;

import de.rub.nds.scanner.core.config.ScannerDetail;
import java.util.LinkedList;
import java.util.List;

/**
 * Container for managing a list of report containers. Supports depth increase for nested
 * indentation of child containers.
 */
public class ListContainer extends ReportContainer {

    private final List<ReportContainer> reportContainerList;

    private final int depthIncrease;

    /** Creates a new empty ListContainer with no depth increase. */
    public ListContainer() {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = new LinkedList<>();
        this.depthIncrease = 0;
    }

    /**
     * Creates a new empty ListContainer with specified depth increase.
     *
     * @param depthIncrease The amount to increase depth when printing child containers
     */
    public ListContainer(int depthIncrease) {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = new LinkedList<>();
        this.depthIncrease = depthIncrease;
    }

    /**
     * Creates a new ListContainer with existing containers and specified depth increase.
     *
     * @param reportContainerList The initial list of report containers
     * @param depthIncrease The amount to increase depth when printing child containers
     */
    public ListContainer(List<ReportContainer> reportContainerList, int depthIncrease) {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = reportContainerList;
        this.depthIncrease = depthIncrease;
    }

    /**
     * Creates a new ListContainer with existing containers and no depth increase.
     *
     * @param reportContainerList The initial list of report containers
     */
    public ListContainer(List<ReportContainer> reportContainerList) {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = reportContainerList;
        this.depthIncrease = 0;
    }

    /**
     * Prints all contained report containers to the provided StringBuilder. Each container is
     * printed with the specified depth plus any depth increase.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        reportContainerList.forEach(
                container -> container.print(builder, depth + depthIncrease, useColor));
    }

    /**
     * Adds a report container to this list.
     *
     * @param container The container to add
     * @return This ListContainer instance for method chaining
     */
    public ListContainer add(ReportContainer container) {
        this.reportContainerList.add(container);
        return this;
    }
}
