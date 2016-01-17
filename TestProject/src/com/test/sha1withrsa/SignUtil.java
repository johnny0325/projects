package com.test.sha1withrsa;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

/*import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;*/

/*
 * SHA1WithRSA:用SHA算法进行签名，接收端验证签名
 */
public class SignUtil {
	public static final String DEFAULT_MSG_CHARSET = "GBK";
	
	private static final String SIGN_ALGORITHM = "SHA1withRSA";
	private static final String DEFAULT_CERTIFCATE_TYPE = "X.509";                       /** 缺省证书类型 X.509 */
	private static final String DEFAULT_KEYSTORE_TYPE = "PKCS12";                    	/** 缺省证书密钥库类型 pfx */
	
	private static final File keyStoreFile = new File("D:/test.pfx");//可以通过工具将Java KeyStore文件转换为微软的.pfx文件
	private static final String keyStorePwd = "123456";
	private static final File certFile = new File("D:/test.crt");
	
	private static Signature sign = null;
	private static PublicKey publicKey = null;
	private static PrivateKey privateKey;
	
	static{
		initKeyByFile();
	}

	private static void initKeyByFile(){
		try {
			initPublicKey();
			initPrivateKey();
			sign = Signature.getInstance(SIGN_ALGORITHM);
		} catch (Exception e) {
			System.out.println("初始化公私钥和签名算法出错! "+e.getMessage());
		}
	}
	
	private static void initPublicKey() throws Exception{
		Certificate cert=getCertficateFromFile(certFile);
		publicKey = cert.getPublicKey();
	}
	
	private static void initPrivateKey() throws Exception{
		privateKey = getKeyStorePrivateKey(keyStoreFile,keyStorePwd);
	}
	
	/**
	 * 对象消息进行签名处理
	 * 返回签名串(BASE64)
	 * @return String
	 */
	public static String signMsg(String xmlMsg) {
		String base64Data = null;
		try {
			// 设置加密散列码用的私钥
			sign.initSign(privateKey);
			// 设置散列算法的输入
			sign.update(xmlMsg.getBytes(DEFAULT_MSG_CHARSET));
			// 进行散列，对产生的散列码进行加密并返回
			byte[] data = sign.sign();
			// 把加密后散列（即签名）加到消息中
			//base64Data = Base64.encodeBase64String(data);
			System.out.println("消息数字签名成功！"+data.length+" "+base64Data.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base64Data;
	}

	/**
	 * 对象消息进行验签
	 * 返回: true 成功
	 *         false 失败
	 * @return boolean
	 */
	public static boolean verifyMsg(String xmlMsg,String signInfo) {
		try {
			sign.initVerify(publicKey);
			// 设置散列算法的输入
			sign.update(xmlMsg.getBytes(DEFAULT_MSG_CHARSET));
			//进行散列计算，比较计算所得散列码是否和解密的散列码是否一致。 一致则验证成功，否则失败
			/*if (sign.verify(Base64.decodeBase64(signInfo))) {//数字签名验证成功
				return true;
			} */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/**
	 * get Certificate from certFile
	 * 
	 * @param certFile
	 * @return
	 * @throws CertOperateException
	 */
	private static Certificate getCertficateFromFile(File certFile) throws Exception{
		Certificate certificate = null;
		InputStream certInput = null;
		try {
			certInput = new BufferedInputStream(new FileInputStream(certFile));
			CertificateFactory certFactory = CertificateFactory.getInstance(DEFAULT_CERTIFCATE_TYPE);
			certificate = certFactory.generateCertificate(certInput);
		} catch (Exception ex) {
			throw new Exception("提取证书文件[" + certFile.getName() + "]认证失败,原因[" + ex.getMessage() + "]");
		} finally {
			//IOUtils.closeQuietly(certInput);
		}
		return certificate;
	}
	
	/**
	 * get PrivateKey from keyStorFile(pfx)
	 * 
	 * @param keyStoreFile
	 * @param keyStorePwd
	 * @return
	 * @throws CertOperateException
	 */
	private static PrivateKey getKeyStorePrivateKey(File keyStoreFile, String keyStorePwd) throws Exception{
		KeyStore ks = getLoadKeyStore(keyStoreFile, keyStorePwd);
		PrivateKey privateKey = null;

		try {
			Enumeration<String> aliasEnum = ks.aliases();
			while (aliasEnum.hasMoreElements()) {
				String alias = aliasEnum.nextElement();
				if (ks.isKeyEntry(alias)) {
					Key key = ks.getKey(alias, keyStorePwd.toCharArray());
					if (key instanceof PrivateKey) {
						privateKey = (PrivateKey) key;
						break;
					}
				}
			}
		} catch (Exception ex) {
			throw new Exception("获取私钥[" + keyStoreFile.getName() + "]出现异常,口令[" + keyStorePwd + "],原因["+ ex.getMessage() + "]");
		}
		return privateKey;
	}
	
	/**
	 * get KeyStore from keyStoreFile
	 * 
	 * @param keyStoreFile
	 * @param keyStorePwd
	 * @return
	 * @throws CertOperateException
	 */
	private static KeyStore getLoadKeyStore(File keyStoreFile, String keyStorePwd) throws Exception {
		KeyStore ks = null;
		InputStream ksInput = null;
		try {
			//ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE, new BouncyCastleProvider());
			ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
			ksInput = new BufferedInputStream(new FileInputStream(keyStoreFile));
			ks.load(ksInput, keyStorePwd.toCharArray());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("加载证书密钥库[" + keyStoreFile.getName() + "]失败,原因[" + ex.getMessage() + "]");
		} finally {
			//IOUtils.closeQuietly(ksInput);
		}
		return ks;
	}
	
	public static void main(String[] args){
		String xmlMessage = "xmlmessage";
		String signInfo = signMsg(xmlMessage);
		boolean signCheck = verifyMsg(xmlMessage,signInfo);
		System.out.println(signCheck);
	}
}
