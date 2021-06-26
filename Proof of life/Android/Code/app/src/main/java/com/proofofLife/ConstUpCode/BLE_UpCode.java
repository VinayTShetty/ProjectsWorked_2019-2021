package com.proofofLife.ConstUpCode;

public class BLE_UpCode {
    public static final String CONST_TIME_STAMP="1618560333605";
    public static final String CONST_3_DIGIT_NUMBER="740";
    public static final String FINGER_PRINT_SECURITY_KEY="39551712138898479008492112d41129";

    public static final byte READ_AUTHNECIATION=0X01;
    public static final byte WRITE_AUTHNECIATION=0X02;
    public static final byte SET_TIME=0X03;
    public static final byte VERIFY_DEVICE_INFO=0X07;
    public static final byte ADD_DEVICE_INFO=0X05;
    public static final byte DELETE_DEVIE_INFO=0X06;
    public static final byte SCAN_FINGER_PRINT_DATA=0X08;
    public static final byte ALL_FINGER_PRINT_DATA=0X09;

    public static final byte FAILURE=0X00;
    public static final byte SUCESS=0X01;
    public static final byte UNKNOWN=0X02;

    public static final byte DATA_STARTING=0X02;
    public static final byte DATA_ENDING=0X12;

    public static final byte DATA_LENGTH_ONE=0X01;
    public static final byte USER_DEFINED= (byte) 0XFF;
    /**
     * Finger Print index.
     */
    public static final byte FRIST_TIME=0X01;
    public static final byte SECOND_TIME=0X02;
    public static final byte THIRD_TIME=0X03;
    public static final byte FOURTH_TIME=0X04;
    public static final byte FIFTH_TIME=0X05;
    public static final byte SIXTH_TIME=0X06;
    public static final byte SEVENTH_TIME=0X07;
    public static final byte EIGHT_TIME=0X08;
    public static final byte NINETH_TIME=0X09;
    public static final byte TENTH_TIME=0X0A;

    /**
     * User Setting
     */
    public static final byte USER_SETTING=0X0B;
}
