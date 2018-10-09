package seung.java.lib.arguments;

import java.nio.charset.Charset;

public enum SCharsetE {

	_UTF8("UTF-8")
	, _EUCKR("EUC-KR")
	;
	
	private String charsetName;
	
	private SCharsetE() {
	}
	
	private SCharsetE(String charsetName) {
		this.charsetName = charsetName;
	}
	
	public String getCharsetName() {
		return charsetName;
	}
	
	public Charset getCharset() {
		return Charset.forName(charsetName);
	}
}
