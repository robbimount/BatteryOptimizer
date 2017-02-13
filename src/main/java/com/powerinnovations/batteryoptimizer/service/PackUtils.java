package com.powerinnovations.batteryoptimizer.service;

import com.powerinnovations.batteryoptimizer.model.Cell;
import com.powerinnovations.batteryoptimizer.model.Pack;
import com.powerinnovations.batteryoptimizer.view.OptimizerView;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Provides utilities for the comparison and calculation of Pack object collections.
 *
 * @author robbi.mount
 * @version 1.0 June 2016
 */
public class PackUtils {

    private boolean running;
    private Thread optimizerThread;
    private final OptimizerView gui;
    private final List<Pack> packList;
    private int optimizedStandard = 10000; //An arbitrary large number of failed improvement attempts that is a safe indication that optimization has occured.

    /**
     * Constructs a new PackUtils object and ties it to a OptimizerView object as a user interface.
     *
     * @param gui
     */
    public PackUtils(OptimizerView gui) {
        this.gui = gui;
        packList = new ArrayList<>();
    }

    /**
     * Calculates the average pack impedance based upon the entire collection of Pack objects.
     *
     * @param packList the list of Pack objects.
     * @return the average pack impedance.
     */
    public static double calculateAverageImp(List<Pack> packList) {
        double average = 0;
        average = packList.stream().map((pack) -> pack.calculateSpreadImp()).reduce(average, (accumulator, _item) -> accumulator + _item);
        return average / packList.size();
    }

    /**
     * Finds the lowest occurring pack impedance from the Pack collection.
     *
     * @param packList the list of Pack objects.
     * @return the lowest occurring pack impedance.
     */
    public static double calculateLow(List<Pack> packList) {
        double low = 999;
        for (Pack pack : packList) {
            if (pack.calculateSpreadImp() < low) {
                low = pack.calculateSpreadImp();
            }
        }
        return low;
    }

    /**
     * Finds the highest occurring pack impedance form the Pack collection within this util
     * instance.
     *
     * @param packList the Pack list object.
     * @return the highest occurring pack impedance.
     */
    public static double calculateHigh(List<Pack> packList) {
        double hi = 0;
        for (Pack pack : packList) {
            if (pack.calculateSpreadImp() > hi) {
                hi = pack.calculateSpreadImp();
            }
        }
        return hi;
    }

    /**
     * Creates and launches an Excel document depicting the current Pack collection contents.
     *
     * @throws IOException
     */
    public void exportPackDetailsToExcel() throws IOException {
        DecimalFormat df = new DecimalFormat("#0.00");
        int numOfCells = getPackList().get(0).getCellCount();

        //Create the document
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Battery Sort Results");
        Row row = sheet.createRow(0);

        //Build the document header
        row.createCell(0).setCellValue("Pack Num:");
        for (int i = 1; i <= numOfCells; i++) {
            row.createCell(i).setCellValue("Cell " + i + ":");
        }
        row.createCell(numOfCells + 1).setCellValue("Average Impedance:");

        //Populate the Data
        int rowCount = 1;
        for (Pack p : getPackList()) {
            Row r = sheet.createRow(rowCount);
            int cellCount = 1;
            r.createCell(0).setCellValue(p.getID());
            for (Cell c : p.getCells()) {
                r.createCell(cellCount).setCellValue(c.getAddress()
                        + " ("
                        + df.format(c.getImpedance() * 1000)
                        + " mΩ)");
                cellCount++;
            }
            r.createCell(cellCount).setCellValue(df.format(p.calculateSpreadImp() * 100) + "%");
            rowCount++;
        }

        //Format the sheet for easy Viewing
        for (int i = 0; i < numOfCells + 2; i++) {
            sheet.autoSizeColumn(i);
        }

        //Open a temporary file and launch it in Excel
        File file = File.createTempFile("sortexport", ".xls");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
            fos.close();
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Creates a List of Pack objects based upon a CSV file input.
     *
     * @param csvFile a file object containing RFC-1480 CSV data.
     * @param numCellsPerPack the desired number of cells per pack
     * @throws IOException thrown in the event of an IO error, or an indivisible number of packs.
     * For example, if 6 cells were provided, but 4 packs per cell were specified, the
     * IllegalArgumentException would be thrown.
     */
    public void loadPackListFromCsv(File csvFile, int numCellsPerPack) throws IOException, IllegalArgumentException {
        List<Pack> packs = new ArrayList<>();
        Stack<Cell> cells = new Stack<>();

        //Read the CSV data and create a master list of cells.
        try (Reader source = new FileReader(csvFile)) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(source);
            records.forEach(csvCell -> {
                Cell newCell = new Cell(csvCell.get("cell_ID"), csvCell.get("cell_value"));
                cells.push(newCell);
            });
        } catch (NumberFormatException e) {
            throw e;
        }

        //Check and see if the number of provided cells is divisible by the requested pack size.
        if (cells.size() % numCellsPerPack != 0) {
            throw new IllegalArgumentException("The number of cells provided is not divisible by the specified pack size.");
        }

        //Create individual packs
        int totalCells = cells.size();
        for (int i = 0; i < totalCells / numCellsPerPack; i++) {
            Pack p = new Pack(Integer.toString(i));
            for (int j = 0; j < numCellsPerPack; j++) {
                p.addCell(cells.pop());
            }
            packs.add(p);
        }
        packList.addAll(packs);
        gui.updateDisplay(new ArrayList(packList));
    }

    public void pauseOptimize() {
        running = false;
        synchronized (packList) {
            packList.sort((p1, p2) -> p1.compareTo(p2));
            gui.updateDisplay(packList);
        }
    }

    /**
     * The optimizer thread and logic. This logic has two modes depending upon the preference of the
     * user. It will either perform a truly random or a semi-random optimization seek. In truly
     * random, two random packs are selected, within each pack a random cell is selected and the two
     * are swapped. If the resulting defined metric (collection average for truly random, max
     * impedance for high centered) is less than the baseline, the change is kept; otherwise the
     * original pack configuration is kept and a new cycle begins. This trulyRandom continually
     * seeks a lower end state of either metric by brute processor power and randomization.
     *
     * @param trulyRandom the method of optimization; true = trulyRandom, false = use highest
     * impedance spread.
     */
    public void optimize(boolean trulyRandom) {
        running = true;
        optimizerThread = new Thread() {
            @Override
            public void run() {
                Random ran = new Random();
                int completeCounter = 0;
                while (running) {
                    synchronized (packList) {
                        try {
                            double baseline = 0;
                            Pack workingA = null;
                            Pack workingB = null;

                            //Gather the specimens
                            if (trulyRandom) {
                                baseline = PackUtils.calculateAverageImp(packList);
                                workingA = packList.remove(ran.nextInt(packList.size()));
                                workingB = packList.remove(ran.nextInt(packList.size()));
                            } else {
                                baseline = PackUtils.calculateHigh(packList);
                                workingA = Collections.max(packList);
                                packList.remove(workingA);
                                workingB = packList.remove(ran.nextInt(packList.size()));
                            }

                            //Prepare a backup
                            Pack cloneA = workingA.getClone();
                            Pack cloneB = workingB.getClone();

                            //Make the switcheroo
                            workingA.addCell(workingB.getRandomCell());
                            workingB.addCell(workingA.getRandomCell());
                            packList.add(workingA);
                            packList.add(workingB);

                            //Check the result
                            double result = 0;
                            if (trulyRandom) {
                                result = PackUtils.calculateAverageImp(packList);
                            } else {
                                result = PackUtils.calculateHigh(packList);
                            }

                            //If we didn't improve, undo.
                            if (!(result < baseline)) {
                                packList.remove(workingA);
                                packList.remove(workingB);
                                packList.add(cloneA);
                                packList.add(cloneB);
                                completeCounter++;
                            } else {
                                completeCounter = 0;
                            }

                            //Check and see if optimisation is complete (by law of large numbers)
                            if (completeCounter > getOptimizedStandard()) {
                                pauseOptimize();
                                JOptionPane.showMessageDialog(null, "Optimization Complete");
                            }
                            gui.updateDisplay(new ArrayList(packList));
                        } catch (Exception ex) {
                            ExceptionHandler.logEvent(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        };
        optimizerThread.start();
    }

    /**
     * Returns a copy of the pack list
     *
     * @return the packList
     */
    public List<Pack> getPackList() {
        synchronized (packList) {
            return new ArrayList(packList);
        }
    }

    /**
     * Returns the current optimization standard
     *
     * @return the optimizedStandard
     */
    public int getOptimizedStandard() {
        return optimizedStandard;
    }

    /**
     * Sets the current optimization standard
     *
     * @param optimizedStandard the optimizedStandard to set
     */
    public void setOptimizedStandard(int optimizedStandard) {
        this.optimizedStandard = optimizedStandard;
    }

    /**
     * Returns the status of the optimization thread.
     *
     * @return the optimization thread status.
     */
    public boolean isOptimizing() {
        return running;
    }
}
