package com.powerinnovations.batteryoptimizer.view;

import com.powerinnovations.batteryoptimizer.service.ExceptionHandler;
import com.powerinnovations.batteryoptimizer.model.Pack;
import com.powerinnovations.batteryoptimizer.service.PackUtils;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main hub of this program. Contains the display logic as well as the main method.
 *
 * @author robbi.mount
 * @version 1.0 June 2016
 */
public final class GUI extends javax.swing.JFrame {

    private final DecimalFormat df = new DecimalFormat("#0.00");
    private List<Pack> packList = Collections.EMPTY_LIST;
    private boolean running = false;
    private final int optimizedStandard = 10000; //An arbitrary large number of failed improvement attempts that is a safe indication that optimization has occured.

    /**
     * Default main method. Sets the look and feel and launches the main JFrame object.
     *
     * @param args
     */
    public static void main(String[] args) {
        //TODO: Create Test Cases
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
            new GUI().setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates new form View
     *
     */
    public GUI() {
        initComponents();
        decorate();
    }

    private void decorate() {
        this.setTitle("Battery Impedance Optimizer");
        URL url = ClassLoader.getSystemResource("logo.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        this.setIconImage(img);
        method.setToolTipText("If selected, the decrease method will select random packs.  "
                + "If not selected, the method will always attempt to decrease the max cell.");
    }

    /**
     * updates the display labels. If the optimize process is running the main grid update is
     * bypassed in order to save processor time.
     */
    private void updateDisplay() {
        grid.removeAll();
        if (!running) {
            packList.stream().forEach((np) -> {
                grid.add(new JLabel(df.format(np.calculateSpreadImp() * 100d) + "%"));
            });
        }
        averageSpreadLabel.setText(df.format(PackUtils.calculateAverageImp(packList) * 100d) + "%");
        numOfPacksLabel.setText(String.valueOf(packList.size()));
        highestSpreadLabel.setText(df.format(PackUtils.calculateHigh(packList) * 100d) + "%");
        lowestSpreadLabel.setText(df.format(PackUtils.calculateLow(packList) * 100d) + "%");
        grid.updateUI();
    }

    /**
     * The optimizer thread and logic. This logic has two modes depending upon the preference of the
     * user. It will either perform a truly random or a semi-random optimization seek. In truly
     * random, two random packs are selected, within each pack a random cell is selected and the two
     * are swapped. If the resulting defined metric (collection average for truly random, max
     * impedance for high centered) is less than the baseline, the change is kept; otherwise the
     * original pack configuration is kept and a new cycle begins. This method continually seeks a
     * lower end state of either metric by brute processor power and randomization.
     */
    private void go() {
        //TODO: Move to the util class.
        new Thread() {
            @Override
            public void run() {
                Random ran = new Random();
                int completeCounter = 0;
                while (running) {
                    try {
                        double baseline = 0;
                        Pack workingA = null;
                        Pack workingB = null;

                        //Gather the specimens
                        if (method.isSelected()) {
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
                        if (method.isSelected()) {
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
                        if (completeCounter > optimizedStandard) {
                            stopActionPerformed(null);
                            JOptionPane.showMessageDialog(null, "Optimization Complete");
                        }
                        updateDisplay();
                    } catch (Exception e) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        }.start();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grid = new javax.swing.JPanel();
        labelPanel = new javax.swing.JPanel();
        lowestSpreadLabel = new javax.swing.JLabel();
        numOfPacksLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        averageSpreadLabel = new javax.swing.JLabel();
        highestSpreadLabel = new javax.swing.JLabel();
        working = new javax.swing.JProgressBar();
        buttonPanel = new javax.swing.JPanel();
        open = new javax.swing.JButton();
        start = new javax.swing.JButton();
        export = new javax.swing.JButton();
        results = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        method = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        grid.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        grid.setLayout(new java.awt.GridLayout(20, 5, 5, 5));

        labelPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lowestSpreadLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lowestSpreadLabel.setOpaque(true);

        numOfPacksLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numOfPacksLabel.setMinimumSize(new java.awt.Dimension(800, 600));
        numOfPacksLabel.setOpaque(true);
        numOfPacksLabel.setPreferredSize(new java.awt.Dimension(800, 600));

        jLabel3.setBackground(new java.awt.Color(204, 204, 204));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Highest Spread");
        jLabel3.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Num of Packs");
        jLabel2.setOpaque(true);

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Average Spread");
        jLabel1.setOpaque(true);

        jLabel4.setBackground(new java.awt.Color(204, 204, 204));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Lowest Spread");
        jLabel4.setOpaque(true);

        averageSpreadLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        averageSpreadLabel.setOpaque(true);

        highestSpreadLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        highestSpreadLabel.setOpaque(true);

        javax.swing.GroupLayout labelPanelLayout = new javax.swing.GroupLayout(labelPanel);
        labelPanel.setLayout(labelPanelLayout);
        labelPanelLayout.setHorizontalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(working, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(labelPanelLayout.createSequentialGroup()
                        .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(averageSpreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numOfPacksLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(highestSpreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lowestSpreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        labelPanelLayout.setVerticalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(labelPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numOfPacksLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(labelPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(averageSpreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(labelPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(highestSpreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(labelPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lowestSpreadLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(working, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        open.setText("Open CSV");
        open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });

        start.setText("Start");
        start.setEnabled(false);
        start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });

        export.setText("Export");
        export.setEnabled(false);
        export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportActionPerformed(evt);
            }
        });

        results.setText("Results");
        results.setEnabled(false);
        results.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsActionPerformed(evt);
            }
        });

        stop.setText("Stop");
        stop.setEnabled(false);
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });

        method.setSelected(true);
        method.setText("True Random");
        method.setToolTipText("");

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(open, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(method)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(export, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(results, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start)
                    .addComponent(stop)
                    .addComponent(open))
                .addGap(8, 8, 8)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(results)
                    .addComponent(export)
                    .addComponent(method))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addComponent(grid, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Starts the optimization process and sets the UI accessibility appropriately.
     *
     * @param evt
     */
    private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed
        running = true;
        go();
        start.setEnabled(false);
        stop.setEnabled(true);
        export.setEnabled(false);
        results.setEnabled(false);
        open.setEnabled(false);
        method.setEnabled(false);
        working.setIndeterminate(true);
    }//GEN-LAST:event_startActionPerformed

    /**
     * Stops the optimization process for the user to review the results and sets the UI
     * accessibility appropriately.
     *
     * @param evt
     */
    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
        running = false;
        stop.setEnabled(false);
        start.setEnabled(true);
        export.setEnabled(true);
        results.setEnabled(true);
        method.setEnabled(true);
        working.setIndeterminate(false);
        updateDisplay();
    }//GEN-LAST:event_stopActionPerformed

    /**
     * Launches the results JFrame to view detailed pack configuration results.
     *
     * @param evt
     */
    private void resultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsActionPerformed
        packList.sort((p1, p2) -> p1.compareTo(p2));
        new Results(packList).setVisible(true);

    }//GEN-LAST:event_resultsActionPerformed

    /**
     * Exports the detailed pack configuration results to an Excel file.
     *
     * @param evt
     */
    private void exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportActionPerformed
        try {
            packList.sort((p1, p2) -> p1.compareTo(p2));
            PackUtils.exportPackDetailsToExcel(packList);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exportActionPerformed

    /**
     * Launches a file chooser window to select the CSV file containing the original battery data.
     * Then initial sorting and assembly of the Pack and Cell collections takes place. The main GUI
     * object is then updated.
     *
     * @param evt
     */
    private void openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openActionPerformed
        try {
            int numCellsPerPack = 0;
            //TODO: Fix null pointer ex here
            numCellsPerPack = Integer.parseInt(JOptionPane.showInputDialog(this,
                    "How many cells will each pack have?\n(Note: the number of cells "
                    + "provided must be a multiple of this value.", 0));
            if (numCellsPerPack > 1) {
                JFileChooser fc = new JFileChooser();
                int option;
                option = fc.showOpenDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    try {
                        this.packList = PackUtils
                                .createPackListFromCsv(fc.getSelectedFile(), numCellsPerPack);
                        start.setEnabled(true);
                        export.setEnabled(true);
                        results.setEnabled(true);
                        open.setEnabled(false);
                        updateDisplay();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage());
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            } else {
                throw new NumberFormatException("Import failed: Enter only integers greater than "
                        + "1 for the size.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_openActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel averageSpreadLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton export;
    private javax.swing.JPanel grid;
    private javax.swing.JLabel highestSpreadLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JLabel lowestSpreadLabel;
    private javax.swing.JCheckBox method;
    private javax.swing.JLabel numOfPacksLabel;
    private javax.swing.JButton open;
    private javax.swing.JButton results;
    private javax.swing.JButton start;
    private javax.swing.JButton stop;
    private javax.swing.JProgressBar working;
    // End of variables declaration//GEN-END:variables
}
