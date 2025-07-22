/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline.testutil;

import de.rub.nds.scanner.core.guideline.FailedCheckGuidelineResult;
import de.rub.nds.scanner.core.guideline.GuidelineAdherence;
import de.rub.nds.scanner.core.guideline.GuidelineCheck;
import de.rub.nds.scanner.core.guideline.GuidelineCheckResult;
import de.rub.nds.scanner.core.guideline.RequirementLevel;
import de.rub.nds.scanner.core.report.ScanReport;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ioTestGuidelineCheck")
@XmlType(name = "ioTestGuidelineCheckType")
public class IOTestGuidelineCheck extends GuidelineCheck {
    // Public constructor for JAXB
    public IOTestGuidelineCheck() {
        super("TestCheck", RequirementLevel.MUST);
    }

    public IOTestGuidelineCheck(String name, RequirementLevel level) {
        super(name, level);
    }

    @Override
    public <ReportT extends ScanReport> GuidelineCheckResult evaluate(ReportT report) {
        return new FailedCheckGuidelineResult(this, GuidelineAdherence.ADHERED);
    }
}
