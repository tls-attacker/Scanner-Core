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

public class ListContainer extends ReportContainer {

    private final List<ReportContainer> reportContainerList;

    private final int depthIncrease;

    public ListContainer() {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = new LinkedList<>();
        this.depthIncrease = 0;
    }

    public ListContainer(int depthIncrease) {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = new LinkedList<>();
        this.depthIncrease = depthIncrease;
    }

    public ListContainer(List<ReportContainer> reportContainerList, int depthIncrease) {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = reportContainerList;
        this.depthIncrease = depthIncrease;
    }

    public ListContainer(List<ReportContainer> reportContainerList) {
        super(ScannerDetail.NORMAL);
        this.reportContainerList = reportContainerList;
        this.depthIncrease = 0;
    }

    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        reportContainerList.forEach(
                container -> container.print(builder, depth + depthIncrease, useColor));
    }

    public ListContainer add(ReportContainer container) {
        this.reportContainerList.add(container);
        return this;
    }
}
