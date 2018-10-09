package seung.java.lib.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

public class SHttpU {
	
	private String              requestUrl;
	private String              requestMethod;
	private ArrayList<String[]> requestHeaders;
	private String              requestEncoding;
	private ArrayList<String[]> requestParameters;
	private String              queryString;
	private String              responseEncoding;
	private int                 connectionTimeout;
	private int                 readTimeout;
	
	private int                 responseCode;
	private String              contentType;
	private int                 contentLength;
	private String              contentDisposition;
	private String              responseText;
	private byte[]              responseByteArray;
	
	private String              exceptionMessage;
	
	public SHttpU() {
		init();
	}
	
	public void init() {
		this.requestUrl         = "http://";
		this.requestMethod      = "GET";
		this.requestHeaders     = new ArrayList<String[]>();
		this.requestEncoding    = "UTF-8";
		this.requestParameters  = new ArrayList<String[]>();
		this.queryString        = "";
		this.responseEncoding   = "UTF-8";
		this.connectionTimeout  = 1000 * 3;
		this.readTimeout        = 1000 * 3;
		this.responseCode       = -1;
		this.contentType        = "";
		this.contentLength      = -1;
		this.contentDisposition = "";
		this.responseText       = "";
		this.responseByteArray  = null;
		this.exceptionMessage   = "";
	}
	
	public void httpRequest() {
		
		HttpURLConnection   httpURLConnection = null;
		OutputStream outputStream = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			
			setQueryString();
			
			httpURLConnection = (HttpURLConnection) new URL(requestUrl + ("GET".equals(requestMethod) ? "?" + queryString : "")).openConnection();
			
			for(String[] header : requestHeaders) {
				httpURLConnection.setRequestProperty(header[0], header[1]);
			}
			if("POST".equals(requestMethod)) httpURLConnection.setRequestProperty("Content-Length", "" + queryString.getBytes(requestEncoding).length);
			
			httpURLConnection.setRequestMethod(requestMethod);
			httpURLConnection.setConnectTimeout(connectionTimeout);
			httpURLConnection.setReadTimeout(readTimeout);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			
			outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
			
			outputStream.write(("GET".equals(requestMethod) ? "" : queryString).getBytes(requestEncoding));
			outputStream.flush();
			
			setResponseCode(httpURLConnection.getResponseCode());
			setContentType(httpURLConnection.getContentType());
			setContentLength(httpURLConnection.getInputStream().available());
			setContentDisposition(httpURLConnection.getHeaderField("Content-Disposition"));
			
			if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				if(getContentType() != null && getContentType().indexOf("excel") > -1) {
					
					setResponseByteArray(IOUtils.toByteArray(httpURLConnection.getInputStream()));
					
				} else {
					
					setResponseText(IOUtils.toString(httpURLConnection.getInputStream(), responseEncoding));
//					bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), responseEncoding));
//					
//					String line;
//					while((line = bufferedReader.readLine()) != null) {
//						stringBuilder.append(line);
//					}
				}
			}
			
		} catch (IOException e) {
			setExceptionMessage(e.getMessage());
			stringBuilder.append(e.getMessage());
		} finally {
			try {
				if(bufferedReader != null) bufferedReader.close();
				if(outputStream != null) outputStream.close();
				if(httpURLConnection != null) httpURLConnection.disconnect();
			} catch (IOException e) {
				stringBuilder.append(e.getMessage());
			}
		}
		
//		setResponseText(stringBuilder.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[        requestUrl] " + requestUrl);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[     requestMethod] " + requestMethod);
		stringBuilder.append(System.getProperty("line.separator"));
		for(String[] header : requestHeaders) {
			stringBuilder.append("[     requestHeader] " + header[0] + ": " + header[1]);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		stringBuilder.append("[   requestEncoding] " + requestEncoding);
		stringBuilder.append(System.getProperty("line.separator"));
		for(String[] parameter : requestParameters) {
			stringBuilder.append("[  requestParameter] " + parameter[0] + ": " + parameter[1]);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		stringBuilder.append("[       queryString] " + queryString);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[  responseEncoding] " + responseEncoding);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[      responseCode] " + responseCode);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[       contentType] " + contentType);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[     contentLength] " + contentLength);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[contentDisposition] " + contentDisposition);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[      responseText] " + responseText);
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("[  exceptionMessage] " + exceptionMessage);
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public void setRequestUrl(String requestUrl) {
		this.requestUrl += requestUrl;
	}
	
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	
	public void addRequestHeader(String key, String val) {
		requestHeaders.add(new String[] {key, val});
	}
	
	public String getRequestEncoding() {
		return requestEncoding == null ? "UTF-8" : requestEncoding;
	}
	
	public void setRequestEncoding(String requestEncoding) {
		this.requestEncoding = requestEncoding;
	}
	
	public void addRequestParameter(String key, String val) {
		requestParameters.add(new String[] {key, val});
	}
	
	public void setQueryString() throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder();
		for(String[] parameter : requestParameters) {
			stringBuilder.append(String.format("&%s=%s", URLEncoder.encode(parameter[0], getRequestEncoding()), URLEncoder.encode(parameter[1], getRequestEncoding())));
		}
		this.queryString = stringBuilder.length() > 0 ? stringBuilder.toString().substring(1) : "";
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public void setResponseEncoding(String responseEncoding) {
		this.responseEncoding = responseEncoding;
	}
	
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getContentDisposition() {
		return contentDisposition == null ? "" : contentDisposition;
	}
	
	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}
	
	public int getContentLength() {
		return contentLength;
	}
	
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
	public byte[] getResponseByteArray() {
		return responseByteArray;
	}
	
	public void setResponseByteArray(byte[] responseByteArray) {
		this.responseByteArray = responseByteArray;
	}
	
	public String getResponseText() {
		return responseText;
	}
	
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
}
