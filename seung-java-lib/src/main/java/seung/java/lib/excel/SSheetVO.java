package seung.java.lib.excel;

import seung.java.lib.arguments.SMap;

public class SSheetVO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3273485369707588717L;
	
	public SSheetVO() {
		
		this.put("sheetName"           , "");
		this.put("physicalNumberOfRows", "");
		this.put("rowList"             , null);
	}
}
