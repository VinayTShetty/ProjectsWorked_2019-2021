package com.proofofLife.UtilitConversion;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

public class ConversionHelper {
    private static final char[] UPPER_HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static String getHexStringFromByteArray(byte[] input, boolean isSpacingRequired) {
        StringBuilder HexBuilder = new StringBuilder(2 * input.length);
        for (int i = 0; i < input.length; i++) {
            HexBuilder.append(UPPER_HEX_CHARS[(input[i] & 0xF0) >> 4]); // Extract Higher Nibble
            HexBuilder.append(UPPER_HEX_CHARS[(input[i] & 0x0F)]); // Extract Lower Nibble

            if (isSpacingRequired) {
                if (i != input.length - 1) {
                    HexBuilder.append(" ");
                }
            }
        }
        return HexBuilder.toString();
    }

    /**
     * Covert HexTimeStamp to timeStamp.
     */
    public static BigInteger convert4bytes(String value4bytes) {
        return new BigInteger(value4bytes, 16);
    }

    public static String getMinValueFromTimeStamp(String hexValue) {
        String minutesValue="NA";
        long timeStamp=Long.parseLong(""+convert4bytes(hexValue));
        timeStamp=timeStamp*1000;
        final Date currentTime = new Date();
        currentTime.setTime(timeStamp);
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar= sdf.getCalendar();
        sdf.format(currentTime);
        int minutes=calendar.get(Calendar.MINUTE);
        //False for single digit, true for double digit
        boolean isDoubleDigit = (minutes / 10 == 0 && minutes / 100 == 0) ? false : true;
        if(isDoubleDigit){
            minutesValue=""+calendar.get(Calendar.MINUTE);
        }else {
            switch (minutes){
                case 0:{
                    minutesValue="00";
                    break;
                }
                case 1:{
                    minutesValue="01";
                    break;
                }
                case 2:{
                    minutesValue="02";
                    break;
                }
                case 3:{
                    minutesValue="03";
                    break;
                }
                case 4:{
                    minutesValue="04";
                    break;
                }
                case 5:{
                    minutesValue="05";
                    break;
                }
                case 6:{
                    minutesValue="06";
                    break;
                }
                case 7:{
                    minutesValue="07";
                    break;
                }
                case 8:{
                    minutesValue="08";
                    break;
                }
                case 9:{
                    minutesValue="09";
                    break;
                }
            }
        }
        return minutesValue;
    }

    public static byte[] convert_TimeStampTo_4bytes(int value) {
        if (value > 0) {
            return ByteBuffer.allocate(4).putInt(value).array();
        } else {
            byte[] timeNotSelected = new byte[4];
            timeNotSelected[0] = (byte) 0XFF;
            timeNotSelected[1] = (byte) 0XFF;
            timeNotSelected[2] = (byte) 0XFF;
            timeNotSelected[3] = (byte) 0XFF;
            return timeNotSelected;
        }
    }

    public static byte[] intToBytes_2(final int data) {
        return new byte[] {(byte)((data >> 8) & 0xff), (byte)((data >> 0) & 0xff)};
    }
    public static byte[] intToBytes_1(final int data) {
        return new byte[] {(byte)((data >> 0) & 0xff)};
    }

}
