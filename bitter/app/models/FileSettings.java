package models;

public class FileSettings {
	
	public String[] supportedTypes;
	public String path;
	public int maxSize;
	
	public FileSettings(String[] types, String path, int maxSize){
		if(types.length < 0 || path.isEmpty() == true || maxSize <= 0){
			throw new IllegalArgumentException("You must support at least one file type, the path can not be empty and max size has to be"
					+ "bigger than zero");
		}
		this.supportedTypes = types;
		this.path = path;
		this.maxSize = maxSize;
	}

}
