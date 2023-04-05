package com.lexisnexis.web.pricing.db.util;

import org.apache.commons.codec.binary.Base64;

public class DataUtil {
	public static String encryptString(String strToEncrypt)
	{
		byte[] encodedBytes = Base64.encodeBase64(strToEncrypt.getBytes());
		System.out.println("Encrypted: "+ new String(encodedBytes));
		
		return new String(encodedBytes);
	}
	
	public static String decryptString(String strToDecrypt)
	{
		byte[] decodedBytes = Base64.decodeBase64(strToDecrypt);
		
		
		return new String(decodedBytes);
	} 
}
