package com.benjaminshamoilia.trackerapp.helper;



/**
 * This class is used as the helper class for the design of Navigation Drawer in Help section.
 *
 */
public class HeaderModel implements Section {

    String header;
    private int section;

    public HeaderModel(int section) {
        this.section = section;
    }

    public void setheader(String header) {
        this.header = header;
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    @Override
    public String getName() {
        return header;
    }

    @Override
    public int sectionPosition() {
        return section;
    }
}
