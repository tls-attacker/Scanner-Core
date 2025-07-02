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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Container for displaying tabular data in scanner reports. Supports automatic column alignment and
 * formatting with headers.
 */
public class TableContainer extends ReportContainer {

    private List<TextContainer> headlineList;

    private List<List<TextContainer>> containerTable;

    private int depthIncrease;

    /** Creates a new empty TableContainer with normal detail level and no depth increase. */
    public TableContainer() {
        super(ScannerDetail.NORMAL);
        this.depthIncrease = 0;
        this.containerTable = new LinkedList<>();
    }

    /**
     * Creates a new empty TableContainer with specified detail level.
     *
     * @param detail The detail level for this container
     */
    public TableContainer(ScannerDetail detail) {
        super(detail);
        this.depthIncrease = 0;
        this.containerTable = new LinkedList<>();
    }

    /**
     * Creates a new empty TableContainer with specified depth increase.
     *
     * @param depthIncrease The amount to increase depth when printing
     */
    public TableContainer(int depthIncrease) {
        super(ScannerDetail.NORMAL);
        this.depthIncrease = depthIncrease;
        this.containerTable = new LinkedList<>();
    }

    /**
     * Creates a new empty TableContainer with specified detail level and depth increase.
     *
     * @param detail The detail level for this container
     * @param depthIncrease The amount to increase depth when printing
     */
    public TableContainer(ScannerDetail detail, int depthIncrease) {
        super(detail);
        this.depthIncrease = depthIncrease;
        this.containerTable = new LinkedList<>();
    }

    /**
     * Prints the table to the provided StringBuilder with a trailing newline.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    @Override
    public void print(StringBuilder builder, int depth, boolean useColor) {
        println(builder, depth, useColor);
        builder.append("\n");
    }

    /**
     * Prints the table to the provided StringBuilder without a trailing newline. Includes headers,
     * separator line, and all data rows with proper column alignment.
     *
     * @param builder The StringBuilder to append the output to
     * @param depth The indentation depth level
     * @param useColor Whether to use ANSI color codes in the output
     */
    public void println(StringBuilder builder, int depth, boolean useColor) {
        List<Integer> paddings = getColumnPaddings();
        printTableLine(headlineList, paddings, builder, depth, useColor);
        printStripline(paddings, builder, depth);
        for (List<TextContainer> containerLine : containerTable) {
            printTableLine(containerLine, paddings, builder, depth, useColor);
        }
    }

    /**
     * Calculates the padding for each column of the table. Needed to properly align table entries.
     *
     * @return padding A list containing the paddings for each column.
     */
    private List<Integer> getColumnPaddings() {
        List<Integer> paddings = new ArrayList<>(headlineList.size());
        for (int i = 0; i < headlineList.size(); i++) {
            paddings.add(i, headlineList.get(i).getText().length());
        }
        for (List<TextContainer> line : containerTable) {
            for (int i = 0; i < line.size(); i++) {
                paddings.set(i, Math.max(paddings.get(i), line.get(i).getText().length()));
            }
        }
        return paddings;
    }

    private static void pad(StringBuilder builder, int n) {
        builder.append(" ".repeat(Math.max(0, n)));
    }

    private void printTableLine(
            List<TextContainer> line,
            List<Integer> paddings,
            StringBuilder builder,
            int depth,
            boolean useColor) {
        addDepth(builder, depth);
        for (int i = 0; i < line.size(); i++) {
            TextContainer container = line.get(i);
            int paddingSpaces = paddings.get(i) - container.getText().length();
            pad(builder, paddingSpaces);
            container.println(builder, 0, useColor);
            builder.append(" | ");
        }
        builder.append("\n");
    }

    private void printStripline(List<Integer> paddings, StringBuilder builder, int depth) {
        addDepth(builder, depth);
        for (Integer padding : paddings) {
            builder.append("-".repeat(padding));
            builder.append(" | ");
        }
        builder.append("\n");
    }

    /**
     * Adds a new row to the table.
     *
     * @param line A list of TextContainers representing the cells in the row
     */
    public void addLineToTable(List<TextContainer> line) {
        this.containerTable.add(line);
    }

    /**
     * Gets the list of table headers.
     *
     * @return The list of TextContainers representing the table headers
     */
    public List<TextContainer> getHeadlineList() {
        return headlineList;
    }

    /**
     * Sets the list of table headers.
     *
     * @param headlineList The list of TextContainers representing the table headers
     */
    public void setHeadlineList(List<TextContainer> headlineList) {
        this.headlineList = headlineList;
    }

    /**
     * Gets the table data as a list of rows.
     *
     * @return The list of rows, where each row is a list of TextContainers
     */
    public List<List<TextContainer>> getContainerTable() {
        return containerTable;
    }

    /**
     * Sets the table data.
     *
     * @param containerTable The list of rows, where each row is a list of TextContainers
     */
    public void setContainerTable(List<List<TextContainer>> containerTable) {
        this.containerTable = containerTable;
    }

    /**
     * Gets the depth increase value.
     *
     * @return The amount of depth increase when printing
     */
    public int getDepthIncrease() {
        return depthIncrease;
    }

    /**
     * Sets the depth increase value.
     *
     * @param depthIncrease The amount of depth increase when printing
     */
    public void setDepthIncrease(int depthIncrease) {
        this.depthIncrease = depthIncrease;
    }
}
