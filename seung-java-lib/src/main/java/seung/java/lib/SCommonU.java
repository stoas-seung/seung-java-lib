package seung.java.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.DeflaterOutputStream;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import seung.java.lib.arguments.SCV;
import seung.java.lib.arguments.SCharsetE;
import seung.java.lib.arguments.SMap;
import seung.java.lib.arguments.SObjectTypeE;

public class SCommonU {

	private static final Logger logger = LoggerFactory.getLogger(SCommonU.class);
	
	public String repeate(int times) {
		return new String(new char[times]).replace("\0", SCV._S_SPACE);
	}
	
	/**
	 * @desc object to json format string
	 * @param object
	 * @param isPrettyPrinting
	 * @return
	 */
	public String toJsonString(Object object) {
		return toJsonString(object, false);
	}
	public String toJsonString(Object object, boolean isPrettyPrinting) {
		return buildGsonBuilder(isPrettyPrinting).create().toJson(object);
	}
	public Gson buildGson() {
		return buildGsonBuilder(false).create();
	}
	public GsonBuilder buildGsonBuilder(boolean isPrettyPrinting) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		if(isPrettyPrinting) gsonBuilder.setPrettyPrinting();
		gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
		return gsonBuilder;
	}
	
	/**
	 * @desc uuid
	 * @return e.g. 770386e0-1fff-4860-af18-e5c75aa72e1c
	 */
	public String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * @desc date to string
	 * @param pattern yyyy-MM-dd HH:mm:sss
	 * @return
	 */
	public String getDateString(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern.length() > 0 ? pattern : "yyyy-MM-dd HH:mm:sssss");
		return sdf.format(new Date());
	}
	public int getDateInteger(String pattern) {
		return Integer.parseInt(getDateString(pattern).replaceAll("[^0-9]", ""));
	}
	
	/**
	 * 
	 * @param sObjectTypeE
	 * @param sCharsetE
	 * @param src
	 * @return
	 */
	public byte[] toByteArray(
			SObjectTypeE sObjectTypeE
			, SCharsetE sCharsetE
			, Object object
			) {
		
		byte[] bytes = null;
		
		switch (sObjectTypeE) {
			case _STRING:
				bytes = ((String) object).getBytes(sCharsetE.getCharset());
				break;
			case _HEX:
				bytes = new BigInteger(((String) object).replaceAll("[^a-zA-Z0-9]", ""), 16).toByteArray();
				break;
			default:
				break;
		}
		
		return bytes;
	}
	
	/**
	 * @desc digest
	 * @param algorithm
	 * @param src
	 * @return
	 */
	public String digest(String algorithm, String src) {
		return digest(SCharsetE._UTF8, algorithm, src);
	}
	public String digest(SCharsetE sCharsetE, String algorithm, String src) {
		String digest = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			byte[] hash = messageDigest.digest(src.getBytes(sCharsetE.getCharset()));
			digest = new String(Hex.encode(hash));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}
		return digest;
	}
	
	public String compress(String src) {
		String compress = "";
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			OutputStream outputStream = new DeflaterOutputStream(byteArrayOutputStream);
			outputStream.write(src.getBytes(SCV._S_UTF8));
			outputStream.close();
			compress = new String(byteArrayOutputStream.toByteArray(), SCV._S_UTF8);
		} catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		}
		return compress;
	}
	
	public SMap getSystemInfo() {
		
		SMap systemInfo = new SMap();
		
		try {
			
			InetAddress inetAddress = InetAddress.getLocalHost();
			
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface = null;
			byte[] macBytes = null;
			StringBuilder stringBuilder = new StringBuilder();
			while(networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();
				macBytes = networkInterface.getHardwareAddress();
				if(macBytes != null) {
					stringBuilder.append(",");
					for(int i = 0; i < macBytes.length; i++) {
						stringBuilder.append(String.format("%02X%s", macBytes[i], (i < macBytes.length - 1) ? "-" : ""));
					}
				}
			}
			
			systemInfo.put("osName", System.getProperty("os.name"));
			systemInfo.put("hostName", inetAddress.getHostName());
			systemInfo.put("ip", inetAddress.getHostAddress());
			systemInfo.put("mac", stringBuilder.length() > 0 ? stringBuilder.toString().substring(1) : "");
			
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		} catch (SocketException e) {
			logger.error(e.getMessage());
		}
		
		return systemInfo;
	}
	
	/**
	 * @desc get mac address
	 */
	@SuppressWarnings("unused")
	private String getMacAddress(InetAddress inetAddress) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
			byte[] macBytes = networkInterface.getHardwareAddress();
			
			for(int i = 0; i < macBytes.length; i++) {
				stringBuilder.append(String.format("%02X%s", macBytes[i], (i < macBytes.length - 1) ? "-" : ""));
			}
			
		} catch (SocketException e) {
			logger.error(e.getMessage());
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * @desc check is vm or not
	 */
	@SuppressWarnings("unused")
	private boolean isVMMac(byte[] mac) {
		
		if(null == mac) return false;
		
		byte invalidMacs[][] = {
			//VMWare
			{0x00, 0x05, 0x69}
			, {0x00, 0x1C, 0x14}
			, {0x00, 0x0C, 0x29}
			, {0x00, 0x50, 0x56}
			//Virtualbox
			, {0x08, 0x00, 0x27}
			, {0x0A, 0x00, 0x27}
			//Virtual-PC
			, {0x00, 0x03, (byte)0xFF}
			//Hyper-V
			, {0x00, 0x15, 0x5D}
		};
		
		for(byte[] invalid: invalidMacs){
			if(invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) return true;
		}
		
		return false;
	}
	
	public String doubleToString(Double d) {
		return Long.toString(d.longValue());
	}
}
