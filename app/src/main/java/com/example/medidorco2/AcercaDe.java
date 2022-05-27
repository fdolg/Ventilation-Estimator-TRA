package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AcercaDe extends AppCompatActivity {

    /*Método void onCreate:
    * se ejecuta cuando se crea la Activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
    }

    /*Método void onClickMain:
     * se ejecuta cuando se quiere regresar al MainActivity*/
    public void onClickMain(View view) // Función asociada a Main
    {
        Intent intentMain =new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intentMain);
    }
}