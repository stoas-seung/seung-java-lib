package seung.java.lib.excel;

import seung.java.lib.arguments.SMap;

public class SCellVO extends SMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1781940788686975382L;
	
	public SCellVO() {
		
		this.put("rowIndex"   , -1);
		this.put("columnIndex", -1);
		this.put("cellValue"  , "");
	}
}
