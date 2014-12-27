package com.jerry.mail;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import sun.misc.BASE64Decoder;

/**
 * JAVA实现的DES加密解密算法
 * @author Jerry Wang
 *
 */
public class Encrypt {
	private Key key;
	private byte[] byteMi = null;
	private byte[] byteMing = null;
	private String strM = "";

	// 根据参数生成KEY
	public void setKey(String strKey) {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(new SecureRandom(strKey.getBytes()));
			this.key = generator.generateKey();
			generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 解密:以String密文输入,String明文输出
	public void setDesString(String strMi) {
		BASE64Decoder base64De = new BASE64Decoder();
		try {
			this.byteMi = base64De.decodeBuffer(strMi);
			this.byteMing = this.getDesCode(byteMi);
			this.strM = new String(byteMing, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			base64De = null;
			byteMing = null;
			byteMi = null;
		}
	}

	// 解密以byte[]密文输入,以byte[]明文输出
	private byte[] getDesCode(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	// 返回解密后的明文
	public String getStrM() {
		return strM;
	}
}