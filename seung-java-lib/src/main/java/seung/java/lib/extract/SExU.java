package seung.java.lib.extract;

import seung.java.lib.arguments.SMap;
import seung.java.lib.extract.dart.SExDartU;

public class SExU {

	public SMap extract(SMap sMap) {
		
		SMap resultMap = null;
		
		switch(sMap.getString("orgCd")) {
			case "dart":
				resultMap = new SExDartU().extract(sMap);
				break;
			case "yahoo":
//				resultMap = new SExYahooU().extract(sMap);
				break;
			case "daum":
//				resultMap = new SExDaumU().extract(sMap);
				break;
			default:
				break;
		}
		
		return resultMap;
	}
	
}
