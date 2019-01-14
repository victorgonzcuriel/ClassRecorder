package com.gonzalez.victor.classes;

/**
 * Created by victor on 3/09/18.
 */

public class FileData {

    private String fileContent;
    private String fileName;

    public FileData(){
        this.fileName = null;
        this.fileContent = null;
    }

    public FileData(String fileContent, String fileName) {
        super();
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
