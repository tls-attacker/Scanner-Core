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
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuidelineChecker<ReportT extends ScanReport> {

    protected static final Logger LOGGER = LogManager.getLogger();

    private final Guideline guideline;

    public GuidelineChecker(Guideline guideline) {
        this.guideline = guideline;
    }

    /**
     * Evaluates all checks in the guideline against the provided report and adds the results to the
     * report.
     *
     * @param report the scan report to evaluate and fill with guideline results
     */
    public void fillReport(ReportT report) {
        List<GuidelineCheckResult> results = new ArrayList<>();
        for (GuidelineCheck check : guideline.getChecks()) {
            GuidelineCheckResult result;
            if (!check.passesCondition(report)) {
                result =
                        new MissingRequirementGuidelineResult(
                                check.getName(),
                                GuidelineAdherence.CONDITION_NOT_MET,
                                "Condition was not met => Check is skipped.");
            } else {
                try {
                    result = check.evaluate(report);
                } catch (Throwable throwable) {
                    LOGGER.debug("Failed evaluating check: ", throwable);
                    result =
                            new FailedCheckGuidelineResult(
                                    check.getName(),
                                    GuidelineAdherence.CHECK_FAILED,
                                    throwable.getLocalizedMessage());
                }
            }
            results.add(result);
        }
        report.addGuidelineReport(
                new GuidelineReport(this.guideline.getName(), this.guideline.getLink(), results));
    }
}
