package seung.java.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import seung.java.lib.arguments.SCV;

public class SCommonUT {

	@Test
	public void test() throws UnsupportedEncodingException {
		System.out.println("[" + new SCommonU().repeate(12) + "]");
		System.out.println(URLEncoder.encode(new SCommonU().repeate(12), SCV._S_UTF8).replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~"));
	}
}
