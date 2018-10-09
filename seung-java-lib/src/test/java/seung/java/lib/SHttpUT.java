package seung.java.lib;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import seung.java.lib.arguments.SMap;
import seung.java.lib.extract.SExU;
import seung.java.lib.extract.dart.DartX0100VO;
import seung.java.lib.extract.dart.SExDartU;
import seung.java.lib.http.SHttpU;

public class SHttpUT {

	private static final Logger logger = LoggerFactory.getLogger(SHttpUT.class);
	
	@Test
	public void test() {
		
		String test = "x0101";
		test = "x0300";
//		test = "x0200";
		
		
		SMap testMap = new SMap();
		switch(test) {
		
			case "x0101":
				
				testMap.put("orgCd", "dart");
				testMap.put("jobCd", "x0101");
				testMap.put("cikCd", "00843830");
				
				break;
				
			case "x0200":
				
				testMap.put("orgCd", "dart");
				testMap.put("jobCd", "x0200");
				testMap.put("startDate", "20180720");
				testMap.put("endDate", "20180729");
				testMap.put("corporationType" , "P");
				
				break;
				
			case "x0300":
				
				testMap.put("orgCd", "dart");
				testMap.put("jobCd", "x0300");
				testMap.put("rcpNo", "20170331004619");
//				testMap.put("rcpNo", "20170331000173");
				testMap.put("lang" , "ko");
				
				break;
				
			default:
				break;
		}
		
		logger.info(new SExU().extract(testMap).toString(true));
	}
}
