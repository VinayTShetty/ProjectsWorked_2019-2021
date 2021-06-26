package com.proofofLife.BlePacketHelper;
import android.util.Base64;
import android.util.Log;

import com.proofofLife.Encryption.EncryptionHelper;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.proofofLife.ConstUpCode.BLE_UpCode.CONST_3_DIGIT_NUMBER;
import static com.proofofLife.ConstUpCode.BLE_UpCode.CONST_TIME_STAMP;
import static com.proofofLife.ConstUpCode.BLE_UpCode.READ_AUTHNECIATION;
import static com.proofofLife.UtilitConversion.ConversionHelper.getHexStringFromByteArray;

public class PacketCreator {
    public static final String TAG=PacketCreator.class.getSimpleName();
    public static byte[] get_AuthenicationPacket_Magic_Number(byte upcode,byte length, byte[] data,boolean encryption_true_false) {
        return createpacket(upcode,length, data,encryption_true_false);
    }

    public static byte[] set_AuthenicationPacket_Magic_Number(byte upcode,byte length, byte[] data,boolean encryption_true_false) {
        return createpacket(upcode, length,data,encryption_true_false);
    }

    public static byte [] get_VerifyDevice_Info(byte upcode,byte length, byte[] data,boolean encryption_true_false){
        return createpacket(upcode, length,data,encryption_true_false);
    }

    public static byte [] set_VerifyDevice_Info(byte upcode, byte length,byte[] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }
    public static byte[] get_delete_user_info(byte upcode,byte length,byte [] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }
    public static byte[] get_ScanningFingerPrintData(byte upcode,byte length,byte[] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }
    public static byte[] getCompleteFingerPrintData(byte upcode,byte length,byte[] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }

    public static byte[] setTimeStampToFirmware(byte upcode,byte length,byte[] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }

    public static byte[] getNextPacketFingerPrintData(byte upcode,byte length,byte[] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }

    public static byte [] getSettingPacketForFirmware(byte upcode,byte length,byte[] data,boolean encryption_true_false){
        return createpacket(upcode,length, data,encryption_true_false);
    }




    public static byte [] get_16_bytes_encry(){
        byte [] toatlData=new byte[18];
        byte[] upcode_length=new byte[2];
        upcode_length[0]=(byte)0xff;
        upcode_length[1]=(byte)0x10;
        byte [] encrypteddata=new byte[16];
        try {
            encrypteddata= EncryptionHelper.encryptData(send_16_bytes_data());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }


        System.arraycopy(upcode_length, 0, toatlData, 0, upcode_length.length);
        System.arraycopy(encrypteddata, 0, toatlData, upcode_length.length, encrypteddata.length);


        return  toatlData;
    }

    public static byte[] getStringytes(){
        byte [] toatlData=new byte[18];
        byte[] upcode_length=new byte[2];
        upcode_length[0]=(byte)0xff;
        upcode_length[1]=(byte)0x10;
        String data_concatenation_string=CONST_TIME_STAMP+CONST_3_DIGIT_NUMBER;
        byte [] encrypteddataArray=new byte[16];
    //    encrypteddata= hexStringToByteArray(fromHexString(data));
        try {
            encrypteddataArray= EncryptionHelper.encryptData(data_concatenation_string.getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        System.arraycopy(upcode_length, 0, toatlData, 0, upcode_length.length);
        System.arraycopy(encrypteddataArray, 0, toatlData, upcode_length.length, encrypteddataArray.length);
        return  toatlData;

    }

    public static String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static byte[] send_16_bytes_data(){
        byte data [] =new byte[16];
        for (int i = 0; i <data.length ; i++) {
            data[i]= (byte) 0xff;
        }
        return  data;
    }

    public static byte[] dummyDescrypte(byte [] data){
        byte[] decryptdata=new byte[18];
       decryptdata[0]=(byte)0xff;
       decryptdata[1]=(byte)0xff;
        try {
            Log.d(TAG, "Vinay  Data going for decrypt "+getHexStringFromByteArray(data,false));
            decryptdata=Arrays.copyOfRange(data, 2, 17);
            EncryptionHelper.decryptData(decryptdata);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return  decryptdata;
    }

    public static byte[] createpacket(byte upcode,byte length, byte[] data ,boolean encryption_true_false) {
        /**
         * link:- https://www.javatpoint.com/how-to-merge-two-arrays-in-java#:~:text=Merging%20two%20arrays%20in%20Java,in%20the%20newly%20merged%20array.
         */
        if(encryption_true_false){
            byte[] upcode_length_array = new byte[2];
            byte[] encryptedData_array = new byte[16];
            upcode_length_array[0] = upcode;
            upcode_length_array[1] = length;
            try {
                encryptedData_array = EncryptionHelper.encryptData(data);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            byte[] completepacketData = new byte[upcode_length_array.length + encryptedData_array.length];
            System.arraycopy(upcode_length_array, 0, completepacketData, 0, upcode_length_array.length);
            System.arraycopy(encryptedData_array, 0, completepacketData, upcode_length_array.length, encryptedData_array.length);
            return completepacketData;
        }else {
            byte[] upcode_length_array = new byte[2];
            upcode_length_array[0] = upcode;
            upcode_length_array[1] = length;
            byte[] completepacketData=new byte[upcode_length_array.length+data.length];
            System.arraycopy(upcode_length_array, 0, completepacketData, 0, upcode_length_array.length);
            System.arraycopy(data, 0, completepacketData, upcode_length_array.length, data.length);
            return completepacketData;
        }

    }

    public static byte[] decryptPacket(byte[] data) {
        byte[] upcode_length_array = new byte[2];
        upcode_length_array[0] = data[0];// Upcode
        upcode_length_array[1] = data[1];// Length
        byte[] encryptedArray = Arrays.copyOfRange(data, 2, 18);//copy only encrypted data values discarding.(Upcode,length)
        byte[] decryptedDataArray=new byte[16];
        try {
            decryptedDataArray = EncryptionHelper.decryptData(encryptedArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        byte [] completepacketData=new byte[data.length];
        System.arraycopy(upcode_length_array, 0, completepacketData, 0, upcode_length_array.length);
        System.arraycopy(decryptedDataArray, 0, completepacketData, upcode_length_array.length, decryptedDataArray.length);
        return  completepacketData;
    }




}
