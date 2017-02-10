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
public class resultModel extends AbstractTableModel {

    private List<Pack> pcs;
    private final DecimalFormat df;

    /**
     * Default constructor.
     */
    public resultModel() {
        this.df = new DecimalFormat("#0.00");
    }

    /**
     * Main constructor
     *
     * @param pcs the list of Pack objects to be displayed
     */
    public resultModel(List<Pack> pcs) {
        this.df = new DecimalFormat("#0.00");
        this.pcs = pcs;
    }

    /**
     * Returns the number of rows in the table based on number of customers.
     *
     * @return the row count.
     */
    @Override
    public int getRowCount() {
        return pcs.size();

    }

    /**
     * returns the column count.
     *
     * @return the column count.
     */
    @Override
    public int getColumnCount() {
        int size = 14;
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
            value = pcs.get(rowIndex).getID();
        } else if (columnIndex == 13) {
            value = df.format(pcs.get(rowIndex).calculateSpreadImp() * 100) + "%";
        } else {
            value = pcs.get(rowIndex).getCells().get(columnIndex - 1).getAddress() + " (" + df.format(pcs.get(rowIndex).getCells().get(columnIndex - 1).getImpedance() * 1000) + " mΩ)";
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
        } else if (column == 13) {
            return "Spread:";
        } else {
            return String.valueOf(column) + ":";
        }
    }
}
