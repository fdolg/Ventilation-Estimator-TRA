package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMenu=findViewById(R.id.btnMenu);
    }

    public void onClickLogin(View view) //Función asociada al button Login
    {
        Intent intentLogin = new Intent(MainActivity.this,Login.class);
        startActivity(intentLogin);
    }
    public void onClickAcercaDe(View view) //Función asociada al button Acerca De
    {
        Intent intentAcercaDe = new Intent(MainActivity.this,AcercaDe.class);
        startActivity(intentAcercaDe);
    }
}