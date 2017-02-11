package com.powerinnovations.batteryoptimizer.service;

import com.powerinnovations.batteryoptimizer.model.Cell;
import com.powerinnovations.batteryoptimizer.model.Pack;
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
import java.util.Stack;
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

    /**
     * Calculates the average pack impedance based upon the entire collection of Pack objects.
     *
     * @param packList a collection of Pack objects
     * @return the average pack impedance.
     */
    public static double calculateAverageImp(List<Pack> packList) {
        double average = 0;
        average = packList.stream().map((np) -> np.calculateSpreadImp()).reduce(average, (accumulator, _item) -> accumulator + _item);
        return average / packList.size();
    }

    /**
     * Finds the lowest occurring pack impedance from the Pack collection.
     *
     * @param packList a collection of Pack Objects
     * @return the lowest occurring pack impedance.
     */
    public static double calculateLow(List<Pack> packList) {
        double low = 999;
        for (Pack np : packList) {
            if (np.calculateSpreadImp() < low) {
                low = np.calculateSpreadImp();
            }
        }
        return low;
    }

    /**
     * Finds the highest occurring pack impedance form the Pack collection.
     *
     * @param packList a collection of Pack objects
     * @return the highest occurring pack impedance.
     */
    public static double calculateHigh(List<Pack> packList) {
        double hi = 0;
        for (Pack np : packList) {
            if (np.calculateSpreadImp() > hi) {
                hi = np.calculateSpreadImp();
            }
        }
        return hi;
    }

    /**
     * Creates and launches an Excel document depicting the current Pack collection contents.
     *
     * @param packList the Pack list to be exported
     * @throws IOException
     */
    public static void exportPackDetailsToExcel(List<Pack> packList) throws IOException {
        DecimalFormat df = new DecimalFormat("#0.00");
        int numOfCells = packList.get(0).getCellCount();

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
        for (Pack p : packList) {
            Row r = sheet.createRow(rowCount);
            int cellCount = 1;
            r.createCell(0).setCellValue(p.getID());
            for (Cell c : p.getCells()) {
                r.createCell(cellCount).setCellValue(c.getAddress() + " (" + df.format(c.getImpedance() * 1000) + " mΩ)");
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
     * @return
     * @throws IOException thrown in the event of an IO error, or an indivisuble number of packs.
     * For example, if 6 cells were provided, but 4 packs per cell were specified, the
     * IllegalArgumentException would be thrown.
     */
    public static List<Pack> createPackListFromCsv(File csvFile, int numCellsPerPack) throws IOException, IllegalArgumentException {
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
            for(int j = 0; j < numCellsPerPack; j++){
                p.addCell(cells.pop());
            }
            packs.add(p);
        }
        return packs;
    }
}
