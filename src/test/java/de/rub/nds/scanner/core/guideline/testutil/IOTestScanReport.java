/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.guideline.testutil;

import de.rub.nds.scanner.core.report.ScanReport;

public class IOTestScanReport extends ScanReport {
    @Override
    public void serializeToJson(java.io.OutputStream outputStream) {
        // Test implementation - do nothing
    }

    @Override
    public String getRemoteName() {
        return "TestRemote";
    }
}
