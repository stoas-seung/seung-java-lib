package seung.java.lib.extract.dart;

import seung.java.lib.arguments.SMap;

public class DartX0200VO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DartX0200VO() {
		
		// 결과코드
		this.put("rslCd" , "9999");
		// 결과메시지
		this.put("rslMsg", "");
		
		// 보고서번호
		this.put("rcpNo" , "");
		// 회사고유번호
		this.put("cikCd" , "");
		
		// 기업명
		this.put("crpNm" , "");
		// 보고서명
		this.put("rcpNm" , "");
		// 제출인
		this.put("sbmNm" , "");
		// 접수일자
		this.put("rcvDt" , "");
		// 비고
		this.put("note"  , "");
		
		// 문서번호
		this.put("dcmNo" , "");
		// 수집여부
		this.put("isExt" , "0");
		
	}
	
}
