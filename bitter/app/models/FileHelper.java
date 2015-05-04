package models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.persistence.*;

import org.apache.commons.io.FilenameUtils;

import play.Logger;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.mvc.Http.MultipartFormData.FilePart;

@Entity
public class FileHelper extends Model {
	
	@Id
	public long id;
	public String fileName;
	public String defaultFilePath;
	public static String basePath = "./public/";
	
	
	public FileHelper(String defaultFilePath){
		defaultFilePath = defaultFilePath;
	}
	
	public FileHelper(){
		
	}
	
	public static Finder<Long, FileHelper> find = new Finder<Long, FileHelper>(Long.class,
			FileHelper.class);


	public String upload(FilePart filePart, FileSettings settings) {
		
		deleteFile();
		
		if (checkContentType(filePart.getContentType(), settings.supportedTypes) == false) {
			return "File type not valid";
		}
		File file = filePart.getFile();
		if (checkSize(file.length(), settings.maxSize) == false) {
			return "File to large";
		}
		
		//making sure the filename is unique
		String miliseconds = Long.toString(new Date().getTime());
		this.fileName = settings.path + miliseconds + "."
				+ FilenameUtils.getExtension(filePart.getFilename());

		// to make sure that the directory exists
		new File(basePath + settings.path).mkdirs();
		
		try {
			Files.move(file.toPath(), new File(basePath + fileName).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logger.error(e.getMessage());
			Logger.error(e.getLocalizedMessage());
			return "Something went wrong, could not save file";
		}
		this.save();
		return null;
	}
	
	public String getFilePath(){
		return fileName != null ? fileName : defaultFilePath;
	}
	
	public void deleteFile(){
		if(fileName != null){
			Logger.debug("Deleting: " + basePath+fileName);
			boolean deleted = new File(basePath+fileName).delete();
			Logger.debug("Deletion is: "+ Boolean.toString(deleted));
		}
	}
	
	public void delete(){
		deleteFile();
		super.delete();
	}

	private boolean checkContentType(String contentType, String[] supported) {

		for (String type : supported) {
			if (type.equals(contentType))
				return true;
		}
		return false;
	}
	
	

	private boolean checkSize(long size, int maxSize) {
		size /= (1024 * 1024);
		return size <= maxSize ? true : false;
	}
}
