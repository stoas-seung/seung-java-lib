package seung.java.lib.extract.dart;

import java.util.UUID;

import seung.java.lib.arguments.SMap;

public class DartX0100VO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DartX0100VO() {
		
		// 결과코드
		this.put("rslCd"   , "9999");
		// 결과메시지
		this.put("rslMsg"  , "");
		
		// 기업코드
		this.put("crpCd"   , "E" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4));
		// 회사고유번호
		this.put("cikCd"   , "");
		// 기업명 한글
		this.put("crpNmKr" , "");
		// 기업명 영문
		this.put("crpNmEn" , "");
		// 기업명 상장
		this.put("crpNmMrk", "");
		// 종목코드
		this.put("itmCd"   , "");
		// 대표자명
		this.put("ceoNm"   , "");
		// 시장구분
		this.put("mrkTypNm", "");
		// 법인구분코드
		// P: 유가증권시장
		// A: 코스닥시장
		// N: 코넥스시장
		// E: 기타법인
		this.put("mrkTypCd", "");
		// 법인등록번호
		this.put("crpNo"   , "");
		// 사업자등록번호
		this.put("bizNo"   , "");
		// 회사주소
		this.put("crpAddr" , "");
		// 회사URL
		this.put("crpUrl"  , "");
		// IR URL
		this.put("crpUrlIR", "");
		// 전화번호
		this.put("crpTel"  , "");
		// 팩스번호
		this.put("crpFax"  , "");
		// 업종명
		this.put("bizTpNm" , "");
		// 설립일
		this.put("dtFnd"   , "");
		// 결산월
		this.put("clsMon"  , "");
	}
}
