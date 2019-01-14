package com.victorgonzcuriel.classrecorder.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebSocketMsg {

	private String action;

	@JsonProperty("file")
    private FileData file;

    public WebSocketMsg(){
        this.action = null;
        this.file = new FileData();
    }

    public WebSocketMsg(String action) {
        this.action = action;
    }
    
    public WebSocketMsg(String action, String fileName) {
    	this.action = action;
    	this.file = new FileData();
    	this.file.setFileName(fileName);
    }

    public WebSocketMsg(String action, FileData file) {
        super();
        this.action = action;
        this.file = file;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public FileData getFile() {
        return file;
    }

    public void setFile(FileData file) {
        this.file = file;
    }
	
}
