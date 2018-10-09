package seung.java.lib.arguments;

public enum SHttpMethodE {

	_GET("GET")
	, _POST("POST")
	;
	
	private String httpMethod;
	
	private SHttpMethodE(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	public String getMethod() {
		return httpMethod;
	}
	
}
