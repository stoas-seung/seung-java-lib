package seung.java.lib.extract.dart;

import seung.java.lib.arguments.SMap;

public class DartX0300VO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DartX0300VO() {
		
		// 결과 코드
		this.put("rslCd" , "9999");
		// 결과 메시지
		this.put("rslMsg", "");
		
		// 회사조회코드
		this.put("cikCd" , "");
		// 문서조회코드
		this.put("rcpNo" , "");
		// 문서번호
		this.put("dcmNo" , "");
		// 문서저장경로
		this.put("dcmPth", "");
		
		// 보고서구분명
		this.put("rptTypNm", "");
		// 정정공시여부
		this.put("isCrr"   , "");
		// 문서작성일
		this.put("rptIssDt", "");
		
		// 회계기수
		this.put("accNo"   , "");
		// 회계기간시작일
		this.put("accDtStr", "");
		// 회계기간종료일
		this.put("accDtEnd", "");
		// 결산월
		this.put("accDtCls", "");
		
		// 종업원수
		this.put("numEmp"  , "");
		// 주주수
		this.put("numShr"  , "");
		// 표준산업분류코드
		this.put("indClsCd", "");
		// 통화ISO코드
		this.put("curIsoCd", "");
		
		// 단위
		this.put("unt_bs" , "");
		this.put("unt_is" , "");
		this.put("unt_cf" , "");
		this.put("unt_ci" , "");
	}
	
}
