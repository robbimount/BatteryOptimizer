package com.powerinnovations.batteryoptimizer.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A Pack object describes a single Lithium Iron Phosphate battery with 12 cells. (realistically the
 * number of cells is irrelevant, however the results and export options are dependant upon 12
 * cells.
 *
 * @author robbi.mount
 * @version 1.0 June 2016
 */
public final class Pack implements Comparable {

    private final List<Cell> cells;
    private final String id;

    /**
     * Constructor for a Pack object.
     *
     * @param newId the pack ID or number.
     */
    public Pack(String newId) {
        this.cells = new LinkedList();
        this.id = newId;
    }

    /**
     * Adds a cell to the Cell collection.
     *
     * @param c
     */
    public void addCell(Cell c) {
        cells.add(c);
    }

    /**
     * Calculates the impedance spread of the pack in terms of the range divided by the average
     * impedance.
     *
     * @return the impedance spread percent.
     */
    public double calculateSpreadImp() {
        double average = 0;
        double high = 0;
        double low = 99999;
        for (Cell c : cells) {
            average += c.getImpedance();

            if (c.getImpedance() > high) {
                high = c.getImpedance();
            }
            if (c.getImpedance() < low) {
                low = c.getImpedance();
            }
        }
        average /= cells.size();
        return (high - low) / average;

    }

    /**
     * Returns a random cell from the Cell collection.
     *
     * @return a random cell
     */
    public Cell getRandomCell() {
        Random ran = new Random();
        return cells.remove(ran.nextInt(cells.size()));
    }

    /**
     * Returns the ID of the pack.
     *
     * @return the pack ID.
     */
    public String getID() {
        return id;
    }

    /**
     * Returns a redundant and distinct instance of this pack. Essentially, this is pass by value
     * instead of pass by reference.
     *
     * @return A new copy of this pack.
     */
    public Pack getClone() {
        Pack p = new Pack(id);
        for (Cell c : cells) {
            p.addCell(c);
        }
        return p;
    }

    /**
     * Returns the Cell collection.
     *
     * @return the Cell collection.
     */
    public List<Cell> getCells() {
        return cells;
    }

    /**
     * Implementation of the Comparable interface for collection sorting.
     *
     * @param o The comparable object
     * @return the comparison result.
     */
    @Override
    public int compareTo(Object o) {
        if (calculateSpreadImp() < ((Pack) o).calculateSpreadImp()) {
            return -1;
        } else if (calculateSpreadImp() > ((Pack) o).calculateSpreadImp()) {
            return 1;
        } else {
            return 0;
        }
    }

}
