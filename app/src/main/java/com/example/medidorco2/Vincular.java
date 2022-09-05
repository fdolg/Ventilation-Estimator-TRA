package com.example.medidorco2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Vincular extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT =10 ;
    private ListView lstDispositivos;
    private BluetoothAdapter BtAdaptador; //Adaptador para manejo de bluetooth
    private TextView txtvTitulo;
    private ArrayList<String> dispositivos;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vincular);
        lstDispositivos = findViewById(R.id.lstdVinculados);
        txtvTitulo= findViewById(R.id.txtvTitulo);
        lstDispositivos.setOnItemClickListener(ItemClickLista);

        Bundle bundleextras= getIntent().getExtras(); // Clase que permite recibir datos de activity previo
        mode = bundleextras.getString(getResources().getString(R.string.str_mode)); // Recepción de dato
    }

    @Override
    protected void onResume() {
        super.onResume();
        VerificarEstadoBt();


    }

    private void VerificarEstadoBt() {
        BtAdaptador= BluetoothAdapter.getDefaultAdapter();

        if(BtAdaptador== null) // El equipo no cuenta con BT
        {
            Toast.makeText(getApplicationContext(),"Su equipo no cuenta con bluetooth",Toast.LENGTH_SHORT).show();
        }else {
            if(BtAdaptador.isEnabled()){ // Se verifica la disponibilidad BT
                Log.d("DispBluetoooth","....Bluetooth Activiado");
                MuestraDispositivos();

            }else
            {
                //Pedir al usuario que active Bluetooth
                Intent haBTint = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(haBTint,REQUEST_ENABLE_BT);
            }
        }
    }

    private void MuestraDispositivos() {
        Set<BluetoothDevice> DispEmp= BtAdaptador.getBondedDevices(); // Se obtienen los dispositivos BT vinculados
        if(DispEmp.size() > 0)
        {
            dispositivos= new ArrayList<>();
            for(BluetoothDevice dispositivo:DispEmp){

                dispositivos.add(dispositivo.getName()+"\n"+dispositivo.getAddress());
            }
            ArrayAdapter<String > adapter =new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dispositivos);
            lstDispositivos.setAdapter(adapter); // Se visualizan mediante adaptador

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK){
            MuestraDispositivos();

        }
    }

    AdapterView.OnItemClickListener ItemClickLista= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String cadAux= dispositivos.get(i).toString();
            String [] cau2= cadAux.split("\n",2); // Se separa nombre y dirección MAC del dispositivo

            if (mode.equals("lectura")){
                Intent intentconfigPar = new Intent(getApplicationContext(),configParametros.class);
                intentconfigPar.putExtra(getResources().getString(R.string.str_direccion_dispositivo),cau2[1]); // Se envío la dirección MAC al siguiente activity
                startActivity(intentconfigPar);
            }
            if (mode.equals("estimador")){
                Intent intentMedExt = new Intent(getApplicationContext(),MedicionCO2Ambiental.class);
                intentMedExt.putExtra(getResources().getString(R.string.str_direccion_dispositivo),cau2[1]); // Se envío la dirección MAC al siguiente activity
                startActivity(intentMedExt);
            }



        }
    };

}