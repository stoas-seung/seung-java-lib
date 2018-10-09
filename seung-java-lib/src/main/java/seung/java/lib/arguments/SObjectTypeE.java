package seung.java.lib.arguments;

public enum SObjectTypeE {

	_STRING("STRING")
	, _FILE("FILE")
	, _BASE64("BASE64")
	, _HEX("HEX")
	;
	
	private String objectType;
	
	private SObjectTypeE() {
	}
	
	private SObjectTypeE(String objectType) {
		this.objectType = objectType;
	}
	
	public String getObjectType() {
		return objectType;
	}
}
