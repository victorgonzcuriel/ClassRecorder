package com.victorgonzcuriel.classrecorder.classes;

public class ManagerUnit {
	
	public String fileName;
	public String status;
	
	public ManagerUnit(String fileName, String status) {
		super();
		this.fileName = fileName;
		this.status = status;
	}
	
	public ManagerUnit() {
		
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "{\"fileName\":\"" + fileName + "\", \"status\":\"" + status + "\"}";
	}
	
	
	
}
