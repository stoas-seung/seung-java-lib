package seung.java.lib.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import seung.java.lib.SCommonU;

public class SSocketU {

	private final String _PRE = "<iftRoot><iftInput>";
	private final String _SUF = "</iftInput></iftRoot>";
	
	private String host;
	private String port;
	private int    timeout    = 1000 * 30;
	private String parameter;
	private String encType;
	
	public SSocketU() {
	}
	
	public String request() {
		
		StringBuffer output = new StringBuffer();
		
		Socket socket = null;
		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		
		try {
			
			if(host == null) throw new UnknownHostException("host(ip) is null.");
			if(port == null) throw new UnknownHostException("port is null.");
			if(parameter == null || parameter.length() == 0) throw new IOException("parameter is null.");
			
			socket = new Socket(host, Integer.parseInt(port));
			socket.setSoTimeout(timeout);
			
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			printWriter.println(parameter);
			printWriter.flush();
			
			String line = "";
			while((line = bufferedReader.readLine()) != null) {
				output.append(line);
				if(line.indexOf("</iftRoot>") > -1) break;
			}
			
			printWriter.close();
			bufferedReader.close();
			socket.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output.toString().replaceAll("<iftRoot>|<iftOutput>|</iftOutput>|</iftRoot>", "");
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public void setEncType(String encType) {
		this.encType = encType;
	}
	public void setParameter(Object parameter) {
		
		String inJson = "";
		
		if(parameter instanceof String) inJson = (String) parameter;
		else                            inJson = new SCommonU().toJsonString(parameter);
		
		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append(_PRE);
		if(this.encType != null) stringBuffer.append("<" + encType + ">");
		stringBuffer.append(inJson);
		if(this.encType != null) stringBuffer.append("</" + encType + ">");
		stringBuffer.append(_SUF);
		
		this.parameter = stringBuffer.toString();
		
	}
	public String getParameter() {
		return parameter;
	}
	
}
