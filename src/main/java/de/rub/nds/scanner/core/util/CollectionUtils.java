/*
 * Scanner Core - A modular framework for probe definition, execution, and result analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    public static <T> Set<T> mergeCollectionsIntoSet(Collection<T>... collections) {
        Set<T> mergeResult = new HashSet<>();
        for (Collection<T> currentCollection : collections) {
            if (currentCollection == null) {
                continue;
            }

            mergeResult.addAll(currentCollection);
        }
        return mergeResult;
    }
}
