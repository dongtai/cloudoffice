package apps.transmanager.weboffice.util.server;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;


public abstract class RSACoder 
{
	public static final String KEY_ALGORITHM  = "RSA";
	public static final String SIGNATURE_ALGORITHM="MD5withRSA";
	public static final String PUBLIC_KEY = "RSAPublicKey";
	public static final String PRIVATE_KEY = "RSAPrivateKey";
	
	private static final int KEY_SIZE = 512;
	
	/**
	 * 签名
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data,byte[] privateKey) throws Exception
	{
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		
		Signature  signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return signature.sign();
	}
	
	/**
	 * 签名
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data,String privateKeyStr) throws Exception
	{
		byte[] privateKey = Base64.decodeBase64(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		
		Signature  signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return signature.sign();
	}
	
	/**
	 * 校验数据
	 * @param data
	 * @param publicKey
	 * @param sign
	 * @return
	 */
	public static boolean verify(byte[] data,byte[] publicKey,byte[] sign)throws Exception
	{
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		
		Signature  signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(sign);
	}
	
	/**
	 * 校验数据
	 * @param data
	 * @param publicKey
	 * @param sign
	 * @return
	 */
	public static boolean verify(byte[] data,String publicKeyStr,byte[] sign)throws Exception
	{
		byte[] publicKey = Base64.decodeBase64(publicKeyStr);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		
		Signature  signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(sign);
	}
	
	
	/**
	 * 取得私钥
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPrivateKey(Map<String,Object> keyMap) throws Exception
	{
		Key key = (Key)keyMap.get(PRIVATE_KEY);
		return key.getEncoded();
	}
	
	/**
	 * 取得公钥
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static byte[] getPublicKey(Map<String,Object> keyMap) throws Exception
	{
		Key key = (Key)keyMap.get(PUBLIC_KEY);
		return key.getEncoded();
	}
	
	/**
	 * 初始化密钥
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> generateKey()throws Exception
	{
		Map<String,String> keyMap = new HashMap<String,String>();
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		
		keyGen.initialize(KEY_SIZE);
		
		KeyPair keyPair = keyGen.generateKeyPair();		
		
		RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();	

		byte[] privateEncoder = privateKey.getEncoded();
		String privateKeyStr = Base64.encodeBase64String(privateEncoder);
		
		RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();		
		byte[] publicEncoder = publicKey.getEncoded();
		String publicKeyStr = Base64.encodeBase64String(publicEncoder);
		keyMap.put(PRIVATE_KEY,privateKeyStr);
		keyMap.put(PUBLIC_KEY,publicKeyStr);
		return keyMap;
	}
	
	public  static String EncodeByte(byte[] data)
	{
		return Base64.encodeBase64String(data);
	}
	
	public static byte[] DeCodeStr(String s)
	{
		return Base64.decodeBase64(s);
	}
	
}
