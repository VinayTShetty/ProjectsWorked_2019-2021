package com.proofofLife.Encryption;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHelper {
  //  private static final byte[] KeyBytes = {0x39, 0x55, 0x17, 0x12, 0x13, (byte) 0x88, (byte) 0x98, 0x47, (byte) 0x90, 0x08, 0x49, 0x21, 0x12, (byte) 0xd4, 0x11, 0x29};
    private static final byte[] KeyBytes ={ 0x3A, 0x09, 0x44, 0x62, (byte) 0xFD, 0x62, 0x10, (byte) 0xCD, (byte) 0xE8, 0x74, 0x42, (byte) 0xCA, (byte)0xA9, (byte)0xD7, 0x18, (byte)0xF9 };

    public static byte[] decryptData(byte[] encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
// Decrypt the text
// Create key and cipher
        Key aesKey = new SecretKeySpec(KeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
     //   Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding ");//PKCS5Padding
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
      byte[] decrypted = cipher.doFinal(encryptedData);
     //   byte[] decrypted = cipher.doFinal(Base64.decode(new String(encryptedData, StandardCharsets.UTF_8), Base64.NO_WRAP));
        return decrypted;
    }

    public static byte[] encryptData(byte[] inputData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
// Create key and cipher
        Key aesKey = new SecretKeySpec(KeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");  // AES/ECB/NoPadding    // AES/CBC/PKCS5Padding
// encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(inputData);
        return encrypted;
    }
}
