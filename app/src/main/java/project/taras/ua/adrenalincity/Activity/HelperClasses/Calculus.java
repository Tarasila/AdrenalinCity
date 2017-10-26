package project.taras.ua.adrenalincity.Activity.HelperClasses;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by Taras on 04.04.2017.
 */

public class Calculus {

    public static String generateMD5(String message){
        return hashString(message, "MD5");
    }

    public static String generateSHA1(String message){
        return hashString(message, "SHA-1");
    }

    private static String hashString(String message, String algorithm) {

        MessageDigest digest = null;
        byte[] hashedBytes = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
            hashedBytes = digest.digest(message.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return convertByteArrayToHexString(hashedBytes);
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
