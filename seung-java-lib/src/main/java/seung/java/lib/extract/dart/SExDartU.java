package seung.java.lib.extract.dart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import seung.java.lib.SExcelU;
import seung.java.lib.arguments.SMap;
import seung.java.lib.http.SHttpU;

public class SExDartU {

	private static final Logger logger = LoggerFactory.getLogger(SExDartU.class);
	
	public SMap extract(SMap sMap) {
		
		SMap resultMap = new SMap();
		resultMap.put("rslCd" , "9999");
		resultMap.put("rslMsg", "");
		
		try {
			
			// corporation list
			if(sMap.getString("jobCd").indexOf("x0100") > -1) resultMap.put("x0100", x0100(sMap));
			// corporation information
			if(sMap.getString("jobCd").indexOf("x0101") > -1) resultMap.put("x0101", x0101(sMap));
			// report list
			if(sMap.getString("jobCd").indexOf("x0200") > -1) resultMap.put("x0200", x0200(sMap));
			// report excel
			if(sMap.getString("jobCd").indexOf("x0300") > -1) resultMap.put("x0300", x0300(sMap));
			// report: INF
			if(sMap.getString("jobCd").indexOf("x0301") > -1) resultMap.put("x0301", x0301(sMap));
			// report: BS
			if(sMap.getString("jobCd").indexOf("x0302") > -1) resultMap.put("x0302", x0302(sMap));
			// report: IS
			if(sMap.getString("jobCd").indexOf("x0303") > -1) resultMap.put("x0303", x0303(sMap));
			// report: CF
			if(sMap.getString("jobCd").indexOf("x0304") > -1) resultMap.put("x0304", x0304(sMap));
			
			resultMap.put("rslCd", "0000");
			
		} catch (Exception e) {
			resultMap.put("rslCd" , "9999");
			resultMap.put("rslMsg", e.getMessage());
		}
		
		return resultMap;
	}
	
	private SMap x0100(SMap sMap) {
		
		int count = 0;
		
		SMap x0100VO = new SMap();
		x0100VO.put("rslCd" , "9999");
		x0100VO.put("rslMsg", "");
		
		SHttpU x0100HttpU = null;
		String responseText = null;
		try {
			
			ArrayList<SMap> x0100SL = new ArrayList<SMap>();
			SMap dartX0100VO = null;
			
			int totalNum = -1;
			int pageNum = -1;
			int currentPage = sMap.containsKey("pageNo") ? sMap.getInt("pageNo") : 1;
			String pageInfo = "";
			Document table = null;
			Elements tds = null;
			while(true) {
				
				if(sMap.containsKey("sleep")) Thread.sleep(sMap.getInt("sleep"));
				x0100HttpU = new SHttpU();
				
				x0100HttpU.setRequestUrl("dart.fss.or.kr/corp/searchCorpL.ax");
				x0100HttpU.setRequestMethod("POST");
				x0100HttpU.addRequestHeader("Content-Type"      , "application/x-www-form-urlencoded");
				x0100HttpU.addRequestHeader("X-Requested-With"  , "XMLHttpRequest");
				x0100HttpU.addRequestParameter("currentPage"    , "" + currentPage);
				x0100HttpU.addRequestParameter("maxResults"     , "");//default: 300
				x0100HttpU.addRequestParameter("maxLinks"       , "");
				x0100HttpU.addRequestParameter("searchIndex"    , "");
				x0100HttpU.addRequestParameter("textCrpNmAddPer", "");
				x0100HttpU.addRequestParameter("textCrpNm"      , "");
				// P: 유가증권시장
				// A: 코스닥시장
				// N: 코넥스시장
				// E: 기타법인
				x0100HttpU.addRequestParameter("corporationType", sMap.containsKey("corporationType") ? sMap.getString("corporationType") : "");
				x0100HttpU.setReadTimeout(10 * 1000);
				
				x0100HttpU.httpRequest();
				
				responseText = x0100HttpU.getResponseText();
				
				if(responseText == null) throw new Exception("0001||response is null.");
				if(responseText.indexOf("일치하는 회사명이 없습니다.") > -1) throw new Exception("0000||no data.");
				if(responseText.indexOf("<caption>회사명 선택 표</caption>") == -1) throw new Exception("0002||table not exist.");
				
				if(totalNum == -1) {
					pageInfo = responseText.split("<p class=\"page_info\">")[1].split("</p>")[0];
					totalNum = Integer.parseInt(pageInfo.split("\\[")[2].replaceAll("[^0-9]", ""));
					pageNum = Integer.parseInt(pageInfo.split("\\[")[1].split("\\/")[1].replaceAll("[^0-9]", ""));
					currentPage = Integer.parseInt(pageInfo.split("\\[")[1].split("\\/")[0].replaceAll("[^0-9]", ""));
				}
				logger.info(String.format("%s / %s, %s", "" + currentPage, "" + pageNum, "" + totalNum));
				
				responseText = "<table>" + responseText.split("<caption>회사명 선택 표</caption>")[1].split("</table>")[0] + "</table>";
				
				table = Jsoup.parse(responseText);
				for(Element tr : table.select("tbody").select("tr")) {
					
					tds = tr.select("td");
					dartX0100VO = new DartX0100VO();
					dartX0100VO.put("cikCd", tds.get(0).select("input[name=hiddenCikCD1]").val());
					
					// contains detail
					if("Y".equals(sMap.getString("x0101"))) {
						
						sMap.put("cikCd", dartX0100VO.getString("cikCd"));
						dartX0100VO = (DartX0100VO) x0101(sMap);
						
					} else {
						
						dartX0100VO.put("crpNmKr", tds.get(0).text());
						dartX0100VO.put("ceoNm"  , tds.get(1).text());
						dartX0100VO.put("itmCd"  , tds.get(2).text());
						dartX0100VO.put("bizTpNm", tds.get(3).text());
						dartX0100VO.put("rslCd"  , "0000");
						dartX0100VO.put("rslMsg" , "");
						
					}
					
					x0100SL.add(dartX0100VO);
					++count;
//					if(count % 10 == 0)
//						System.out.println("count: " + count);
//					if(count % 5 == 0) break;
				}
				
				if(pageNum <= currentPage) break;
				currentPage++;
				
//				break;
			}
			
			x0100VO.put("totalNum"   , totalNum);
			x0100VO.put("pageNum"    , pageNum);
			x0100VO.put("currentPage", currentPage);
			x0100VO.put("x0100SL"    , x0100SL);
			x0100VO.put("rslCd"      , "0000");
			
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("\\|\\|") > -1) {
				x0100VO.put("rslCd" , e.getMessage().split("\\|\\|")[0]);
				x0100VO.put("rslMsg", e.getMessage().split("\\|\\|")[1]);
			} else {
				x0100VO.put("rslCd" , "9999");
				x0100VO.put("rslMsg", e.getMessage());
			}
		}
		
		return x0100VO;
	}
	
	private SMap x0101(SMap sMap) {
		
		SMap dartX0101VO = new DartX0100VO();
		
		SHttpU x0101HttpU = null;
		String responseText = null;
		try {
			
			Document table = null;
			Elements trs = null;
			
			if(sMap.containsKey("sleep")) Thread.sleep(sMap.getInt("sleep"));
			x0101HttpU = new SHttpU();
			
			x0101HttpU.setRequestUrl("dart.fss.or.kr/dsae001/selectPopup.ax");
			x0101HttpU.setRequestMethod("POST");
			x0101HttpU.addRequestHeader("Content-Type"    , "application/x-www-form-urlencoded");
			x0101HttpU.addRequestHeader("X-Requested-With", "XMLHttpRequest");
			x0101HttpU.addRequestParameter("selectKey"    , sMap.getString("cikCd"));
			
			x0101HttpU.httpRequest();
			
			responseText = x0101HttpU.getResponseText();
			
			if(responseText == null) throw new Exception("0001||response is null.");
			if(responseText.indexOf("<tbody>") == -1) throw new Exception("0001||table not exist.");
			
			responseText = "<table>" + responseText.split("<tbody>")[1].split("</tbody>")[0] + "</table>";
			table = Jsoup.parse(responseText);
			
			trs = table.select("tr");
			
			dartX0101VO.put("cikCd"   , sMap.getString("cikCd"));
			dartX0101VO.put("crpNmKr" , trs.get(0).select("td").get(0).text());
			dartX0101VO.put("crpNmEn" , trs.get(1).select("td").get(0).text());
			dartX0101VO.put("crpNmMrk", trs.get(2).select("td").get(0).text());
			dartX0101VO.put("itmCd"   , trs.get(3).select("td").get(0).text());
			if(dartX0101VO.getString("itmCd").length() > 4) dartX0101VO.put("crpCd", dartX0101VO.getString("itmCd").substring(0, 5));
			dartX0101VO.put("ceoNm"   , trs.get(4).select("td").get(0).text());
			dartX0101VO.put("mrkTypNm", trs.get(5).select("td").get(0).text());
			
			if("유가증권시장".equals(dartX0101VO.getString("mrkTypNm"))) dartX0101VO.put("mrkTypCd", "P");
			if("코스닥시장".equals(dartX0101VO.getString("mrkTypNm"))) dartX0101VO.put("mrkTypCd", "A");
			if("코스넥시장".equals(dartX0101VO.getString("mrkTypNm"))) dartX0101VO.put("mrkTypCd", "N");
			if("기타법인".equals(dartX0101VO.getString("mrkTypNm"))) dartX0101VO.put("mrkTypCd", "E");
			
			dartX0101VO.put("crpNo"   , trs.get(6).select("td").get(0).text());
			dartX0101VO.put("bizNo"   , trs.get(7).select("td").get(0).text());
			dartX0101VO.put("crpAddr" , trs.get(8).select("td").get(0).text());
			dartX0101VO.put("crpUrl"  , trs.get(9).select("td").get(0).text());
			dartX0101VO.put("crpUrlIR", trs.get(10).select("td").get(0).text());
			dartX0101VO.put("crpTel"  , trs.get(11).select("td").get(0).text());
			dartX0101VO.put("crpFax"  , trs.get(12).select("td").get(0).text());
			dartX0101VO.put("bizTpNm" , trs.get(13).select("td").get(0).text());
			dartX0101VO.put("dtFnd"   , trs.get(14).select("td").get(0).text().replaceAll("[^0-9]", ""));
			dartX0101VO.put("clsMon"  , trs.get(15).select("td").get(0).text().replaceAll("[^0-9]", ""));
			dartX0101VO.put("rslCd"   , "0000");
			dartX0101VO.put("rRslMsg" , "");
			
		} catch (Exception e) {
			if(e.getMessage().indexOf("\\|\\|") > -1) {
				dartX0101VO.put("rslCd" , e.getMessage().split("\\|\\|")[0]);
				dartX0101VO.put("rslMsg", e.getMessage().split("\\|\\|")[1]);
			} else {
				dartX0101VO.put("rslCd" , "9999");
				dartX0101VO.put("rslMsg", e.getMessage());
			}
		}
		
		return dartX0101VO;
	}
	
	private SMap x0200(SMap sMap) {
		
		int count = 0;
		
		SMap x0200VO = new SMap();
		x0200VO.put("rslCd" , "9999");
		x0200VO.put("rslMsg", "");
		
		SHttpU x0200HttpU = null;
		String responseText = null;
		try {
			
			if(!sMap.containsKey("startDate") || sMap.getString("startDate").length() != 8)
				throw new Exception("8001||[startDate] can not be null.");
			if(!sMap.containsKey("endDate") || sMap.getString("endDate").length() != 8)
				throw new Exception("8002||[endDate] can not be null.");
			
			ArrayList<SMap> x0200SL = new ArrayList<SMap>();
			SMap dartX0200VO = null;
			
			int totalNum = -1;
			int pageNum = -1;
			int currentPage = sMap.containsKey("pageNo") ? sMap.getInt("pageNo") : 1;
			String pageInfo = "";
			Document table = null;
			Elements tds = null;
			while(true) {
				
				if(sMap.containsKey("sleep")) Thread.sleep(sMap.getInt("sleep"));
				x0200HttpU = new SHttpU();
				
				x0200HttpU.setReadTimeout(10 * 1000);
				x0200HttpU.setRequestUrl("dart.fss.or.kr/dsab002/search.ax");
				x0200HttpU.setRequestMethod("POST");
				x0200HttpU.addRequestHeader("Content-Type"    , "application/x-www-form-urlencoded");
				x0200HttpU.addRequestHeader("X-Requested-With", "XMLHttpRequest");
				
				x0200HttpU.addRequestParameter("currentPage"         , "" + currentPage);
				x0200HttpU.addRequestParameter("maxResults"          , "100");
				x0200HttpU.addRequestParameter("maxLinks"            , "10");
				x0200HttpU.addRequestParameter("sort"                , "date");
				x0200HttpU.addRequestParameter("series"              , "asc");
				x0200HttpU.addRequestParameter("textCrpCik"          , "");
				x0200HttpU.addRequestParameter("reportNamePopYn"     , "Y");
				x0200HttpU.addRequestParameter("textCrpNm"           , "");
				x0200HttpU.addRequestParameter("textPresenterNm"     , "");
				x0200HttpU.addRequestParameter("startDate"           , sMap.getString("startDate"));
				x0200HttpU.addRequestParameter("endDate"             , sMap.getString("endDate"));
//				x0200HttpU.addRequestParameter("finalReport"         , "recent");
				x0200HttpU.addRequestParameter("typesOfBusiness"     , "all");
				// P: 유가증권시장
				// A: 코스닥시장
				// N: 코넥스시장
				// E: 기타법인
				x0200HttpU.addRequestParameter("corporationType"     , sMap.containsKey("corporationType") ? sMap.getString("corporationType") : "all");
				x0200HttpU.addRequestParameter("closingAccountsMonth", "all");
				x0200HttpU.addRequestParameter("reportName"          , "");
				// A001: 사업보고서
				// A002: 반기보고서
				// A003: 분기보고서
				x0200HttpU.addRequestParameter("publicType"          , "A001");
				x0200HttpU.addRequestParameter("publicType"          , "A002");
				x0200HttpU.addRequestParameter("publicType"          , "A003");
				
				x0200HttpU.httpRequest();
				
				responseText = x0200HttpU.getResponseText();
				
				if(responseText == null) throw new Exception("0001||response is null.");
				if(responseText.indexOf("조회 결과가 없습니다.") > -1) throw new Exception("0000||no data.");
				if(responseText.indexOf("<caption>공시서류검색 목록</caption>") == -1) throw new Exception("0002||table not exist.");
				
				if(totalNum == -1) {
					pageInfo = responseText.split("<p class=\"page_info\">")[1].split("</p>")[0];
					totalNum = Integer.parseInt(pageInfo.split("\\[")[2].replaceAll("[^0-9]", ""));
					pageNum = Integer.parseInt(pageInfo.split("\\[")[1].split("\\/")[1].replaceAll("[^0-9]", ""));
					currentPage = Integer.parseInt(pageInfo.split("\\[")[1].split("\\/")[0].replaceAll("[^0-9]", ""));
				}
				logger.info(String.format("%s / %s, %s", "" + currentPage, "" + pageNum, "" + totalNum));
				
				responseText = "<table>" + responseText.split("<caption>공시서류검색 목록</caption>")[1].split("</table>")[0] + "</table>";
				
				table = Jsoup.parse(responseText);
				for(Element tr : table.select("tbody").select("tr")) {
					
					tds = tr.select("td");
					dartX0200VO = new DartX0200VO();
					dartX0200VO.put("rcpNo" , tds.get(2).select("a").attr("onclick").split("'")[1]);
					dartX0200VO.put("cikCd" , tds.get(1).select("a").attr("onclick").split("'")[1]);
					dartX0200VO.put("crpNm" , tds.get(1).select("a").text().trim());
					dartX0200VO.put("rcpNm" , tds.get(2).select("a").text().trim());
					dartX0200VO.put("sbmNm" , tds.get(3).text().trim());
					dartX0200VO.put("rcvDt" , tds.get(4).text().trim().replaceAll("[^0-9]", ""));
					dartX0200VO.put("note"  , tds.get(5).select("img").outerHtml().trim());
					
					// x0201: extract document number
					/*
					if(sMap.containsKey("sleep")) Thread.sleep(sMap.getInt("sleep"));
					x0201HttpU = new SHttpU();
					x0201HttpU.setRequestUrl("dart.fss.or.kr/dsaf001/main.do");
					
					x0201HttpU.setRequestMethod("GET");
					x0201HttpU.addRequestHeader("Content-Type"      , "application/x-www-form-urlencoded");
					x0201HttpU.addRequestHeader("X-Requested-With"  , "XMLHttpRequest");
					
					x0201HttpU.addRequestParameter("rcpNo", dartX0200VO.getString("rcpNo"));
					
					x0201HttpU.httpRequest();
					
					responseText = x0201HttpU.getResponseText();
					
					if(responseText == null) throw new Exception("0101||response is null.");
					if(responseText.indexOf("#download") == -1) throw new Exception("0102||expected data not exist.");
					
					dartX0200VO.put("dcmNo" , responseText.split("#download")[1].split("'")[3]);
					*/
					
					dartX0200VO.put("dcmNo" , "");
					dartX0200VO.put("dcmPth", "");
					dartX0200VO.put("rslCd" , "0000");
					
					x0200SL.add(dartX0200VO);
					++count;
//					if(count % 10 == 0)
//						System.out.println("count: " + count);
//					if(count % 5 == 0) break;
				}
				
				if(pageNum <= currentPage) break;
				currentPage++;
				
			}
			
			x0200VO.put("totalNum"   , totalNum);
			x0200VO.put("pageNum"    , pageNum);
			x0200VO.put("currentPage", currentPage);
			x0200VO.put("x0200SL"    , x0200SL);
			x0200VO.put("rslCd"      , "0000");
			
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().indexOf("\\|\\|") > -1) {
				x0200VO.put("rslCd" , e.getMessage().split("\\|\\|")[0]);
				x0200VO.put("rslMsg", e.getMessage().split("\\|\\|")[1]);
			} else {
				x0200VO.put("rslCd" , "9999");
				x0200VO.put("rslMsg", e.getMessage());
			}
		}
		
		return x0200VO;
	}
	
	@SuppressWarnings("serial")
	private SMap x0300(SMap sMap) {
		
		SMap x0300VO = new DartX0300VO();
		x0300VO.put("rslCd" , "9999");
		x0300VO.put("rslMsg", "");
		
		ArrayList<SMap> x0301List = null;
		SMap x0301VO = null;
		
		SHttpU x0300HttpU   = null;
		SHttpU x0301HttpU   = null;
		String responseText = null;
		try {
			
			if(!sMap.containsKey("rcpNo")) throw new Exception("8001||[rcp_no] can not be null.");
			
			x0300HttpU = new SHttpU();
			x0300HttpU.setRequestUrl("dart.fss.or.kr/dsaf001/main.do");
			
			x0300HttpU.setRequestMethod("GET");
			x0300HttpU.addRequestHeader("Content-Type"    , "application/x-www-form-urlencoded");
			x0300HttpU.addRequestHeader("X-Requested-With", "XMLHttpRequest");
			
			x0300HttpU.addRequestParameter("rcpNo", sMap.getString("rcpNo"));
			
			x0300HttpU.httpRequest();
			
			responseText = x0300HttpU.getResponseText();
			
			if(responseText == null) throw new Exception("0301||response is null.");
			if(responseText.indexOf("#download") == -1) throw new Exception("0302||expected data not exist.");
			
			sMap.put("dcmNo", responseText.split("#download")[1].split("'")[3]);
			
			x0301HttpU = new SHttpU();
			
			x0301HttpU.setRequestUrl("dart.fss.or.kr/pdf/download/excel.do");
			x0301HttpU.setRequestMethod("GET");
			
			x0301HttpU.addRequestParameter("rcp_no", sMap.getString("rcpNo"));
			x0301HttpU.addRequestParameter("dcm_no", sMap.getString("dcmNo"));
			x0301HttpU.addRequestParameter("lang"  , sMap.getString("lang"));
			
			x0301HttpU.httpRequest();
			
			if(200 != x0301HttpU.getResponseCode()) throw new Exception("0303||response code is " + x0301HttpU.getResponseCode());
			
			if(x0301HttpU.getContentLength() <= 0) throw new Exception("0304||excel content length is " + x0301HttpU.getContentLength());
			
			// excel search conditions
			SMap excelVO = new SMap();
			ArrayList<SMap> sheetConditions = new ArrayList<SMap>();
			sheetConditions.add(new SMap().putMap(new HashMap<String, String>() {
				{
					put("sheetName", "기본정보");
				}
			}));
			sheetConditions.add(new SMap().putMap(new HashMap<String, String>() {
				{
					put("sheetName", "재무상태표");
				}
			}));
			sheetConditions.add(new SMap().putMap(new HashMap<String, String>() {
				{
					put("sheetName", "손익계산서");
				}
			}));
			sheetConditions.add(new SMap().putMap(new HashMap<String, String>() {
				{
					put("sheetName", "포괄손익계산서");
				}
			}));
			sheetConditions.add(new SMap().putMap(new HashMap<String, String>() {
				{
					put("sheetName", "현금흐름표");
				}
			}));
			excelVO.put("sheetConditions", sheetConditions);
			
			excelVO = new SExcelU().readXls(x0301HttpU.getResponseByteArray(), excelVO);
			
			List<SMap> rowList = null;
			SMap row = null;
			List<SMap> cellList = null;
			SMap cell = null;
			int accNo = -1;
			int arrayNo = -1;
			for(SMap sheet : excelVO.getListSMap("sheetList")) {
				
				switch(sheet.getString("sheetName")) {
					case "기본정보":// INF
						
//						System.out.println(sheet.getString("sheetName"));
						
						rowList = sheet.getListSMap("rowList");
						
						x0300VO.put("rcpNo", sMap.getString("rcpNo"));
						x0300VO.put("dcmNo", sMap.getString("dcmNo"));
						
						for(int rowNo = 0; rowNo < rowList.size(); rowNo++) {
							
							row = rowList.get(rowNo);
							
							cellList = row.getListSMap("cellList");
							
							for(int cellNo = 0; cellNo < cellList.size(); cellNo++) {
								
								cell = cellList.get(cellNo);
								
								if(cell.getString("cellValue").indexOf("보고서 유형") > -1) {
									x0300VO.put("rptTypNm", cell.getString("cellValue").split(":")[1].trim());
								} else if(cell.getString("cellValue").indexOf("정정공시여부") > -1) {
									x0300VO.put("isCrr", cell.getString("cellValue").split(":")[1].trim());
								} else if(cell.getString("cellValue").indexOf("문서작성일") > -1) {
									x0300VO.put("rptIssDt", cell.getString("cellValue").split(":")[1].replaceAll("[^0-9]", ""));
								} else if(0 == cellNo && "기수".equals(cell.getString("cellValue").trim())) {
									x0300VO.put("accNo", cellList.get(1).getString("cellValue").replaceAll("[^0-9]", ""));
									break;
								} else if(0 == cellNo && "회계기간시작일".equals(cell.getString("cellValue").trim())) {
									x0300VO.put("accDtStr", cellList.get(1).getString("cellValue").replaceAll("[^0-9]", ""));
									break;
								} else if(0 == cellNo && "회계기간종료일".equals(cell.getString("cellValue").trim())) {
									x0300VO.put("accDtEnd", cellList.get(1).getString("cellValue").replaceAll("[^0-9]", ""));
									break;
								} else if(cell.getString("cellValue").indexOf("회사고유번호") > -1) {
									x0300VO.put("cikCd", cell.getString("cellValue").split(":")[1].replaceAll("[^0-9]", ""));
								} else if(cell.getString("cellValue").indexOf("결산월") > -1) {
									x0300VO.put("accDtCls", cell.getString("cellValue").split(":")[1].replaceAll("[^0-9]", ""));
								} else if(cell.getString("cellValue").indexOf("종업원수") > -1) {
									x0300VO.put("numEmp", cell.getString("cellValue").split(":")[1].replaceAll("[^0-9]", ""));
									if(cell.getString("cellValue").indexOf("주주의 수") > -1) {
										x0300VO.put("numShr", cell.getString("cellValue").split(":")[2].replaceAll("[^0-9]", ""));
									}
								} else if(cell.getString("cellValue").indexOf("표준산업분류코드") > -1) {
									x0300VO.put("indClsCd", cell.getString("cellValue").split(":")[1].trim());
								} else if(cell.getString("cellValue").indexOf("통화ISO코드") > -1) {
									x0300VO.put("curIsoCd", cell.getString("cellValue").split(":")[1].trim());
								}
							}
							
						}
						
						break;
					case "재무상태표":// BS
						
						x0301List = new ArrayList<SMap>();
						accNo = -1;
						arrayNo = -1;
						
						rowList = sheet.getListSMap("rowList");
						if(rowList != null) {
							
							for(int rowNo = 0; rowNo < rowList.size(); rowNo++) {
								
								row = sheet.getListSMap("rowList").get(rowNo);
								if(1 == rowNo) {
									accNo = Integer.parseInt(row.getListSMap("cellList").get(0).getString("cellValue").split("기")[0].replaceAll("[^0-9]", ""));
								} else if(0 == row.getListSMap("cellList").get(0).getString("cellValue").trim().length()) {
									arrayNo = rowNo;
								} else if(0 < arrayNo && arrayNo < rowNo) {
									x0301VO = new DartX0301VO();
									x0301VO.put("accNo"    , accNo);
									x0301VO.put("docTypCd" , "BS");
									x0301VO.put("accOrd"   , rowNo - arrayNo);
									x0301VO.put("accNm"    , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("\\s+", ""));
									x0301VO.put("accDpt"   , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("[^\\s]", ""));
									x0301VO.put("accNmCd"  , "");
									if(row.getListSMap("cellList").get(1).getString("cellValue").trim().length() == 0) {
										x0301VO.put("accVal"   , -1);
										x0301VO.put("accValSgn", 0);
									} else {
										x0301VO.put("accVal"   , row.getListSMap("cellList").get(1).getString("cellValue").replaceAll("[^0-9]", ""));
										x0301VO.put("accValSgn", row.getListSMap("cellList").get(1).getString("cellValue").indexOf("(") > -1 ? -1 : 1);
									}
									
									x0301VO.put("rslCd", "0000");
									x0301List.add(x0301VO);
								} else if(row.getListSMap("cellList").get(0).getString("cellValue").indexOf("단위") > -1) {
									x0300VO.put("unt_bs", row.getListSMap("cellList").get(0).getString("cellValue").split(":")[1].replaceAll("\\)", "").trim());
								}
								
							}// end of row loop
							
						}
						
						x0300VO.put("x0301", x0301List);
						
						break;
					case "손익계산서":// IS

						x0301List = new ArrayList<SMap>();
						accNo = -1;
						arrayNo = -1;
						
						rowList = sheet.getListSMap("rowList");
						if(rowList != null) {
							
							for(int rowNo = 0; rowNo < rowList.size(); rowNo++) {
								
								row = sheet.getListSMap("rowList").get(rowNo);
								if(1 == rowNo) {
									accNo = Integer.parseInt(row.getListSMap("cellList").get(0).getString("cellValue").split("기")[0].replaceAll("[^0-9]", ""));
								} else if(0 == row.getListSMap("cellList").get(0).getString("cellValue").trim().length()) {
									arrayNo = rowNo;
								} else if(0 < arrayNo && arrayNo < rowNo) {
									x0301VO = new DartX0301VO();
									x0301VO.put("accNo"    , accNo);
									x0301VO.put("docTypCd" , "BS");
									x0301VO.put("accOrd"   , rowNo - arrayNo);
									x0301VO.put("accNm"    , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("\\s+", ""));
									x0301VO.put("accDpt"   , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("[^\\s]", ""));
									x0301VO.put("accNmCd"  , "");
									if(row.getListSMap("cellList").get(1).getString("cellValue").trim().length() == 0) {
										x0301VO.put("accVal"   , -1);
										x0301VO.put("accValSgn", 0);
									} else {
										x0301VO.put("accVal"   , row.getListSMap("cellList").get(1).getString("cellValue").replaceAll("[^0-9]", ""));
										x0301VO.put("accValSgn", row.getListSMap("cellList").get(1).getString("cellValue").indexOf("(") > -1 ? -1 : 1);
									}
									
									x0301VO.put("rslCd", "0000");
									x0301List.add(x0301VO);
								} else if(row.getListSMap("cellList").get(0).getString("cellValue").indexOf("단위") > -1) {
									x0300VO.put("unt_is", row.getListSMap("cellList").get(0).getString("cellValue").split(":")[1].replaceAll("\\)", "").trim());
								}
								
							}// end of row loop
							
						}
						
						x0300VO.put("x0302", x0301List);
						
						break;
					case "포괄손익계산서":// CI

						x0301List = new ArrayList<SMap>();
						accNo = -1;
						arrayNo = -1;
						
						rowList = sheet.getListSMap("rowList");
						if(rowList != null) {
							
							for(int rowNo = 0; rowNo < rowList.size(); rowNo++) {
								
								row = sheet.getListSMap("rowList").get(rowNo);
								if(1 == rowNo) {
									accNo = Integer.parseInt(row.getListSMap("cellList").get(0).getString("cellValue").split("기")[0].replaceAll("[^0-9]", ""));
								} else if(0 == row.getListSMap("cellList").get(0).getString("cellValue").trim().length()) {
									arrayNo = rowNo;
								} else if(0 < arrayNo && arrayNo < rowNo) {
									x0301VO = new DartX0301VO();
									x0301VO.put("accNo"    , accNo);
									x0301VO.put("docTypCd" , "BS");
									x0301VO.put("accOrd"   , rowNo - arrayNo);
									x0301VO.put("accNm"    , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("\\s+", ""));
									x0301VO.put("accDpt"   , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("[^\\s]", ""));
									x0301VO.put("accNmCd"  , "");
									if(row.getListSMap("cellList").get(1).getString("cellValue").trim().length() == 0) {
										x0301VO.put("accVal"   , -1);
										x0301VO.put("accValSgn", 0);
									} else {
										x0301VO.put("accVal"   , row.getListSMap("cellList").get(1).getString("cellValue").replaceAll("[^0-9]", ""));
										x0301VO.put("accValSgn", row.getListSMap("cellList").get(1).getString("cellValue").indexOf("(") > -1 ? -1 : 1);
									}
									
									x0301VO.put("rslCd", "0000");
									x0301List.add(x0301VO);
								} else if(row.getListSMap("cellList").get(0).getString("cellValue").indexOf("단위") > -1) {
									x0300VO.put("unt_ci", row.getListSMap("cellList").get(0).getString("cellValue").split(":")[1].replaceAll("\\)", "").trim());
								}
								
							}// end of row loop
							
						}
						
						x0300VO.put("x0303", x0301List);
						
						break;
					case "현금흐름표":// CF

						x0301List = new ArrayList<SMap>();
						accNo = -1;
						arrayNo = -1;
						
						rowList = sheet.getListSMap("rowList");
						if(rowList != null) {
							
							for(int rowNo = 0; rowNo < rowList.size(); rowNo++) {
								
								row = sheet.getListSMap("rowList").get(rowNo);
								if(1 == rowNo) {
									accNo = Integer.parseInt(row.getListSMap("cellList").get(0).getString("cellValue").split("기")[0].replaceAll("[^0-9]", ""));
								} else if(0 == row.getListSMap("cellList").get(0).getString("cellValue").trim().length()) {
									arrayNo = rowNo;
								} else if(0 < arrayNo && arrayNo < rowNo) {
									x0301VO = new DartX0301VO();
									x0301VO.put("accNo"    , accNo);
									x0301VO.put("docTypCd" , "BS");
									x0301VO.put("accOrd"   , rowNo - arrayNo);
									x0301VO.put("accNm"    , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("\\s+", ""));
									x0301VO.put("accDpt"   , row.getListSMap("cellList").get(0).getString("cellValue").replaceAll("[^\\s]", ""));
									x0301VO.put("accNmCd"  , "");
									if(row.getListSMap("cellList").get(1).getString("cellValue").trim().length() == 0) {
										x0301VO.put("accVal"   , -1);
										x0301VO.put("accValSgn", 0);
									} else {
										x0301VO.put("accVal"   , row.getListSMap("cellList").get(1).getString("cellValue").replaceAll("[^0-9]", ""));
										x0301VO.put("accValSgn", row.getListSMap("cellList").get(1).getString("cellValue").indexOf("(") > -1 ? -1 : 1);
									}
									
									x0301VO.put("rslCd", "0000");
									
									x0301List.add(x0301VO);
									
								} else if(row.getListSMap("cellList").get(0).getString("cellValue").indexOf("단위") > -1) {
									x0300VO.put("unt_cf", row.getListSMap("cellList").get(0).getString("cellValue").split(":")[1].replaceAll("\\)", "").trim());
								}
								
							}// end of row loop
							
						}
						
						x0300VO.put("x0304", x0301List);
						
						break;
					default:
						break;
				}
			}
			
			x0300VO.put("rslCd", "0000");
			
		} catch (Exception e) {
			if(e.getMessage().indexOf("||") > -1) {
				x0300VO.put("rslCd" , e.getMessage().split("\\|\\|")[0]);
				x0300VO.put("rslMsg", e.getMessage().split("\\|\\|")[1]);
			} else {
				x0300VO.put("rslCd" , "9999");
				x0300VO.put("rslMsg", e.getMessage());
			}
		}
		
		return x0300VO;
	}
	
	private SMap x0301(SMap sMap) {
		
		SMap x0300VO = new DartX0300VO();
		
		return x0300VO;
	}
	
	private SMap x0302(SMap sMap) {
		SMap x0302VO = new DartX0300VO();
		return x0302VO;
	}
	
	private SMap x0303(SMap sMap) {
		SMap x0302VO = new DartX0300VO();
		return x0302VO;
	}
	
	private SMap x0304(SMap sMap) {
		SMap x0302VO = new DartX0300VO();
		return x0302VO;
	}
	
	private SMap x0305(SMap sMap) {
		SMap x0302VO = new DartX0300VO();
		return x0302VO;
	}
}
