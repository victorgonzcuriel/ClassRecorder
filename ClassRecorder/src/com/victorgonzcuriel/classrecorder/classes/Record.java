package com.victorgonzcuriel.classrecorder.classes;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Record {
	
	private String fileName = "";
	//Esta o no grabando
	private boolean isRecording = false;
	//esta o no en estado de pausa
	private boolean isPaused = false;
	//proceso de grabacion de la ffmpeg
	private Process proc;
	//lista con los timestamps de play
	private List<String> resumeTimeStamps;
	//lista con los timestamps de pause
	private List<String> pauseTimeStamps;
	private String screenResolution; 
	
	private static final String fileDirectory = System.getProperty("user.home") + "/ClassRecorder/clases_grabadas/";


	public Record(String filename) {
		
		this.fileName = Record.fileDirectory + filename;
		
		this.resumeTimeStamps = new ArrayList<String>();
		this.pauseTimeStamps = new ArrayList<String>();
		
		//obtengo la resolución de la pantalla
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.screenResolution = String.valueOf(device.getDisplayMode().getWidth()) + "x" + String.valueOf(device.getDisplayMode().getHeight());
		
		
	}
	
	public Record() {
		this.resumeTimeStamps = new ArrayList<String>();
		this.pauseTimeStamps = new ArrayList<String>();
	}

	public boolean isPaused() {
		return isPaused;
	}
	
	public void addResumeTimeStamp(String input) {
		this.resumeTimeStamps.add(input);
		this.isPaused = false;
	}
	
	public void addPauseTimeStamp(String input) {
		this.pauseTimeStamps.add(input);
		this.isPaused = true;
	}
	
	

	public List<String> getResumeTimeStamps() {
		return resumeTimeStamps;
	}

	public List<String> getPauseTimeStamps() {
		return pauseTimeStamps;
	}

	public String getFilename() {
		return this.fileName;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public boolean startRecord() {
		boolean result = true;
		try {
			if (!isRecording) {
				
				proc = Runtime.getRuntime().exec(
						"ffmpeg -video_size " + this.screenResolution + " -framerate 25 -f x11grab -i :0.0 -v quiet "+this.fileName+"_video.mp4"
				);
				isRecording = true;
			}
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

	public boolean stopRecord() throws IOException, InterruptedException {
		boolean result = true;
			if(isRecording) {
					OutputStream stream = proc.getOutputStream();
					stream.write("q".getBytes());
					stream.flush();
					isRecording = false;
			}

		
		saveClass();
		return result;
	}
	
	//guarda el json en un fichero
	private void saveClass() {
		new OldRecord(this).saveOnFile();
	}
}
