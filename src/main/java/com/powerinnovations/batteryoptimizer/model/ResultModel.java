package com.powerinnovations.batteryoptimizer.model;

import java.text.DecimalFormat;
import javax.swing.table.*;
import java.util.*;

/**
 * This class is the model for the jTable in the Results JFrame. It displays the detailed Pack
 * collection.
 *
 * @author robbi.mount
 * @version 1.0 June 2016
 */
public class ResultModel extends AbstractTableModel {

    private final List<Pack> packs;
    private final DecimalFormat df;

    /**
     * Main constructor
     *
     * @param packs the list of Pack objects to be displayed
     */
    public ResultModel(List<Pack> packs) {
        this.df = new DecimalFormat("#0.00");
        this.packs = packs;
    }

    /**
     * Returns the number of rows in the table based on number of customers.
     *
     * @return the row count.
     */
    @Override
    public int getRowCount() {
        return packs.size();
    }

    /**
     * Returns the column count.
     *
     * @return the column count.
     */
    @Override
    public int getColumnCount() {
        int size = packs.get(0).getCellCount() + 2; //numbe of cells plus two columns for line# and average
        return size;
    }

    /**
     * Returns the value at a given cell.
     *
     * @param rowIndex
     * @param columnIndex
     * @return the value at the given cell.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        @SuppressWarnings("UnusedAssignment")
        String value = "";
        if (columnIndex == 0) {
            value = packs.get(rowIndex).getID();
        } else if (columnIndex == packs.get(0).getCellCount() + 1) {
            value = df.format(packs.get(rowIndex).calculateSpreadImp() * 100) + "%";
        } else {
            value = packs.get(rowIndex).getCells().get(columnIndex - 1).getAddress() + " (" + df.format(packs.get(rowIndex).getCells().get(columnIndex - 1).getImpedance() * 1000) + " mΩ)";
        }
        return value;

    }

    /**
     * Returns the column name.
     *
     * @param column
     * @return the column name.
     */
    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Pack:";
        } else if (column == packs.get(0).getCellCount() + 1) {
            return "Spread:";
        } else {
            return String.valueOf(column) + ":";
        }
    }
}
