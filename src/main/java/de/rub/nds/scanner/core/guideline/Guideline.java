/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline;

import de.rub.nds.scanner.core.report.ScanReport;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "guideline")
@XmlType(propOrder = {"name", "link", "checks"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Guideline<ReportT extends ScanReport> implements Serializable {

    private String name;
    private String link;

    @XmlAnyElement(lax = true)
    private List<GuidelineCheck<ReportT>> checks;

    private Guideline() {}

    public Guideline(String name, String link, List<GuidelineCheck<ReportT>> checks) {
        this.name = name;
        this.link = link;
        this.checks = new ArrayList<>(checks);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<GuidelineCheck<ReportT>> getChecks() {
        return Collections.unmodifiableList(checks);
    }

    public void addCheck(GuidelineCheck<ReportT> check) {
        checks.add(check);
    }
}
