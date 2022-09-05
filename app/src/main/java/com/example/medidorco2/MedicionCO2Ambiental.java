package com.example.medidorco2;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MedicionCO2Ambiental extends AppCompatActivity {
    private Button btnComenzar;
    private TextView tvContando;
    private ProgressBar pbTiempo;

    //Atributos BT
    private static final int STATE_LISTENING = 1;
    private static final int STATE_MESSAGE_RECIEVED = 2;
    private BluetoothAdapter btAdaptador;
    private BluetoothSocket btSocket=null;
    private static final UUID btUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ComunicaThread comunicaThreadBT;
    private byte[] mmBuffer = new byte[1024];
    private int numBytes,i,progress;
    private float ppmAcum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicion_co2_ambiental);
        btnComenzar=findViewById(R.id.btnComenzar);
        tvContando=findViewById(R.id.tvMidiendo);
        pbTiempo=findViewById(R.id.pbTiempo);
        progress=0;
        pbTiempo.setMax(60);
        i=0;
        ppmAcum=0;
        //Configuracion BT
        btAdaptador= BluetoothAdapter.getDefaultAdapter();
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
                    Promediar(tempMsg); // Se envían los datos a promediar

                    break;

            }
            return  true;
        }

    });
    private void Promediar(String data) {
        String aux []= data.split(",",2); // Se obtiene dato de concentración y tasa separados por coma
        if (i<55)
        {
            ppmAcum= Float.parseFloat(aux[0])+ppmAcum;
            i++;
        }

    }


    public void onClickComenzar(View view) {
        countDownTimer.start();
        btnComenzar.setVisibility(View.INVISIBLE);
        tvContando.setText("Midiendo");
    }

    CountDownTimer countDownTimer=new CountDownTimer(1000*60,1*1000) {
        @Override
        public void onTick(long l){ // Contador para el registro periódico de datos del sensor
            progress++;
            pbTiempo.setProgress(progress);

        }

        @Override
        public void onFinish() {
            usuario.ppmext=ppmAcum/i;
            Intent intentEvalParam = new Intent(getApplicationContext(),getEvalParametros.class);
            startActivity(intentEvalParam);

        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Método para controlar el bóton BACK
        if (keyCode==event.KEYCODE_BACK){
            Intent intentMenu = new Intent(getApplicationContext(), menu.class);
            startActivity(intentMenu);
        }
        return super.onKeyDown(keyCode, event);
    }
}