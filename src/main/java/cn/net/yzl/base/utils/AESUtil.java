package cn.net.yzl.base.utils;

import android.util.Log;



import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import java.util.Date;


public class AESUtil {

    private static final String IV_STRING = "16-Bytes--String";
    public static final String KEY = "yzl_staff&oa#514";



    public static String encryptAES(Date content) {
        String reslut = null;
        try {
            reslut = encryptAES(DateUtil.datetoString(content,"yyyy-MM-dd hh:mm:ss"), KEY);
        } catch (Exception e) {
            Log.e("AESUtil","AES加密失败.{}");
        }
        return reslut;
    }

    public static String encryptAES(String content, String key){
        try{
            byte[] byteContent = content.getBytes("UTF-8");
            // 注意，为了能与 iOS 统一
            // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");

            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(byteContent);
            // 同样对加密后数据进行 base64 编码
            return Base64.encodeToString(encryptedBytes,Base64.NO_WRAP);
        }catch (Exception ex){

        }
        return "";
    }

    public static String decryptAES(String content, String key) {
        try{
            // base64 解码
            byte[] encryptedBytes =Base64.decode(content, Base64.NO_WRAP);
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, "UTF-8");
        }catch (Exception ex){

        }
        return "";
    }

}
