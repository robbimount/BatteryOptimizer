package com.powerinnovations.batteryoptimizer.view;

import com.powerinnovations.batteryoptimizer.model.Pack;
import java.util.List;

/**
 * An interface for creating UI objects to interface with the PackUtils class and receive updates.
 * @author robbi.mount
 */
public interface OptimizerView {
    public void updateDisplay(List<Pack> packList);
}
