package seung.java.lib;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import seung.java.lib.file.SFileU;

public class SFileUT {

	private static final Logger logger = LoggerFactory.getLogger(SFileUT.class);
	
	@Test
	public void test() throws IOException {
		
		String fileName = "2018.1.2.3.4.5.log";
		logger.info(fileName.substring(fileName.indexOf(".") + 1).replace(".log", ""));
		
//		SCommonU sCommonU = new SCommonU();
//		SFileU sFileU = new SFileU();
//		
//		try {
//			logger.info(sCommonU.getUUID());
//			logger.info(sFileU.getDirPath("c:/infotech/exgate/exgate.exe"));
//			logger.info(sCommonU.toJsonString(sFileU.getFileListInfo(new File("c:/server/ftp"), null, "f", 0), true));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
