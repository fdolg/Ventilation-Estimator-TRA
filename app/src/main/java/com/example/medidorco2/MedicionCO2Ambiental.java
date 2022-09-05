package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MedicionCO2Ambiental extends AppCompatActivity {
    private Button btnComenzar;
    private TextView tvContando;
    private ProgressBar pbTiempo;
    private int progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicion_co2_ambiental);
        btnComenzar=findViewById(R.id.btnComenzar);
        tvContando=findViewById(R.id.tvMidiendo);
        pbTiempo=findViewById(R.id.pbTiempo);
        progress=0;
        pbTiempo.setMax(60);
    }

    public void onClickComenzar(View view) {
        countDownTimer.start();
        btnComenzar.setVisibility(View.INVISIBLE);
        tvContando.setText("Midiendo");
    }

    CountDownTimer countDownTimer=new CountDownTimer(1000*60,1*1000) {
        @Override
        public void onTick(long l){ // Contador para el registro periódico de datos del sensor
            progress++;
            pbTiempo.setProgress(progress);

        }

        @Override
        public void onFinish() {
            Intent intentEvalParam = new Intent(getApplicationContext(),getEvalParametros.class);
           // intentEvalParam.putExtra(getResources().getString(R.string.str_direccion_dispositivo),cau2[1]); // Se envío la dirección MAC al siguiente activity
            startActivity(intentEvalParam);

        }
    };
}