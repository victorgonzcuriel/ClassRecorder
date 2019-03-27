package com.gonzalez.victor.classrecorderapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ControlActivity extends AppCompatActivity {

    private StompedClient client;
    private boolean isPaused = false;
    private Record record;
    private String domain;
    Timer pingTimer;

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
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
        String url = "http://"+ this.domain + "/ClassRecorder/web/RecieveFile";
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "classRecorder" + File.separator + fileName);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.MINUTES)
                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",fileName, RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.body().close();

        //mando mensaje al servidor de que pase actulice la pagina
        WebSocketMsg msg = new WebSocketMsg("SENDED");
        Gson gson = new Gson();
        String jsonMsg = gson.toJson(msg);

        client.send("/crws/msg", jsonMsg);
        httpClient.dispatcher();
    }

    private void Connect(){
        client = new StompedClient.StompedClientBuilder().build(("ws://" + this.domain + "/ClassRecorder/ws/websocket"));
        client.subscribe("/subscription/sub", new StompedListener() {
            @Override
            public void onNotify(StompedFrame stompedFrame) {
                final String stompedBody = stompedFrame.getStompedBody().replaceAll("\\p{C}", "");


                WebSocketMsg msg = new Gson().fromJson(stompedBody, WebSocketMsg.class);
                if (msg.getAction().equals("NEW")) {
                    record = new Record(msg.getActionInfo().replace(' ', '_') + "_audio");
                }
                //si es para bajarse un fichero
                else if (msg.getAction().equals("DOWNLOAD")) {
                    SendFile(msg.getActionInfo().replaceAll(".mp4", "_audio.mp4"));
                }
                //borrado del fichero
                else if (msg.getAction().equals("DELETE"))
                    DeleteFile(msg.getActionInfo().replaceAll(".mp4", "_audio.mp4"));
                else if (msg.getAction().equals("CLASSENDED"))
                    ReloadActivity();
            }
        });
    }

    @Override
    protected void onStop(){
        try {
            client.disconnect();
        }catch (Exception e){

        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //layoutObjects
        final ImageButton rightArrow = (ImageButton) findViewById(R.id.btnRight);
        final ImageButton leftArrow = (ImageButton) findViewById(R.id.btnLeft);
        final ImageButton btnStart = (ImageButton) findViewById(R.id.btnStart);
        final ImageButton btnStop = (ImageButton) findViewById(R.id.btnStop);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        //lectura del id de ngrok y conexion
        String ngrokId = getIntent().getStringExtra((MainActivity.NGROK_CODE));

        if(ngrokId.length() <10)
            domain = ngrokId + ".ngrok.io";
        else
            domain = ngrokId;

        try {

            Connect();
        }catch(Exception ex)
        {
            //si hay problemas al conectar con el servidor vuelvo atras y lo notifico
            Intent intent = new Intent();
            intent.putExtra("output", "Compruebe el identificador de ngrok");
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        //inicializo el timer para mandar mensajes
        pingTimer = new Timer();
        TimerTask pingTask = new TimerTask(){
          @Override
            public void run(){
                  WebSocketMsg msg = new WebSocketMsg("OK");
                  Gson gson = new Gson();
                  String jsonMsg = gson.toJson(msg);
                  client.send("/crws/msg", jsonMsg);

          }
        };

        pingTimer.schedule(pingTask, 0, 1*60*1000);


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
        Intent intent = new Intent("Reload");
        setResult(5, intent);
        finish();
        startActivity(getIntent());
    }
}
