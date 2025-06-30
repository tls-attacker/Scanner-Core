/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
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
    private transient List<GuidelineCheck<ReportT>> checks;

    /** Private no-arg constructor to please JAXB */
    @SuppressWarnings("unused")
    private Guideline() {}

    public Guideline(String name, String link, List<GuidelineCheck<ReportT>> checks) {
        this.name = name;
        this.link = link;
        this.checks = new ArrayList<>(checks);
    }

    /**
     * Gets the name of this guideline.
     *
     * @return the guideline name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this guideline.
     *
     * @param name the guideline name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the link (URL) to the guideline documentation.
     *
     * @return the guideline documentation link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the link (URL) to the guideline documentation.
     *
     * @param link the guideline documentation link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets an unmodifiable list of all checks associated with this guideline.
     *
     * @return an unmodifiable list of guideline checks
     */
    public List<GuidelineCheck<ReportT>> getChecks() {
        return checks != null ? Collections.unmodifiableList(checks) : Collections.emptyList();
    }

    /**
     * Adds a new check to this guideline.
     *
     * @param check the guideline check to add
     */
    public void addCheck(GuidelineCheck<ReportT> check) {
        if (checks == null) {
            checks = new ArrayList<>();
        }
        checks.add(check);
    }
}
