package com.gonzalez.victor.classes;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by victor on 23/09/18.
 */

public class Record {

    private String fileName;
    private boolean isRecording;
    protected final String fileRoute = Environment.getExternalStorageDirectory() + File.separator + "classRecorder";
    private MediaRecorder mediaRecorder;

    public String getFilename(){
        return this.fileName;
    }

    public boolean isRecording(){
        return this.isRecording;
    }

    public Record(String fileName){
        this.fileName = this.fileRoute + File.separator + fileName + ".mp4";
        this.isRecording = false;

        File dir = new File(fileRoute);
        dir.mkdirs();

        //configuracion del MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(this.fileName);
    }

    public void startRecording(){
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            this.isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopRecording(){
        mediaRecorder.stop();
        this.isRecording = false;
    }

}
