package com.gonzalez.victor.classes;

/**
 * Created by victor on 3/09/18.
 */

public class WebSocketMsg {


    private String action;
    private String actionInfo;

    public WebSocketMsg(){
        this.action = null;
    }

    public WebSocketMsg(String action) {
        super();
        this.action = action;
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
