package com.example.medidorco2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManejoDB {


    //Para establecer la conexión con la base de datos
    private FirebaseFirestore db;
    //Para guardar los datos de los registros de la base de datos en el listView
    private ArrayList<String> registros;
    //para guardar los ID's de los registros en la base de datos, son los
    //registros que cumplen con pertenecer al correo ingresado solamente
    private ArrayList<String> registroID;
    //Una copia del listview que se usa en consulta, para poder
    //llenarlo desde aquí dentro
    private ListView lstRegistros;
    //Esta variable es para tener el contexto de la clase consulta
    //para poder mostrar lo que hay en la base de datos y para poder borrar
    private Context context;

    //Constructor
    //Método ManejoDB: constructor donde se establece la conexión con la base de datos
    public ManejoDB(){
        db=FirebaseFirestore.getInstance();

    }

    /*étodo void setVariables:
     * Este método sólo se manda a llamar cuando
     *se va a revisar lo que hay dentro de la base de datos, sirve para
     * llenar la variable context y la variable lstRegistros*/
    public void setVariables(Context context,ListView lstRegistros){
        this.context=context;
        this.lstRegistros=lstRegistros;
    }

    /*Método void ConsultarPorEmail:
    * éste método es el que permite que se pueda ver el contenido de la base de datos en el listView */
    public void ConsultarPorEmail(){
        //se realiza la búsqueda en la base de datos con el siguiente query
        Query query = db.collection("lecturasCO2").whereEqualTo("email", usuario.email); // Hace búsqueda con email de la clase registro
        //si el query no es null realiza lo siguiente
        if (query!=null){

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {// Se emplean los registros de la búsqueda

                            if(task.isSuccessful()){
                                //inicializa las variables de registros y registro ID
                                registros=new ArrayList<>();
                                registroID=new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //para cada documento que esté contenido en el resultado, se añade la información requerida
                                    registros.add("Fecha: "+(String) document.get("fecha")+"\n"+"Concentración(PPM): "+
                                            (String) document.get("concentracion")+"\n"+
                                            "Tasa(PPM/s): "+(String) document.get("tasa")+"\n"); // Se concatenan los campos en el formato requerido

                                    registroID.add(document.getId());//se adquiere el ID de cada documento del query
                                    Log.w(TAG, "Llegaste a la busqueda");
                                }
                                //Adapta el contenido de la base de datos y luego llena el listView
                                ArrayAdapter<String > adapter =new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,registros);
                                lstRegistros.setAdapter(adapter); // Se emplea el adaptador para visualizar registros
                            }else{
                                Log.w(TAG, "Error getting documents. Tienes un error", task.getException());

                            }

                        }
                    });


        }


    }
    /*Método void borrar:
    * Sirve para borrar los documentos de la base de datos que tengan el ID IDborrar*/
    public void borrar(String IDborrar){
        db.collection("lecturasCO2").
                document(IDborrar).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot se borró exitosamente");
                Toast.makeText(context,"Se borró exitosamente", Toast.LENGTH_LONG);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "DocumentSnapshot NO se borró");
                Toast.makeText(context,"No se pudo borrar exitosamente", Toast.LENGTH_LONG);
            }
        });

    }

    /*Método void registrarEnDB:
    * sirve para registrar un nuevo dato en la base de datos, accede a la fecha actual del dispositivo
    * para ingresar la fecha*/
    public void registrarEnDB(String concentracion, String tasa){
        CollectionReference lecturas=db.collection("lecturasCO2"); // Se señala la colección a usar
        Map<String,Object> data =new HashMap<>();

        String fecha = new Date().toString();
        //Si los datos a registrar son correctos, entonces se suben los datos a la base
        if(fecha.equals("")==false && concentracion.equals("")==false && tasa.equals("")==false){ // Verificación de datos
            data.put("email", usuario.email);
            data.put("fecha", fecha);
            data.put("concentracion", concentracion);
            data.put("tasa", tasa);
            lecturas.document().set(data); // Se registran datos
        }

    }


    //Getter para obtener los registros
    public ArrayList<String> getRegistros() {
        return registros;
    }

    //Getter para obtener los ID de los registros
    public ArrayList<String> getRegistroID() {
        return registroID;
    }
    //Getter para obtener el objeto db
    public FirebaseFirestore getDb() {
        return db;
    }


}
