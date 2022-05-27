package com.example.medidorco2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Consultas extends AppCompatActivity {


    //Esta variable servirá para llenar el listView de la Activity
    private ListView lstRegistros;
    //Esta variable sirve para no tener que gestionar directamente las acciones
    //de la base de datos
    private ManejoDB manejoDB;


    /*Método void onCreate:
    * se ejecuta cuando se crea la Activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);
        Intent intentoVariables=getIntent();
        lstRegistros=findViewById(R.id.lstRegistros);
        //se instancia db, inicia la conexión con la base de datos
        manejoDB=new ManejoDB();
        //aquí pasa el contexto y el lstRegistros para llenarlos en manejoDB
        //estos sólo es necesarios proporcionarlos cuando hay una búsqueda en la base
        manejoDB.setVariables(getApplicationContext(),lstRegistros);
        //Aquí hace la consulta y llena el lstRegistros
        manejoDB.ConsultarPorEmail();
        lstRegistros.setOnItemClickListener(ItemClickTabla);


        //Toma las variables de la clase anterior
        String cadClase=intentoVariables.getStringExtra("clase");
        int validacion= intentoVariables.getIntExtra("borrar",0);
        String IDdecision=intentoVariables.getStringExtra("indice");
        //Si la clase anterior es el BorrarActivity y el código de validacion es 1, manda a
        //llamar al método borrar e ingresa el ID del registro a borrar
        if(cadClase.equals("borrar") && validacion==1){
            borrar(IDdecision);
        }

    }


    /*Método void onRestart:
    * Sirve para actualizar los datos que se visualizan tan pronto como se vuelve a entrar
    * a la Activity de Consultas*/
    @Override
    protected void onRestart() {
        super.onRestart();
        manejoDB.ConsultarPorEmail();
    }


    /*ItemClickTabla:
    * Cuando se de click sobre uno de los elementos del listView, entonces se desplegará
    * el BorrarActivity para que el usuario decida si borrar o no el dato seleccionado*/
    AdapterView.OnItemClickListener ItemClickTabla=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intentBorrar=new Intent(getApplicationContext(),BorrarActivity.class);
            String cadena= (String)(adapterView.getItemAtPosition(i));
            intentBorrar.putExtra("registro", cadena);
            intentBorrar.putExtra("indice",manejoDB.getRegistroID().get(i));
            startActivity(intentBorrar);

        }
    };

    /*Método void borrar:
    * Borra de la base el registro con el ID seleccionado*/
    private void borrar(String IDseleccionado){
            Log.e("oye, aqui","Aqui llegó antes de fallar 1 revisar:"+IDseleccionado);
            manejoDB.borrar(IDseleccionado);
            //Cuando termina de borrar, se vuelve a consultar la base para actualizar
            manejoDB.ConsultarPorEmail();
    }

    /*Método boolean onKeyDown:
    * Sirve para regresar al menú una vez que se presiona el botón para regresar del celular*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentMenu = new Intent(getApplicationContext(), menu.class);
            startActivity(intentMenu);
        }
        return super.onKeyDown(keyCode, event);
    }
}