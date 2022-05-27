package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class configParametros extends AppCompatActivity {
    private EditText etConcentracion;
    private EditText etTasa;
    private String direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_parametros);
        etConcentracion=findViewById(R.id.etConcentracion);
        etTasa=findViewById(R.id.etTasa);
        Bundle bundleextras= getIntent().getExtras(); // Objeto que permite recibir datos de activity previo
        direccion = bundleextras.getString(getResources().getString(R.string.str_direccion_dispositivo)); // Se obtiene MAC de activity previo

    }
    public void onClickComenzar(View view)
    {
        String aux1 = String.valueOf(etConcentracion.getText()); // Se obtienen los valores establecidos por usuario
        String aux2 = String.valueOf(etTasa.getText());
        Intent intentMedicion =new Intent(getApplicationContext(),Medicion.class);

        intentMedicion.putExtra(getResources().getString(R.string.str_concentracion_max),aux1);// Se envía dato al siguiente activity
        intentMedicion.putExtra(getResources().getString(R.string.str_tasa_max),aux2); // Se envía dato al siguiente activity
        intentMedicion.putExtra(getResources().getString(R.string.str_direccion_dispositivo),direccion); // Se envía dato MAC siguiente activity
        startActivity(intentMedicion);
    }


}