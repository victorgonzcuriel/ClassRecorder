package com.gonzalez.victor.classrecorderapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    //constante para el mensaje a la siguiente vista
    public static String NGROK_CODE = "com.gonzalez.victor.classrecoderapp.NGROK_CODE";
    private static final int REQUEST_CODE = 101;

    ProgressBar redCircle = null;
    ProgressBar greenCircle = null;
    Button connectButton = null;

    //función para controlar los progress bar
    private void SetOkStatus(boolean isOk)
    {
        if(isOk){
            redCircle.setVisibility(View.INVISIBLE);
            greenCircle.setVisibility(View.INVISIBLE);
        }else{
            redCircle.setVisibility(View.VISIBLE);
            greenCircle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckPermissions();

        //layout objects
        connectButton = findViewById(R.id.connectButton);
        final EditText txtBoxNgokId = findViewById(R.id.txtBoxNgokId);

        redCircle = findViewById(R.id.RedCircle);
        greenCircle = findViewById(R.id.GreenCircle);


        //listeners
        connectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                redCircle.setVisibility(View.INVISIBLE);
                if(Connect(txtBoxNgokId.getText().toString())){
                    SetOkStatus(true);
                }else{
                    SetOkStatus(false);
                }
            }
        });
    }

    private boolean Connect(String ngrokId){

        Intent intent = new Intent(this, ControlActivity.class);
        EditText ngrokEditText = (EditText) findViewById(R.id.txtBoxNgokId);
        intent.putExtra(NGROK_CODE, ngrokEditText.getText().toString());
        startActivityForResult(intent, RESULT_FIRST_USER);
        return true;
    }

    private void CheckPermissions(){
        //miro mis permisos y si no los tengo los pido
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
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

        CheckPermissions();
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

    //control del activity main
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Si hay problemas al conectar con el servidor lo notifico
        if(requestCode == RESULT_FIRST_USER) {
            if (resultCode == RESULT_CANCELED) {
                SetOkStatus(false);
                new AlertDialog.Builder(this)
                        .setMessage(data.getStringExtra("output"))
                        .show();
            }
            //si devuelve un 5, hay qu recargar otra vez
            else if(resultCode == 5){
                connectButton.performClick();
            }
        }
    }

}

