package seung.java.lib.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.InflaterOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import seung.java.lib.arguments.SMap;

public class SFileU {

	private static final Logger logger = LoggerFactory.getLogger(SFileU.class);
	
	public byte[] fileInflate(byte[] data) {
		
		byte[] byteArray = null;
		
		ByteArrayInputStream byteArrayInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		InflaterOutputStream inflaterOutputStream = null;
		
		try {
			
			byteArrayInputStream = new ByteArrayInputStream(data);
			byteArrayOutputStream = new ByteArrayOutputStream();
			inflaterOutputStream = new InflaterOutputStream(byteArrayOutputStream);
			
			IOUtils.copy(byteArrayInputStream, inflaterOutputStream);
			
			byteArray = byteArrayOutputStream.toByteArray();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			closeCloseable(inflaterOutputStream);
			closeCloseable(byteArrayOutputStream);
			closeCloseable(byteArrayInputStream);
		}
		
		return byteArray;
	}
	
	public String readFile(File file, String charsetName) throws UnsupportedEncodingException {
		return new String(readFile(file), charsetName);
	}
	
	public byte[] readFile(File file) {
		
		byte[] byteArray = null;
		
		InputStream inputStream = null;
		try {
			
			if(file.exists()) {
				inputStream = new FileInputStream(file);
				byteArray = IOUtils.toByteArray(inputStream);
			} else {
				throw new IOException("file [" + file + "] is not exists.");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			closeCloseable(inputStream);
		}
		
		return byteArray;
	}
	
	public void writeFile(File file, String data, String charsetName) throws UnsupportedEncodingException {
		writeFile(file, data.getBytes(charsetName));
	}
	
	public void writeFile(File file, byte[] data) {
		
		OutputStream outputStream = null;
		
		try {
			outputStream = openFileOutputStream(file);
			if(data != null) outputStream.write(data);
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			closeCloseable(outputStream);
		}
	}
	
	public FileOutputStream openFileOutputStream(File file) throws IOException {
		
		if(file.exists()) {
			if(file.isDirectory()) throw new IOException("file [" + file + "] is directory.");
			if(!file.canWrite()) throw new IOException("file [" + file + "] can not write.");
		} else {
			File parentFile = file.getParentFile();
			if(parentFile != null && !parentFile.exists()) {
				if(!parentFile.mkdirs()) throw new IOException("file [" + file + "] is not created.");
			}
		}
		
		return new FileOutputStream(file);
	}
	
	public void closeCloseable(Closeable closeable) {
		try {
			if(closeable != null) closeable.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<SMap> getFileListInfo(
			File rootFile
			, String match
			, String type
			, int depth
			) throws IOException {
		
		if(!rootFile.exists())  throw new IOException("file is not exists.");
		if(!rootFile.canRead()) throw new IOException("file is not readable.");
		
		if(type == null || type.length() == 0) type = "fd";
		
		ArrayList fileList = new ArrayList<SMap>();
		
		if(rootFile.isFile()) {
			if(type.contains("f") && (match == null || match.length() == 0 || rootFile.getPath().toLowerCase().contains(match.toLowerCase()))) {
				fileList.add(getFileInfo(rootFile));
			}
		} else if(rootFile.isDirectory()) {
			if(type.contains("d") && (match == null || match.length() == 0 || rootFile.getPath().toLowerCase().contains(match.toLowerCase()))) fileList.add(getFileInfo(rootFile));
			for(File file : rootFile.listFiles()) {
				if(depth > -1) fileList.addAll(getFileListInfo(file, match, type, depth - 1));
			}
		}
		
		return fileList;
	}
	
	public SMap getFileInfo(File file) throws IOException {
		
		SMap fileMap = new SMap();
		
		if(file.isDirectory()) fileMap.put("type", "directory");
		else if(file.isFile()) fileMap.put("type", "file");
		else                   fileMap.put("type", "");
		
		fileMap.put("absolutePath" , file.getAbsolutePath().replace("\\", "/"));
		fileMap.put("canonicalPath", file.getCanonicalPath().replace("\\", "/"));
//		fileMap.put("totalSpace"   , file.getTotalSpace());
//		fileMap.put("usableSpace"  , file.getUsableSpace());
//		fileMap.put("freeSpace"    , file.getFreeSpace());
		fileMap.put("path"         , file.getPath().replace("\\", "/"));
		fileMap.put("name"         , file.getName());
		if(file.isFile() && file.getName().lastIndexOf(".") > -1) fileMap.put("extension", file.getName().substring(file.getName().lastIndexOf(".") + 1));
		else                                                      fileMap.put("extension", "");
		fileMap.put("extension"    , FilenameUtils.getExtension(file.getCanonicalPath()));
		fileMap.put("parentPath"   , file.getParent().replace("\\", "/"));
		fileMap.put("parentName"   , file.getParentFile().getName());
		fileMap.put("lastModified" , new SimpleDateFormat("yyyyMMddHHmmss").format(file.lastModified()));
		fileMap.put("length"       , file.length());
		fileMap.put("creationTime" , new SimpleDateFormat("yyyyMMddHHmmss").format(Files.getFileAttributeView(Paths.get(file.getAbsolutePath()), BasicFileAttributeView.class).readAttributes().creationTime().toMillis()));
		
		return fileMap;
	}
	
	/**
	 * @desc extract directory path
	 * @param path
	 * @return
	 */
	public String getDirPath(String path) {
		return getDirPath(path, "");
	}
	public String getDirPath(String path, String suf) {
		return path.substring(0, path.lastIndexOf("/")) + suf;
	}
}
