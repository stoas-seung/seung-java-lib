package seung.java.lib.extract.dart;

import seung.java.lib.arguments.SMap;

public class DartX0301VO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DartX0301VO() {
		
		// 결과 코드
		this.put("rslCd" , "9999");
		// 결과 메시지
		this.put("rslMsg", "");
		
		// 문서구분코드
		// BS: balance sheet
		// IS: income statement
		// CI: statement of comprehensive income
		// CF: statement of cash flows
		this.put("docTypCd" , "");
		// 회계기수
		this.put("accNo"    , "");
		// 계정순서
		this.put("accOrd"   , "");
		// 계정명
		this.put("accNm"    , "");
		// 계정단계
		this.put("accDpt"   , "");
		// 계정코드
		this.put("accNmCd"  , "");
		// 계정금액
		this.put("accVal"   , "");
		// 계정금액부호
		this.put("accValSgn", "");
		
	}
	
}
