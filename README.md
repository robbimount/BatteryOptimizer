# BatteryOptimizer

## An Introduction
This is a Java application for optimizing the pairing of cells for batteries in a configurateion of 4S3P and requiring tight impedance matching.

Lithium Iron Phosphate battery cells suffer from inconsistencies in the manufacture process.  These inconsistencies result in varying levels of cell impedance after manufactoruing.  Large variations in impedance between battery cells used in a parallel (+ ends lined up with other + ends, - ends lined up with other - ends) lead to a disparity in their charge state over time due to a variation in charge current to the individual cells resulting from the varying impedance.

This cell imballance can be counteracted through the use of active or passive ballancers included in battery management systems; however, most balancers are only capable of small balancing current.  For large cells, this is not sufficient to counteract the induced imbalance caused by mismatched cells.  The result is that the cells will eventually no longer carry similar charge states (some cells will be lower, others will be higher).  This results in the battery management system terminating the charge cycle rematurely (because a single cell reached full and can't be overcharged).  The discharge cycle is also terminated prematurely (because a single cell reached empty before the others and can't be over-discharged).  The net effect is a battery with quickly diminishing overall capacity.

During the assembly process, cells must be paired very closely.  This application takes a collection of cells and optimizes their arrangement to achieve the closest unity of impedance possible.  

## How to use
This application GUI was built in NetBeans IDE.  It is recommended to load the project into NetBeans to modify the program at all.  



