package com.example.medidorco2;

import static com.example.medidorco2.R.drawable.alerta;
import static com.example.medidorco2.R.drawable.palomita;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Evaluacion extends AppCompatActivity {
    private float ancho,alto,largo,ocup,ACH;
    private String gettingACH;
    private Bundle bundleextras;
    private double cSS;
    private TextView tvcSS,tvcExt,tvDet;
    private static final int STATE_LISTENING = 1;
    private static final int STATE_MESSAGE_RECIEVED = 2;
    private BluetoothAdapter btAdaptador;
    private BluetoothSocket btSocket=null;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ComunicaThread comunicaThreadBT;
    private byte[] mmBuffer = new byte[1024];
    private int numBytes,i,progress;
    private String aux[];
    private ImageView ivSeñal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluacion);
        bundleextras= getIntent().getExtras(); // Clase que permite recibir datos de activity previo
        tvcSS=findViewById(R.id.tvcSS);
        tvcExt=findViewById(R.id.tvcExt);
        tvDet = findViewById(R.id.tvDet);
        btAdaptador= BluetoothAdapter.getDefaultAdapter();
        ivSeñal=findViewById(R.id.ivSeñal);

        //Recepción de datos
         ancho= Float.parseFloat(bundleextras.getString(getResources().getString(R.string.str_ancho)));
         alto= Float.parseFloat(bundleextras.getString(getResources().getString(R.string.str_alto)));
         largo= Float.parseFloat(bundleextras.getString(getResources().getString(R.string.str_largo)));
         ocup= Float.parseFloat(bundleextras.getString(getResources().getString(R.string.str_ocup)));
         gettingACH= bundleextras.getString(getResources().getString(R.string.str_ACHselec));
         setACH();
         calcularConcObj();

         tvcSS.setText(String.valueOf((int)cSS));

    }

    private void setACH() {

        switch (gettingACH){
             case "Mínimo(3 ACH)":
                 ACH=3;
                 break;
            case "Bueno(4 ACH)":
                 ACH=4;
                break;
            case "Excelente(5 ACH)":
                ACH=5;
                break;
            case "Ideal(6 ACH)":
                ACH=6;
                break;
        }

    }

    private void calcularConcObj() {
        double genCO2,objflujAE,vol;
        genCO2=ocup*0.36812;
        vol=alto*ancho*largo;
        objflujAE= (vol)*(ACH*1000)/60;

        cSS=(genCO2+objflujAE*usuario.ppmext*0.000001)/(objflujAE*0.000001);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothDevice dispositivo=btAdaptador.getRemoteDevice(usuario.BTdir); //Se establece dispositivo

        try {

            btSocket= dispositivo.createInsecureRfcommSocketToServiceRecord(btUUID); // Se inicia el socket con la configuración UUID
            Toast.makeText(getApplicationContext(),"Direccion:" +usuario.BTdir,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Falló la creación de soccket",Toast.LENGTH_SHORT).show();
        }
        try {
            btSocket.connect();

        } catch (IOException e) {
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
                    break;
                case STATE_MESSAGE_RECIEVED:
                    byte[] readBuffer= (byte[]) msg.obj; // Se obtiene el mensaje
                    String tempMsg = new String(readBuffer,0,msg.arg1);
                    Desplegar(tempMsg);

                    break;

            }
            return  true;
        }

    });

    private void Desplegar(String data) {
        aux= data.split(",",2); // Se obtiene dato de concentración y tasa separados por coma
        tvcExt.setText(aux[0]);

        int concExt = Integer.parseInt(aux[0]);

        if (concExt < cSS || concExt==cSS){
            tvDet.setText("Ventilación natural adecuada");
            tvDet.setTextColor(Color.GREEN);
            ivSeñal.setImageResource(palomita);


        }
        else{
            tvDet.setText("Se requiere ventilación asistida");
            tvDet.setTextColor(Color.RED);
            ivSeñal.setImageResource(alerta);

        }

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