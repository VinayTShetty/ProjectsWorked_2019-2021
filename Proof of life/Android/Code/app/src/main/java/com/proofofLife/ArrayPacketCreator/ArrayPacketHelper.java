package com.proofofLife.ArrayPacketCreator;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Base64;

import static com.proofofLife.UtilitConversion.ConversionHelper.getHexStringFromByteArray;
import static com.proofofLife.UtilitConversion.ConversionHelper.intToBytes_1;
import static com.proofofLife.UtilitConversion.ConversionHelper.intToBytes_2;

public class ArrayPacketHelper {
    private static final String TAG=ArrayPacketHelper.class.getSimpleName();
    public static byte[] Authenication_ValuePacket_Array(int m_auth_key){
        byte authValue[]=new byte[4];
        int calcualtedValue=((((m_auth_key * 49) + 1232) * 29) - (121*m_auth_key + 778));
        /**
         * Send ing the byte array in Reverse due to firmware issue.
         */
        authValue[0] = (byte) (calcualtedValue & 0xFF);
        authValue[1] = (byte) ((calcualtedValue >> 8) & 0xFF);
        authValue[2] = (byte) ((calcualtedValue >> 16) & 0xFF);
        authValue[3] = (byte) ((calcualtedValue >> 24) & 0xFF);
        return authValue;
    }
    public static byte[] AskAuthenicationNumber_Array(){
        byte [] dataArray=new byte[1];
        dataArray[0]=0X00;
        return  dataArray;
    }

    public static byte[]  add_verify_delete_DeviceInfo(String secretKey){
        if((secretKey!=null)&&(secretKey.length()==16)){
            final String combineData=secretKey;
            byte[] deviceInfo=new byte[16];
           deviceInfo=combineData.getBytes();
         //   deviceInfo= Base64.getEncoder().encode(combineData.getBytes());
            Log.d(TAG, "verifyDeviceInfo: "+getHexStringFromByteArray(deviceInfo,false));
            return  deviceInfo;
        }else return null;
    }
   /* public static byte[]  add_verify_delete_DeviceInfo(String timeStamp_milliSeconds, String last_3_digit_number){
        byte [] x= {0x39,0x55,0x17,0x12,0x13,(byte) 0x88,(byte) 0x98,0x47,(byte) 0x90,0x08,0x49,0x21,0x12,(byte) 0xd4,0x11,0x29};
        return x;
    }*/

    public static byte[] index_ScanFingerPrintData(byte indexNUmber){
        byte[] dataArray=new byte[16];
        dataArray[0]=indexNUmber;
        return dataArray;
    }


    public static byte[] askDeviceInfo(){
        byte[] askDeviceInfo=new byte[16];
        askDeviceInfo[1]= (byte) 0XFF;;
        return askDeviceInfo;
    }

    public static byte[] getAllFingerPrints(){
        byte[] dataArray=new byte[16];
        dataArray[0]= (byte) 0xff;
        return dataArray;
    }
    public static byte [] getNextFingerPrintData(byte index){
        byte[] dataArray=new byte[16];
        dataArray[0]= index;
        return  dataArray;
    }
    public static  byte[] getPresetTime(int timeStamp){
        byte[] dataArray=new byte[16];
        if (timeStamp > 0) {
            byte [] timeStampArray= ByteBuffer.allocate(4).putInt(timeStamp).array();
            dataArray[0]=timeStampArray[0];
            dataArray[1]=timeStampArray[1];
            dataArray[2]=timeStampArray[2];
            dataArray[3]=timeStampArray[3];
            return dataArray;

        } else {
            dataArray[0] = (byte) 0XFF;
            dataArray[1] = (byte) 0XFF;
            dataArray[2] = (byte) 0XFF;
            dataArray[3] = (byte) 0XFF;
            return dataArray;
        }
    }

    public static byte[] getSettingArray(int otpValidity,int otpDisplayTime,int fingerPrintmatchType,int polAuthCycle){
        byte[] dataArray=new byte[16];
        byte [] otpValidityArray=intToBytes_2(otpValidity);
        byte [] otpdisplayTime=intToBytes_1(otpDisplayTime);
        byte [] fingerPrintMatch=intToBytes_1(fingerPrintmatchType);
        byte [] polArray=intToBytes_2(polAuthCycle);
        int  otpValidityArraylength=otpValidityArray.length;
        int otpdisplayTimelength=otpdisplayTime.length;
        int  fingerPrintMatchlength=fingerPrintMatch.length;
        int  polArraylength=polArray.length;
        System.arraycopy(otpValidityArray, 0, dataArray, 0, otpValidityArraylength);
        System.arraycopy(otpdisplayTime, 0, dataArray, otpValidityArraylength, otpdisplayTimelength);
        System.arraycopy(fingerPrintMatch, 0, dataArray, otpValidityArraylength+otpdisplayTimelength,fingerPrintMatchlength);
        System.arraycopy(polArray, 0, dataArray, otpValidityArraylength+otpdisplayTimelength+fingerPrintMatchlength,polArraylength);
        return dataArray;
    }
}
