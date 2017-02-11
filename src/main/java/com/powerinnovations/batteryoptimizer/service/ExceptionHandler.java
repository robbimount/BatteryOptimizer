package com.powerinnovations.batteryoptimizer.service;

import com.powerinnovations.batteryoptimizer.view.GUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class provides a static means of logging all exceptions that occur within the program. Log
 * files are generated daily even while a single instance of the program is run long-term. Log files
 * are stored in the Log folder off the current path of the program.
 *
 * Uncaught exceptions are caught by this class by calling
 * "Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());" in the program's main
 * method. All other threads will then revert back to the parent thread because no other handler is
 * defined.
 *
 * Caught exceptions are handled by calling "ExceptionHandler.logEvent(Level.SEVERE, e.getMessage(),
 * e);" in any catch statement where "e" equals the instance name of the throwable parameter.
 *
 * @author Robbi Mount
 */
public class ExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger log = Logger.getLogger(GUI.class.getName());
    private static Calendar currentLog = null;
    private static FileHandler file = null;

    public ExceptionHandler() {

    }

    /**
     * This method logs the thrown exception. It first checks to see if the existing logger has a
     * file handler object attached. It if doesn't, it creates one. If it does, the method checks to
     * see if the date on the logger matches the current date. If it does, the event is logged. If
     * it does not, a new handler is created for the new day and replaces the old one. The event is
     * then logged.
     *
     * @param level The level of severity of the logged event.
     * @param message The textual message to accompany the thrown event. A standalone message can
     * also be logged by providing null int he thrown parameter.
     * @param thrown The thrown exception object (prints the stack trace of the exception).
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public synchronized static void logEvent(Level level, String message, Throwable thrown) {
        try {
            if (log.getHandlers().length == 0) {
                try {
                    new FileInputStream("Logs");
                } catch (Exception e) {
                    File f = new File("Logs");
                    f.mkdir();
                } finally {
                    currentLog = Calendar.getInstance();
                    file = new FileHandler("Logs/eventLog" + String.valueOf(currentLog.get(Calendar.YEAR)) + String.valueOf(currentLog.get(Calendar.MONTH) + 1)
                            + String.valueOf(currentLog.get(Calendar.DATE)) + ".txt", true);
                    SimpleFormatter txt = new SimpleFormatter();
                    file.setFormatter(txt);
                    log.addHandler(file);
                }
            } else if (Calendar.getInstance().get(Calendar.YEAR) == currentLog.get(Calendar.YEAR) && Calendar.getInstance().get(Calendar.MONTH) == currentLog.get(Calendar.MONTH) && Calendar.getInstance().get(Calendar.DATE) == currentLog.get(Calendar.DATE)) {
            } else {
                try {
                    log.removeHandler(file);
                    new FileInputStream("Logs");
                } catch (SecurityException | FileNotFoundException e) {
                    File f = new File("Logs");
                    f.mkdir();
                } finally {
                    currentLog = Calendar.getInstance();
                    file = new FileHandler("Logs/eventLog" + String.valueOf(currentLog.get(Calendar.YEAR)) + String.valueOf(currentLog.get(Calendar.MONTH) + 1)
                            + String.valueOf(currentLog.get(Calendar.DATE)) + ".txt", true);
                    SimpleFormatter txt = new SimpleFormatter();
                    file.setFormatter(txt);
                    log.addHandler(file);
                }
            }
            log.logp(level, "", "", "-----------------------------------------------------\n" + message, thrown);
        } catch (IOException | SecurityException e) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * This method is called for all uncaught exceptions. This method forwards the exception to the
     * logEvent() method.
     *
     * @param t The thread calling the exception (This parameter is not utilize in this loading of
     * the method.
     * @param e The thrown exception to be logged.
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logEvent(Level.SEVERE, e.getMessage(), e);
    }

}
