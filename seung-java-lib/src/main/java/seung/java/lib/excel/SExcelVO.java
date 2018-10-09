package seung.java.lib.excel;

import seung.java.lib.arguments.SMap;

public class SExcelVO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6349370837851645020L;

	public SExcelVO() {
		
		this.put("numberOfSheets"  , -1);
		this.put("sheetList"       , null);
		this.put("exceptionMessage", "");
		
	}
}
