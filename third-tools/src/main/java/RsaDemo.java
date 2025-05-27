import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

/**
 * @author hanyangyang
 * @since 2025/4/14
 */
public class RsaDemo {
    private static final String PUBLIC_KEY = "public_key";
    private static final String PRIVATE_KEY = "private_key";

    public static  String decryptParamByPub(String ciphertext, String rsaEncryptPublicKey) {
        try {
            RSA rsa = new RSA(null, rsaEncryptPublicKey);
            byte[] decrypt = rsa.decrypt(ciphertext.getBytes(StandardCharsets.UTF_8), KeyType.PublicKey);
            String plaintext = new String(decrypt, StandardCharsets.UTF_8);
            //返回解密后的字符串
            return plaintext;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }

    public static void genSign(JSONObject jsonObject){
        TreeMap bean = JSONUtil.toBean(jsonObject, TreeMap.class);
        for (Object key : bean.keySet()) {

        }
    }


    public static void main(String[] args) {



    }
}
