package com.gonzalez.victor.classrecorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    //constante para el mensaje a la siguiente vista
    public static String NGROK_CODE = "com.gonzalez.victor.classrecoderapp.NGROK_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //layout objects
        final Button connectButton = findViewById(R.id.connectButton);
        final EditText txtBoxNgokId = findViewById(R.id.txtBoxNgokId);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final ProgressBar redCircle = findViewById(R.id.RedCircle);
        final ProgressBar greenCircle = findViewById(R.id.GreenCircle);

        //listeners
        connectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                redCircle.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(Connect(txtBoxNgokId.getText().toString())){
                    greenCircle.setVisibility(View.VISIBLE);
                }else{
                    redCircle.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean Connect(String ngrokId){
/*        final StompedClient client = new StompedClient.StompedClientBuilder().build(("ws://c3c12622.ngrok.io/classRecorder/websocket"));
            client.subscribe("/crws/sub", new StompedListener() {


                @Override
                public void onNotify(StompedFrame stompedFrame) {

                }


            });
        CalcInput message = new CalcInput();
        message.setNum1(1);
        message.setNum2(2);

        Gson gson = new Gson();
        String jsonMsg = gson.toJson(message);
            client.send("/crwsmsg/msg", jsonMsg);*/

        Intent intent = new Intent(this, ControlActivity.class);
        EditText ngrokEditText = (EditText) findViewById(R.id.txtBoxNgokId);
        intent.putExtra(NGROK_CODE, ngrokEditText.getText().toString());
        startActivity(intent);
        return true;

    }

}

