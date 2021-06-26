package com.benjaminshamoilia.trackerapp.helper;


/**
 * Created by Jaydeep on 13-03-2018.
 */
public class Constant {
    public static String MAIN_URL = "http://kuurvtrackerapp.com/mobile/";
  //  public static String MAIN_URL = "http://vithamastech.com/track/mobile/";
//    public static String DIRECTORY_FOLDER_NAME = "/TrackerApp";

    public static short OPCODE_WRITE_OWNER_NAME = 0x01; // length(1-18), owner name (1-18)
    public static short OPCODE_WRITE_OWNER_PHONE_NO = 0x02;// length(10), owner phone no(10)
    public static short OPCODE_WRITE_OWNER_EMAIL_1 = 0x03;// length(1-18), owner email(1-18)
    public static short OPCODE_WRITE_OWNER_EMAIL_2 = 0x04;// length(0-18), owner email(0-18)
    public static short OPCODE_READ_AUTH_NUMBER = 0x05;
    public static short OPCODE_WRITE_AUTH_VALUE = 0x06;
    public static short OPCODE_BEEP_BUZZER = 0x07;
    public static short OPCODE_SILENTMODE_ON_OFF = 0x08;
    public static short OPCODE_DELETE_OWNER_INFO = 0x09;
    public static short OPCODE_DEVICE_ADD = 0x0A;
    public static short OPCODE_VERIFY_DEVICE_INFO = 0x0B;
    public static short OPCODE_READ_OWNER_INFO = 0x0C;
    public static short OPCODE_READ_BATTERY_VALUE = 0x0D;
    public static short OPCODE_STOP_BUZZER = 0x0E;
    public static short OPCODE_READ_SILENT_MODE_ON_OFF = 0x0F;
    public static short OPCODE_WRITE_BUZZER_VOLUME= 0x10;
    public static short OPCODE_READ_BUZZER_VOLUME = 0x11;
    public static short OPCODE_READ_UNIQUE_KEY=0x12; //18
}
