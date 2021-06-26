package com.proofofLife.CustomBleObjects;

public class FingerPrintData {
    int indexNumer;
    String hexFingerPrintData;

    public FingerPrintData(int indexNumer, String hexFingerPrintData) {
        this.indexNumer = indexNumer;
        this.hexFingerPrintData = hexFingerPrintData;
    }

    public int getIndexNumer() {
        return indexNumer;
    }

    public void setIndexNumer(int indexNumer) {
        this.indexNumer = indexNumer;
    }

    public String getHexFingerPrintData() {
        return hexFingerPrintData;
    }

    public void setHexFingerPrintData(String hexFingerPrintData) {
        this.hexFingerPrintData = hexFingerPrintData;
    }

    @Override
    public String toString() {
        return "FingerPrintData{" + "indexNumer=" + indexNumer + ", hexFingerPrintData='" + hexFingerPrintData + '\'' + '}';
    }
}
