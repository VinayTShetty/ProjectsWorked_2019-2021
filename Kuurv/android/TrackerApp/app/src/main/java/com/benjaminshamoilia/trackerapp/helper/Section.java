package com.benjaminshamoilia.trackerapp.helper;


/**
 * This class is used as the helper class for the design of Navigation Drawer in Help section.
 *
 */
public interface Section {

    boolean isHeader();
    String getName();

    int sectionPosition();
}
