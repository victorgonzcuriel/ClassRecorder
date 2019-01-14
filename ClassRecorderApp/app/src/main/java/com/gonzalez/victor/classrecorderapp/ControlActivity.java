package com.gonzalez.victor.classrecorderapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.gonzalez.victor.classes.Record;
import com.gonzalez.victor.classes.WebSocketMsg;
import com.google.gson.Gson;
import com.stomped.stomped.client.StompedClient;
import com.stomped.stomped.component.StompedFrame;
import com.stomped.stomped.listener.StompedListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ControlActivity extends AppCompatActivity {

    private StompedClient client;
    private boolean isPaused = false;
    private static final int REQUEST_CODE = 101;
    private Record record;
    private String fileName;
    private String domain;

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0)
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                        closeNow();
                    }
            }
        }
    }

    private byte[] ReadByteFiles(String filePath) throws IOException {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] output = new byte[size];

        BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(filePath));
        buffer.read(output, 0, output.length);
        buffer.close();

        return output;
    }

    private void SendFile(String fileName){
        String ngrokId = getIntent().getStringExtra((MainActivity.NGROK_CODE));
        String url = "http://"+ this.domain + "/ClassRecorder/web/RecieveFile";
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "classRecorder" + File.separator + fileName);

        OkHttpClient httpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",fileName, RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //mando mensaje al servidor de que pase actulice la pagina
        WebSocketMsg msg = new WebSocketMsg("SENDED");
        Gson gson = new Gson();
        String jsonMsg = gson.toJson(msg);
        client.send("/crws/msg", jsonMsg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //layoutObjects
        final ImageButton rightArrow = (ImageButton) findViewById(R.id.btnRight);
        final ImageButton leftArrow = (ImageButton) findViewById(R.id.btnLeft);
        final ImageButton btnStart = (ImageButton) findViewById(R.id.btnStart);
        final ImageButton btnStop = (ImageButton) findViewById(R.id.btnStop);

        //miro mis permisos y si no los tengo los pido
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(internetPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);
        }
        if(recordAudioPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        }
        if(writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        //lectura del id de ngrok y conexion

        String ngrokId = getIntent().getStringExtra((MainActivity.NGROK_CODE));

        if(ngrokId.length() <10)
            domain = ngrokId + ".ngrok.io";
        else
            domain = ngrokId;

        client = new StompedClient.StompedClientBuilder().build(("ws://" + this.domain + "/ClassRecorder/ws/websocket"));
        client.subscribe("/subscription/sub", new StompedListener() {
            @Override
            public void onNotify(StompedFrame stompedFrame) {
                final String stompedBody = stompedFrame.getStompedBody().replaceAll("\\p{C}", "");;

                WebSocketMsg msg = new Gson().fromJson(stompedBody, WebSocketMsg.class);
                if(msg.getAction().equals("NEW")) {
                    record = new Record(msg.getFile().getFileName().replace(' ', '_') + "_audio");
                    //fileName = msg.getFile().getFileName();
                }
                //si es para bajarse un fichero
                else if(msg.getAction().equals("DOWNLOAD")){
                    SendFile(msg.getFile().getFileName().replaceAll(".mp4", "_audio.mp4"));
                }
                //borrado del fichero
                else if(msg.getAction().equals("DELETE"))
                    DeleteFile(msg.getFile().getFileName().replaceAll(".mp4", "_audio.mp4"));
                else if(msg.getAction().equals("CLASSENDED"))
                    ReloadActivity();
            }
        });

        //listeners
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mando mensaje al servidor de que pase pagina
                WebSocketMsg msg = new WebSocketMsg("RIGHT");
                Gson gson = new Gson();
                String jsonMsg = gson.toJson(msg);
                client.send("/crws/msg", jsonMsg);
            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mando mensaje al servidor de que pase pagina
                WebSocketMsg msg = new WebSocketMsg("LEFT");
                Gson gson = new Gson();
                String jsonMsg = gson.toJson(msg);
                client.send("/crws/msg", jsonMsg);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //voy mandando mensaje al servidor, este es el que decide que hacer
                WebSocketMsg msg = new WebSocketMsg("PLAY");
                String jsonMsg = new Gson().toJson(msg);
                client.send("/crws/msg", jsonMsg);

                //cambio el booleno de control
                isPaused = !isPaused;
                //cambio el icono en función de si esta pausado o no
                if(isPaused)
                    btnStart.setImageResource(R.mipmap.pause);
                else
                    btnStart.setImageResource(R.mipmap.play);

                //miro si no esta grabando para ponerlo a grabar
                if(!record.isRecording())
                    record.StartRecording();

                btnStop.setVisibility(View.VISIBLE);

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //voy mandando mensaje al servidor, este es el que decide que hacer
                WebSocketMsg msg = new WebSocketMsg("STOP");
                String jsonMsg = new Gson().toJson(msg);
                client.send("/crws/msg", jsonMsg);;
                record.StopRecording();
            }
        });
    }

    private void DeleteFile(String fileName){
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "classRecorder" + File.separator + fileName);
        if(file.exists())
            file.delete();

        WebSocketMsg msg = new WebSocketMsg("DELETED");
        String jsonMsg = new Gson().toJson(msg);
        client.send("/crws/msg", jsonMsg);
    }

    private void ReloadActivity(){
        finish();
        startActivity(getIntent());
    }
}
