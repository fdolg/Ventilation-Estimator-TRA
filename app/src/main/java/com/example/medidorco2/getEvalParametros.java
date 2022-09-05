package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class getEvalParametros extends AppCompatActivity {
    private TextView tvPPMext;
    private EditText etAncho, etAlto,etLargo,etOcup;
    private Spinner spinner;
    private String[] ACH={"Mínimo(3 ACH)","Bueno(4 ACH)","Excelente(5 ACH)","Ideal(6 ACH)"};
    Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_eval_parametros);
        tvPPMext=findViewById(R.id.tvPPMext);
        tvPPMext.setText("Concentración exterior (PPM):"+usuario.ppmext);
        spinner=findViewById(R.id.spinner);
        etAncho=findViewById(R.id.etAncho);
        etAlto=findViewById(R.id.etAlto);
        etLargo=findViewById(R.id.etLargo);
        etOcup=findViewById(R.id.etOcup);
        btnStart=findViewById(R.id.btnStart);
        // Configuración spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,ACH);
        spinner.setAdapter(adapter);

    }



    public void onClickStart(View view) {
        String ancho,alto,largo,ocup,ACHselec;
        ancho = etAncho.getText().toString();
        alto = etAlto.getText().toString();
        largo = etLargo.getText().toString();
        ocup =etOcup.getText().toString();
        ACHselec = spinner.getSelectedItem().toString();

        Intent intentEval =new Intent(getApplicationContext(),Evaluacion.class);

        intentEval.putExtra(getResources().getString(R.string.str_ancho),ancho);
        intentEval.putExtra(getResources().getString(R.string.str_alto),alto);
        intentEval.putExtra(getResources().getString(R.string.str_largo),largo);
        intentEval.putExtra(getResources().getString(R.string.str_ocup),ocup);
        intentEval.putExtra(getResources().getString(R.string.str_ACHselec),ACHselec);

        startActivity(intentEval);

    }
}