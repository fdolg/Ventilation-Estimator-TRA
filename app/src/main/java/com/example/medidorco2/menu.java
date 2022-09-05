package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class menu extends AppCompatActivity {

    private Button btnLectura;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnLectura=findViewById(R.id.btnLectura);

    }
    public void onClickLectura(View view) // Función asociada a activity Lectura
    {
        mode="lectura";
        Intent intentVincular =new Intent(getApplicationContext(),Vincular.class);
        intentVincular.putExtra(getResources().getString(R.string.str_mode),mode);
        startActivity(intentVincular);
    }

    public void onClickRegistrar(View view) // Función asociada a activity Registrar
    {
        Intent intentRegistrar =new Intent(getApplicationContext(), Registro.class);
        startActivity(intentRegistrar);
    }
    public void onClickCambio(View view) // Función asociada a cambio de usuario (Activity Login)
    {
        Intent intentLogin =new Intent(getApplicationContext(),Login.class);
        startActivity(intentLogin);
    }
    public void onClickConsultas(View view) // Función asociada a activity Consultas de registros
    {
        Intent intentConsultas =new Intent(getApplicationContext(),Consultas.class);
        intentConsultas.putExtra("clase","menu");
        startActivity(intentConsultas);
    }


    public void onClickEstimador(View view) {
        mode="estimador";
        Intent intentVincular =new Intent(getApplicationContext(),Vincular.class);
        intentVincular.putExtra(getResources().getString(R.string.str_mode),mode);
        startActivity(intentVincular);

    }
}