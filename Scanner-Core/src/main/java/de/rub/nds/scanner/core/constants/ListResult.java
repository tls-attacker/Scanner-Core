/**
 * Scanner-Core - A TLS configuration and analysis tool based on TLS-Attacker
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.scanner.core.constants;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListResult<T> implements TestResult {

    private String name = "ListResult";
    private final List<T> list;

    public ListResult(List<T> list) {
        this.list = list;
    }

    public ListResult(List<T> list, String name) {
        this.list = list;
        this.name = name;
    }

    public List<T> getList() {
        return this.list;
    }

    @Override
    public String name() {
        return this.name;
    }
}
