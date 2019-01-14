package com.victorgonzcuriel.classrecorder.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileData {
	
	@JsonProperty("fileContent")
    private String fileContent;
	
    private String fileName;

    public FileData(){
        this.fileName = null;
        this.fileContent = null;
    }

    public FileData(String fileContext, String fileName) {
        super();
        this.fileContent = fileContext;
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }
    public void getFileContent(String fileContext) {
        this.fileContent = fileContext;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
