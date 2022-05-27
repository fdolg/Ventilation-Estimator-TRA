package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Registro extends AppCompatActivity {
    private ManejoDB manejoDB;
    EditText etConcentracion,etTasa;
    Button btnRegistrar, btnRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);
        manejoDB=new ManejoDB(); // Se instancia la clase creada para manejo de Firebase

        etConcentracion=findViewById(R.id.etConcentracion);
        etTasa=findViewById(R.id.etTasa);
        btnRegistrar=findViewById(R.id.btnRegistrar);
        btnRegresar=findViewById(R.id.btnRegistrar);

    }
    public void registrarADB(){


        String concentracion=etConcentracion.getText().toString();
        String tasa=etTasa.getText().toString();

        manejoDB.registrarEnDB(concentracion,tasa); // Se emplea el método de la clase para hacer registros en Firebase
    }

    public void onClickRegistrar(View view)
    {
        registrarADB();
        etConcentracion.setText(""); // Limpia campos de datos
        etTasa.setText("");
    }
    public void onClickRegresar(View view)
    {
        Intent intentMenu =new Intent(getApplicationContext(),menu.class);
        startActivity(intentMenu);
    }

}