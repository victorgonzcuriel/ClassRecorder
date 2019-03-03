package com.victorgonzcuriel.classrecorder.classes;

public class WebSocketMsg {

	private String action;
	private String actionInfo;

    public WebSocketMsg(){
        this.action = null;
    }

    public WebSocketMsg(String action) {
        this.action = action;
    }
    
    public WebSocketMsg(String action, String actionInfo) {
    	this.action = action;
    	this.actionInfo = actionInfo;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public String getActionInfo() {
    	return this.actionInfo;
    }
    
    public void setActionInfo(String actionInfo) {
    	this.actionInfo = actionInfo;
    }
	
}
