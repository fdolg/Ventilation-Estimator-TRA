package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
    }

    public void onClickIngresar(View view) {
        //Validación de correo con forma correcta
        if (ValidaCorreo()) {
            //Lo que sigue si esta bien el correo

            usuario.email = etEmail.getText().toString().trim(); // Se asigna el correo al atributo Static de la clase registro
            Intent intentIngresar = new Intent(getApplicationContext(), menu.class);
            startActivity(intentIngresar);
        }
    }

    private boolean ValidaCorreo() {
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        // Email ingresado para validar
        String email = etEmail.getText().toString().trim();

        Matcher mather = pattern.matcher(email); //Validación del correo, true (correcto) o false (incorrecto)

        if (mather.find()) {
            Toast.makeText(getApplicationContext(), "El email ingresado es válido", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "El email ingresado es inválido", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
