package com.houbank.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author duyuanfei
 *
 */
public class AESUtils {
	public static final Charset CHARSET = Charset.forName("utf-8");

    public static final byte KEYSTRSZIE = 16;

    public static final String ALGORITHM = "AES";

    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";



    /**
     * AES/CBC/NoPadding encrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @param secretKeyBytes
     * @param intVectorBytes
     * @param input
     * @return
     */
    public static byte[] encryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);
            int inputLength = input.length;
            int srcLength;
            
            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            int blockSize = cipher.getBlockSize();
            byte[] srcBytes;
            if (0 != inputLength % blockSize) {
                srcLength = inputLength + (blockSize - inputLength % blockSize);
                srcBytes = new byte[srcLength];
                System.arraycopy(input, 0, srcBytes, 0, inputLength);
            } else {
                srcBytes = input;
            }
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(srcBytes);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES/CBC/NoPadding decrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @param secretKeyBytes
     * @param intVectorBytes
     * @param input
     * @return
     */
    public static byte[] decryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(input);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     *
     * @param secretStr
     * @param inputStr
     * @return
     */
    public static byte[] encode(String secretStr, String inputStr) {
        /*if (keyStrSzie != secretStr.length()) {
            return null;
        }*/
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = Arrays.copyOfRange(secretKeyBytes, 0, 16);
        System.out.println("----iv----"+new String(ivBytes));
        byte[] inputBytes = inputStr.getBytes(CHARSET);

        byte[] outputBytes = encryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     * 并对加密后的字节数组调用 sun.misc.BASE64Encoder.encode 方法，
     * 转换成 base64 字符串返回。
     *
     * @param secretStr
     * @param inputStr
     * @return
     */
    public static String strEncodBase64(String secretStr, String inputStr){
        String base64Str = Base64.encodeBase64String(encode(secretStr, inputStr));
        return base64Str;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     *
     * @param secretStr
     * @param inputBytes
     * @return
     */
    public static byte[] decode(String secretStr, byte[] inputBytes){
        /*if (keyStrSzie != secretStr.length()) {
            return null;
        }*/
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = Arrays.copyOfRange(secretKeyBytes, 0, 16);

        byte[] outputBytes = decryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     * 并对加密后的字节数组调用 sun.misc.BASE64Encoder.encode 方法，
     * 转换成 base64 字符串返回。
     * 
     * （仅作为测试用途，具体加密流程以接口文档为准）
     *
     * @param secretStr
     * @param inputStr
     * @return
     * @throws IOException
     */
    public static String base64StrDecode(String secretStr, String inputStr){
        byte[] inputBytes;
        inputBytes = Base64.decodeBase64(inputStr);
        String outputStr = new String(decode(secretStr, inputBytes), CHARSET);
        System.out.println("base64Decode > base64 decrypt " + outputStr);
        return outputStr;
    }


    public static void main(String[] args) {
        String key = "3FADAE9950B216AF";

        Map<String, Object> userAttr = new HashMap<>(4);
        userAttr.put("idNo", "421202199501233266");
        userAttr.put("mobile", "13701234567");
        userAttr.put("name", "张三");

        String userAttrStr = JSONObject.toJSONString(userAttr);

        String enUserAttr =  strEncodBase64(key, userAttrStr);
        System.out.println("encodedStr = " + enUserAttr);
        System.out.println("decodedStr = " + new String(base64StrDecode(key,enUserAttr)));
    }
}

