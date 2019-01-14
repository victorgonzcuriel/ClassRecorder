package com.gonzalez.victor.classes;

/**
 * Created by victor on 3/09/18.
 */

public class WebSocketMsg {


    private String action;

    private FileData file;

    public WebSocketMsg(){
        this.action = null;
        this.file = new FileData();
    }

    public WebSocketMsg(String action) {
        super();
        this.action = action;
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
