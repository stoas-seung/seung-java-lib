package seung.java.lib;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import seung.java.lib.arguments.SCharsetE;
import seung.java.lib.arguments.SObjectTypeE;

public class SUtilsT {

	private static final Logger logger = LoggerFactory.getLogger(SUtilsT.class);
	
	@Test
	public void test() {
		
		SCommonU sCommonU = new SCommonU();
		
		try {
			
			String hex = "66 69 6e 73 69 67 68 74".replaceAll(" ", "");
			
			logger.info(hex.replaceAll("[^0-9a-zA-Z]", ""));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
