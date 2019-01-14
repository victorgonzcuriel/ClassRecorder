package com.victorgonzcuriel.classrecorder.web;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.victorgonzcuriel.classrecorder.classes.ManagerUnit;
import com.victorgonzcuriel.classrecorder.classes.OldRecord;
import com.victorgonzcuriel.classrecorder.classes.Record;

@Controller
@ComponentScan("com.victorgonzcuriel.classrecorder.classes")
public class WebController {
	
	@Autowired
	Record actualRecord;
	//cronometro
	StopWatch stopWatch;
	
	private static final String fileDirectory = System.getProperty("user.home") + "/ClassRecorder/clases_grabadas/";
	private static final String audioDirectory = System.getProperty("user.home") + "/ClassRecorder/audios_descargados/";
	private static final String mixtedDirectory = System.getProperty("user.home") + "/ClassRecorder/clases_mezcladas/";
	private static final String finalDirectory = System.getProperty("user.home") + "/ClassRecorder/clases_finales/";

	private void CreateDirectories() {
		File filePath = new File(fileDirectory);
		if(!filePath.exists()) {
			filePath.mkdirs();
		}
		
		File audioPath = new File(audioDirectory);
		if(!audioPath.exists())
			audioPath.mkdirs();
		
		File mixtedPath = new File(mixtedDirectory);
		if(!mixtedPath.exists())
			mixtedPath.mkdirs();
		
		File finalPath = new File(finalDirectory);
		if(!finalPath.exists())
			finalPath.mkdirs();
	}
	
	@RequestMapping("/RecieveFile")
	
	public ResponseEntity<?> RecieveFile(@RequestParam("file") MultipartFile fileData) {
		
		try {
			byte[] binaryData = fileData.getBytes();
			
			File newFile = new File(audioDirectory + fileData.getOriginalFilename());
			newFile.createNewFile();
			FileOutputStream stream = new FileOutputStream(newFile);
			stream.write(binaryData);
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@RequestMapping("/")
	public ModelAndView index() {
		CreateDirectories();
		return new ModelAndView("index");
	}
	
	@RequestMapping("newclass")
	public ModelAndView newclass (String fileName) {
		this.actualRecord = new Record(fileName.replace(' ', '_'));
		stopWatch = new StopWatch();
		ModelAndView model = new ModelAndView("newClass", "fileName", fileName);
		return model;
	}
	
	@RequestMapping("Play")
	public ResponseEntity<Object> Play(){
		//si no esta grabado lo pongo a grabar
		//meto el timestamp del tiempo 0 para mantener la coherencia de las listas
		if(!actualRecord.IsRecording()) {
			actualRecord.StartRecord();
			actualRecord.AddResumeTimeStamp("0");
			stopWatch.start();
		}
		else {
			//en función de si esta o no en pausa, paso el tiempo a un
			if(actualRecord.isPaused())
				actualRecord.AddResumeTimeStamp(Long.toString(stopWatch.getTime(TimeUnit.SECONDS)));
			else
				actualRecord.AddPauseTimeStamp(Long.toString(stopWatch.getTime(TimeUnit.SECONDS)));
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping("/StopRecord")
	public ResponseEntity<Object> Record() {
		//paro la grabación y el tiempo
		if(actualRecord.IsRecording()) {
			stopWatch.stop();
			//si no esta pausado, pongo el tiempo del final del video en la cola de pausa
			if(!actualRecord.isPaused())
				actualRecord.AddPauseTimeStamp(Long.toString(stopWatch.getTime(TimeUnit.SECONDS)));
			actualRecord.StopRecord();


		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping("management")
	public ModelAndView Management() {
		List<ManagerUnit> items = GetFileList();
		ModelAndView model = new ModelAndView("management");
		
		JSONArray jsonArray = new JSONArray();
		for(ManagerUnit unit : items)
			jsonArray.add(unit);
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("list", jsonArray);
		model.addObject("items", jsonObject.toJSONString());
		return model;
	}
	
	@RequestMapping("cut")
	public ModelAndView CutVideo(@RequestParam("fileName") String fileName) {
		
		String originalPath = mixtedDirectory + fileName.replaceAll(".mp4", "_mezclado.mp4");
		String finalPath = finalDirectory + fileName;
		String jsonPath = fileDirectory + fileName.replaceAll(".mp4", ".json");
		
		OldRecord recordData = null;
		
		File newFinalFile = new File(finalPath);
		if(newFinalFile.exists())
			newFinalFile.delete();
		
		//lectura del json
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(jsonPath));
			recordData = new OldRecord(obj);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String command = "ffmpeg -i " + originalPath + " -filter_complex \"";
		int loopSize = recordData.getPauseTimeStamps().size();
		for (int i=0; i< recordData.getPauseTimeStamps().size(); i++) {
			command += "[0:v]trim=" + recordData.getResumeTimeStamps().get(i) + ":" + recordData.getPauseTimeStamps().get(i) + ",setpts=N/FRAME_RATE/TB[v" + i + "]; ";
			command += "[0:a]atrim=0" + recordData.getResumeTimeStamps().get(i) + ":" + recordData.getPauseTimeStamps().get(i) + ",asetpts=N/SR/TB[a"+ i + "]; ";
		}
		
		for(int i=0; i<recordData.getPauseTimeStamps().size(); i++) {
			command += "[v" + i + "][a" + i + "]";
		}
		
		command += "concat=" + recordData.getPauseTimeStamps().size() + ":v=1:a=1[v][a] \"";
		command += " -map \"[v]\" -map \"[a]\" " + finalPath;
		
		Process proc;
		try {
			//proc = Runtime.getRuntime().exec(command);
			proc = new ProcessBuilder("/bin/sh", "-c", command).start();
			proc.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Management();
	}
	
	@RequestMapping("mix")
	public ModelAndView MixVideos(@RequestParam("fileName") String fileName)
	{
		
		String videoPath = fileDirectory + fileName.replaceAll(".mp4", "_video.mp4");
		String audioPath = audioDirectory + fileName.replaceAll(".mp4", "_audio.mp4");
		String mixtedPath = mixtedDirectory + fileName.replaceAll(".mp4", "_mezclado.mp4");
		
		File newMixtedFile = new File(mixtedPath);
		if(newMixtedFile.exists())
			newMixtedFile.delete();
		
		try {
			String command = "ffmpeg -i "+ videoPath +"  -i "+audioPath+" -codec copy -shortest " + mixtedPath;
			Process proc = Runtime.getRuntime().exec(command);
			
			proc.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Management();
	}
	
	@RequestMapping("DoAll")
	public ModelAndView DoAll(@RequestParam("fileName") String fileName) {
		
		MixVideos(fileName);
		CutVideo(fileName);
		
		return Management();
	}
	
	@RequestMapping("DeleteFile")
	public ModelAndView DeleteFile(@RequestParam("fileName") String fileName) {
		
		String videoPath = fileDirectory + fileName.replaceAll(".mp4", "_video.mp4");
		String audioPath = audioDirectory + fileName.replaceAll(".mp4", "_audio.mp4");
		String mixtedPath = mixtedDirectory + fileName.replaceAll(".mp4", "_mezclado.mp4");
		String jsonPath = fileDirectory + fileName.replaceAll(".mp4", ".json");
		String finalPath = finalDirectory + fileName;
		
		File file = new File(videoPath);
		if(file.exists())
			file.delete();
		
		file = new File(audioPath);
		if(file.exists())
			file.delete();
		
		file = new File(mixtedPath);
		if(file.exists())
			file.delete();
		
		file = new File(jsonPath);
		if(file.exists())
			file.delete();
		
		file = new File(finalPath);
		if(file.exists())
			file.delete();
		
		return Management();
	}

	private List<ManagerUnit> GetFileList() {
		List<ManagerUnit> output = new ArrayList<ManagerUnit>();
		
		File filePath = new File(fileDirectory);
		File[] files = filePath.listFiles(new FilenameFilter() {
			public boolean accept(File path, String name) {
				return name.toLowerCase().endsWith(".mp4");
			}
		});
		
		File audioPath = new File(audioDirectory);
		File[] audios = audioPath.listFiles(new FilenameFilter() {
			public boolean accept(File path, String name) {
				return name.toLowerCase().endsWith(".mp4");
			}
		});
		
		File mixtedPath = new File(mixtedDirectory);
		File[] mixted = mixtedPath.listFiles(new FilenameFilter() {
			public boolean accept(File path, String name) {
				return name.toLowerCase().endsWith(".mp4");
			}
		});
		
		File finalPath = new File(finalDirectory);
		File[] finals = finalPath.listFiles(new FilenameFilter() {
			public boolean accept(File path, String name) {
				return name.toLowerCase().endsWith(".mp4");
			}
		});
		
		//Recorro primero los finales y luego voy hacia atras, 
		//si ya estaba en la lista es que es de los pasos sigueinte
		for(File item : finals) {
			output.add(new ManagerUnit(item.getName(), "FINALIZADO"));
		}
		
		for(File item : mixted) {
			Boolean check = false;
			for(ManagerUnit unit : output) {
				if(unit.getFileName().equals(item.getName().replaceAll("_mezclado", "")))
					check = true;
			}
			if(!check)
				output.add(new ManagerUnit(item.getName().replaceAll("_mezclado", ""), "MEZCLADO"));
		}
		
		for(File item : audios) {
			Boolean check = false;
			for(ManagerUnit unit : output) {
				if(unit.getFileName().equals(item.getName().replaceAll("_audio", "")))
					check = true;
			}
			if(!check)
				output.add(new ManagerUnit(item.getName().replaceAll("_audio", ""), "RECOGIDO"));
		}
		
		for(File item : files) {
			Boolean check = false;
			for(ManagerUnit unit : output) {
				if(unit.getFileName().equals(item.getName().replaceAll("_video", "")))
					check = true;
			}
			if(!check)
				output.add(new ManagerUnit(item.getName().replaceAll("_video", ""), "GRABADO"));
		}
		
		return output;
	}

}
