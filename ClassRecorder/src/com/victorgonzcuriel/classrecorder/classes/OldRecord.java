package com.victorgonzcuriel.classrecorder.classes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OldRecord {
		
	private String fileName;
	//lista con los timestamps de play
	private List<String> resumeTimeStamps;
	//lista con los timestamps de pause
	private List<String> pauseTimeStamps;
	
	public OldRecord(Record input) {
		this.fileName = input.getFilename();
		this.resumeTimeStamps = input.getResumeTimeStamps();
		this.pauseTimeStamps = input.getPauseTimeStamps();
	}
	
	public OldRecord(JSONObject object) {
		this.fileName = (String) object.get("fileName");
		this.resumeTimeStamps = new ArrayList<String>();
		this.pauseTimeStamps = new ArrayList<String>();
		JSONArray resumeStamps = (JSONArray) object.get("resumeTimeStamps");
		for(Object item : resumeStamps) {
			this.resumeTimeStamps.add((String) item);
		}
		JSONArray pauseStamps = (JSONArray) object.get("pauseTimeStamps");
		for(Object item : pauseStamps) {
			this.pauseTimeStamps.add((String) item);
		}
	}
	
	
	
	public String getFileName() {
		return fileName;
	}

	public List<String> getResumeTimeStamps() {
		return resumeTimeStamps;
	}

	public List<String> getPauseTimeStamps() {
		return pauseTimeStamps;
	}

	@SuppressWarnings("unchecked")
	public void saveOnFile() {
		JSONObject jsonObject = new JSONObject();
		JSONArray resumeArray = new JSONArray();
		JSONArray pauseArray = new JSONArray();
		
		//Asigno datos al json
		for(String item : this.resumeTimeStamps)
			resumeArray.add(item);
		
		for(String item : this.pauseTimeStamps)
			pauseArray.add(item);
		
		jsonObject.put("resumeTimeStamps", resumeArray);
		jsonObject.put("pauseTimeStamps", pauseArray);
		jsonObject.put("fileName", this.fileName);
		
		//escribo el fichero
		try {
			FileWriter fileWriter = new FileWriter(this.fileName + ".json");
			fileWriter.write(jsonObject.toJSONString());
			fileWriter.flush();
			fileWriter.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
