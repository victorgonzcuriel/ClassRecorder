package com.victorgonzcuriel.classrecorder.classes;

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
	List<String> resumeTimeStamps;
	//lista con los timestamps de pause
	List<String> pauseTimeStamps;
	
	private static final String fileDirectory = System.getProperty("user.home") + "/ClassRecorder/clases_grabadas/";


	public Record(String filename) {
		
		this.fileName = Record.fileDirectory + filename;
		
		this.resumeTimeStamps = new ArrayList<String>();
		this.pauseTimeStamps = new ArrayList<String>();
	}
	
	public Record() {
		this.resumeTimeStamps = new ArrayList<String>();
		this.pauseTimeStamps = new ArrayList<String>();
	}

	public boolean isPaused() {
		return isPaused;
	}
	
	public void AddResumeTimeStamp(String input) {
		this.resumeTimeStamps.add(input);
		this.isPaused = false;
	}
	
	public void AddPauseTimeStamp(String input) {
		this.pauseTimeStamps.add(input);
		this.isPaused = true;
	}
	
	

	public List<String> getResumeTimeStamps() {
		return resumeTimeStamps;
	}

	public List<String> getPauseTimeStamps() {
		return pauseTimeStamps;
	}

	public String GetFilename() {
		return this.fileName;
	}

	public boolean IsRecording() {
		return isRecording;
	}

	public boolean StartRecord() {
		boolean result = true;
		try {
			if (!isRecording) {
				//proc = Runtime.getRuntime().exec(
				//		"ffmpeg -f x11grab -r 25 -s 1280x720 -i :0.0+0,24  -vcodec libx264 -preset ultrafast -threads 0 "+this.fileName+"_video.mp4");
					proc = Runtime.getRuntime().exec(
							"ffmpeg -video_size 1366x768 -framerate 25 -f x11grab -i :0.0 -v quiet "+this.fileName+"_video.mp4"
							);
					isRecording = true;
			}
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

	public boolean StopRecord() throws IOException, InterruptedException {
		boolean result = true;
			if(isRecording) {
					OutputStream stream = proc.getOutputStream();
					stream.write("q".getBytes());
					stream.flush();
					//proc.waitFor();
					isRecording = false;
			}

		
		SaveClass();
		return result;
	}
	
	//guarda el json en un fichero
	private void SaveClass() {
		new OldRecord(this).SaveOnFile();
	}
}
