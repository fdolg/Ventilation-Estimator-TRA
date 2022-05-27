package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BorrarActivity extends AppCompatActivity {

    //Los siguientes son los elementos presentes en BorrarActivity
    private Button borrar;
    private Button volver;
    private TextView tvRegistro;
    //El ID del registro que se podría borrar
    private String IDseleccion;

    /*Método void onCreate:
    * Se ejecuta al crearse la Activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar);
        //Relaciona los elementos del xml con los atributos del Activity
        borrar=findViewById(R.id.btnBorrar);
        volver=findViewById(R.id.btnVolver);
        tvRegistro=findViewById(R.id.txtvRegistroABorrar);
        //Toma las variables del Activity anterior
        Intent variablesIntentAnterior=getIntent();
        IDseleccion =variablesIntentAnterior.getStringExtra("indice");
        tvRegistro.setText(variablesIntentAnterior.getStringExtra("registro"));
        //Agrega el OnClickListener a los botones de volver y borrar
        borrar.setOnClickListener(onClickborrar);
        volver.setOnClickListener(onClickVolver);

    }


    /*onClickborrar: Acciones a realizar cuando se presione el botón borrar*/
    View.OnClickListener onClickborrar=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Se crea un nuevo intent y se le agregan variables
            Intent intentregreso=new Intent(getApplicationContext(),Consultas.class);
            intentregreso.putExtra("clase","borrar");
            intentregreso.putExtra("borrar",1);
            intentregreso.putExtra("indice", IDseleccion);
            //Inicia la otra actividad
            startActivity(intentregreso);

        }
    };

    /*onClickVolver: Acciones a realizar cuando se presione el botón volver*/
    View.OnClickListener onClickVolver=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Se crea un nuevo intent y se le agregan variables
            Intent intentregreso=new Intent(getApplicationContext(),Consultas.class);
            intentregreso.putExtra("clase","menu");
            intentregreso.putExtra("borrar",0);
            intentregreso.putExtra("indice", IDseleccion);
            //Inicia la otra actividad
            startActivity(intentregreso);

        }
    };

}