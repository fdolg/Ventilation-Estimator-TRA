package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Medicion extends AppCompatActivity {
    private static final int STATE_LISTENING = 1;
    private static final int STATE_MESSAGE_RECIEVED = 2;
    private String direccion;
    private BluetoothAdapter btAdaptador;
    private BluetoothSocket btSocket=null;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final int handlerState=0;
    private Handler hBTcomunica;
    private ComunicaThread comunicaThreadBT;
    private TextView tvConcentracion;
    private TextView tvTasa;
    private TextView tvCalidad;
    private int getValue;
    private TextView tvStatus;
    private byte[] mmBuffer = new byte[1024];
    private int numBytes; // bytes returned from read()
    private String aux[];
    private ProgressBar pbConcentracion;
    private ProgressBar pbTasa;
    private String concentracionMax;
    private String tasaMax;
    private Switch swRegistrar;
    private ManejoDB manejoDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicion);
        manejoDB=new ManejoDB(); //Clase creada para el manejo de Firebase
        tvConcentracion=findViewById(R.id.tvConcentracion);
        tvStatus=findViewById(R.id.tvStatus);
        tvCalidad=findViewById(R.id.tvCalidad);
        tvTasa=findViewById(R.id.tvTasa);
        pbConcentracion=findViewById(R.id.pbConcentracion);
        pbTasa=findViewById(R.id.pbTasa);
        btAdaptador= BluetoothAdapter.getDefaultAdapter();
        swRegistrar=findViewById(R.id.swRegistrar);
        Bundle bundleextras= getIntent().getExtras(); // Clase que permite recibir datos de activity previo
        concentracionMax = bundleextras.getString(getResources().getString(R.string.str_concentracion_max)); // Recepción de dato
        tasaMax = bundleextras.getString(getResources().getString(R.string.str_tasa_max));// Recepción de dato

        swRegistrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) { // Inicia contador al encender switch
                if(isChecked){
                    countDownTimer.start();
                }
                else {
                    countDownTimer.cancel();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundleextras= getIntent().getExtras();
        direccion = bundleextras.getString(getResources().getString(R.string.str_direccion_dispositivo)); // Se obtiene dirección MAC
        BluetoothDevice dispositivo=btAdaptador.getRemoteDevice(direccion); //Se establece dispositivo

        try {

            btSocket= dispositivo.createInsecureRfcommSocketToServiceRecord(btUUID); // Se inicia el socket con la configuración UUID
            tvStatus.setText("Escuchando");
            Toast.makeText(getApplicationContext(),"Direccion:" +direccion,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Falló la creación de soccket",Toast.LENGTH_SHORT).show();
        }
        try {
            btSocket.connect();

        } catch (IOException e) {
            Log.d("ErrorContect","Error de conexión: " +e.toString());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error al conectar con dispositivo" ,Toast.LENGTH_LONG).show();

            try {
                btSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        comunicaThreadBT= new ComunicaThread(btSocket);
        comunicaThreadBT.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Comunicación con Bluetooth por medio del protocolo serie
    private class ComunicaThread extends Thread{
        private InputStream minputStream;
        private OutputStream moutputStream;


        private ComunicaThread(BluetoothSocket socket) {
            try {
                this.minputStream= socket.getInputStream();
                this.moutputStream= socket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.d("eSocket","Error: " +e.toString());
            }
        }

        public void run() {


            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = minputStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED,numBytes,-1,mmBuffer).sendToTarget();
                } catch (IOException e) {
                    Log.d("eInputStream", "Input stream was disconnected", e);
                    tvStatus.setText("Desconectado");
                    break;
                }
            }

        }

    }
    Handler handler = new Handler(new Handler.Callback(){ // Manejador de los datos recibidos
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what){
                case STATE_LISTENING:
                    tvStatus.setText("Escuchando");
                    break;
                case STATE_MESSAGE_RECIEVED:
                    byte[] readBuffer= (byte[]) msg.obj; // Se obtiene el mensaje
                    String tempMsg = new String(readBuffer,0,msg.arg1);
                    Desplegar(tempMsg); // Se envían los datos a mostrar

                    break;

            }
            return  true;
        }

    });

    private void Desplegar(String data) {
        aux= data.split(",",2); // Se obtiene dato de concentración y tasa separados por coma
        tvConcentracion.setText(aux[0]); // Se imprimen los datos recibidos
        tvTasa.setText(aux[1]);

        setpbFlujo(Integer.parseInt(aux[0]), Integer.parseInt(aux[1]));
        setTvEstado(Integer.parseInt(aux[0]), Integer.parseInt(aux[1]));
    }

    public void setpbFlujo(int concentracion,int tasa) // Visualización de datos en progress bar
    {

        pbConcentracion.setProgress(concentracion);
        pbConcentracion.setMax(Integer.parseInt(concentracionMax)); // Máximo del progress bar

        pbTasa.setProgress(tasa);
        pbTasa.setMax(Integer.parseInt(tasaMax)); // Máximo del progress bar

    }

    public void setTvEstado(int concentracion,int tasa)
    {

        if (concentracion<=Integer.parseInt(concentracionMax)){ // Es menor o igual al límite establecido
            tvCalidad.setText("Adecuada");
            tvCalidad.setTextColor(Color.GREEN); // Cambio de color

        }
        else
        {
            tvCalidad.setText("Excedida");
            tvCalidad.setTextColor(Color.RED);
        }


    }

    public void onClickRegresar(View view) // Asociado al activity menú
    {
        Intent intentMenu =new Intent(getApplicationContext(),menu.class);
        startActivity(intentMenu);
    }

    CountDownTimer countDownTimer=new CountDownTimer(1000*60,10*1000) {
        @Override
        public void onTick(long l){ // Contador para el registro periódico de datos del sensor


            String concentracion= (String) tvConcentracion.getText(); //Obtención de datos a registrar
            String tasa=tvTasa.getText().toString();
            manejoDB.registrarEnDB(concentracion,tasa); // Método para registro en Firebase

        }

        @Override
        public void onFinish() {
            countDownTimer.start();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
         countDownTimer.cancel(); // Se apaga el Timer
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentMenu = new Intent(getApplicationContext(), menu.class);
            startActivity(intentMenu);
        }
        return super.onKeyDown(keyCode, event);
    }

}