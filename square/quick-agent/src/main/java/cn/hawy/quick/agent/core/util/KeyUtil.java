package cn.hawy.quick.agent.core.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by dhcao on 2018/1/16.
 * 读取证书文件为公私钥
 */
public class KeyUtil {

    /**
     * 获取证书私钥字符串
     * @return
     * @throws Exception
     */
    public static String getPrimaryKey(String privateKeyFile,String password)throws Exception{
    	KeyStore ks = KeyStore.getInstance("PKCS12");
    	// 获得密钥库文件流
        InputStream is = new FileInputStream(privateKeyFile);
        // 加载密钥库
        ks.load(is, password.toCharArray());
        // 关闭密钥库文件流
        is.close();
        
        @SuppressWarnings("rawtypes")
		Enumeration aliases = ks.aliases();
        String keyAlias = null;

        if (aliases.hasMoreElements()){
            keyAlias = (String)aliases.nextElement();
        }
        PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, password.toCharArray());
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        return privateKeyStr;
    }

   /* public static String getKeyAlias()throws Exception{
    	
        @SuppressWarnings("rawtypes")
		Enumeration aliases = ks.aliases();
        String keyAlias = null;

        if (aliases.hasMoreElements()){
            keyAlias = (String)aliases.nextElement();
        }
        return keyAlias;
    }*/


}
