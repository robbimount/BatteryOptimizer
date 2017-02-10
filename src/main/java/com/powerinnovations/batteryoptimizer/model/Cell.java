package com.powerinnovations.batteryoptimizer.model;

/**
 * A Cell object defines a single cell in a battery pack. It contains a cell address and an
 * impedance value.
 *
 * @author robbi.mount
 * @version 1.0 June 2016
 */
public class Cell implements Comparable {

    private final double impedance;
    private final String address;

    /**
     * Constructor for the Cell object
     *
     * @param address the cell address within a pack.
     * @param impedance the impedance of the cell.
     */
    public Cell(String address, double impedance) {
        this.impedance = impedance;
        this.address = address;
    }

    /**
     * Returns the impedance value of the cell.
     *
     * @return the impedance
     */
    public double getImpedance() {
        return impedance;
    }

    /**
     * Returns the address of the cell within a pack.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Implementation of the Comparable interface for collection sorting.
     *
     * @param o The comparable object
     * @return the comparison result.
     */
    @Override
    public int compareTo(Object o) {
        if (getImpedance() < ((Cell) o).getImpedance()) {
            return -1;
        } else if (getImpedance() > ((Cell) o).getImpedance()) {
            return 1;
        } else {
            return 0;
        }
    }

}
