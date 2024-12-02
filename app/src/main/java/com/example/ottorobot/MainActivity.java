package com.example.ottorobot;

// Importación de bibliotecas necesarias
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

// Clase principal de la aplicación que extiende AppCompatActivity
public class MainActivity extends AppCompatActivity {
    // Declaración de constantes para solicitudes de permisos y UUID de Bluetooth
    private static final String TAG = "MainActivity"; //Tag para registro de logs
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID para comunicación Bluetooth
    private static final int REQUEST_ENABLE_BT = 1; // Solicitud para habilitar Bluetooth
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3; // Solicitud de permisos de conexión Bluetooth
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2; // Solicitud de permisos de ubicación

    // Variables para manejar Bluetooth y la interfaz de usuario
    private BluetoothAdapter mBtAdapter;
    private BluetoothSocket btSocket;
    private BluetoothDevice DispositivoSeleccionado;
    private ConnectedThread MyConexionBT; // Clase interna para manejar la comunicación Bluetooth
    private ArrayList<String> mNameDevices = new ArrayList<>(); // Lista de nombres de dispositivos Bluetooth
    private ArrayAdapter<String> deviceAdapter;

    // Botones y Spinner de la interfaz de usuario
    Button IdBtnBuscar,IdBtnConectar,IdBtnCantar,IdBtnBaile,IdBtnContAt,IdBtnDesconectar;
    Spinner IdDisEncontrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración inicial de la actividad
        EdgeToEdge.enable(this); //Configuración para mostrar contenido en pantalla completa
        setContentView(R.layout.activity_main); //Establece el diseño de la actividad

        // Solicitar permisos necesarios
        requestBluetoothConnectPermission();
        requestLocationPermission();

        // Manejo de insets de la ventana para ajustar los elementos de la interfaz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de elementos de la interfaz
        IdBtnBuscar = findViewById(R.id.IdBtnBuscar);
        IdBtnConectar = findViewById(R.id.IdBtnConectar);
        IdBtnCantar = findViewById(R.id.IdBtnCantar);
        IdBtnBaile = findViewById(R.id.IdBtnBaile);
        IdBtnContAt = findViewById(R.id.IdBtnContAt);
        IdBtnDesconectar = findViewById(R.id.IdBtnDesconectar);
        IdDisEncontrados = findViewById(R.id.IdDisEncontrados);

        // Configuración del adaptador para mostrar dispositivos encontrados
        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,mNameDevices);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IdDisEncontrados.setAdapter(deviceAdapter);

        // Configuración de eventos para los botones
        IdBtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para evento de pulsar boton
                DispositivosVinculados();
            }
        });

        IdBtnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para evento de pulsar boton
                ConectarDispBT();
            }
        });

        IdBtnCantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para evento de pulsar boton
                Toast.makeText(getBaseContext(),"Se ha presionado CANTAR",Toast.LENGTH_SHORT).show();
                try {
                    // Intentar enviar la señal por Bluetooth
                    MyConexionBT.write('a');
                } catch (Exception e) {
                    // Si ocurre un error, como que el dispositivo no esté conectado, mostrar un Toast
                    Toast.makeText(getBaseContext(), "Para realizar esta accion conecte un dispositivo", Toast.LENGTH_LONG).show();
                }
            }
        });

        IdBtnBaile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para evento de pulsar boton
                Toast.makeText(getBaseContext(),"Se ha presionado BAILAR",Toast.LENGTH_SHORT).show();
                try {
                    // Intentar enviar la señal por Bluetooth
                    MyConexionBT.write('b');
                } catch (Exception e) {
                    // Si ocurre un error, como que el dispositivo no esté conectado, mostrar un Toast
                    Toast.makeText(getBaseContext(), "Para realizar esta accion conecte un dispositivo", Toast.LENGTH_LONG).show();
                }
            }
        });

        IdBtnContAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para evento de pulsar boton
                Toast.makeText(getBaseContext(),"Se ha presionado EVASOR DE OBSTACULOS",Toast.LENGTH_SHORT).show();
                try {
                    // Intentar enviar la señal por Bluetooth
                    MyConexionBT.write('c');
                } catch (Exception e) {
                    // Si ocurre un error, como que el dispositivo no esté conectado, mostrar un Toast
                    Toast.makeText(getBaseContext(), "Para realizar esta accion conecte un dispositivo", Toast.LENGTH_LONG).show();
                }
            }
        });

        IdBtnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Codigo para evento de pulsar boton
                if (btSocket!=null)
                {
                    try {btSocket.close();
                        btSocket = null;
                        Toast.makeText(getBaseContext(), "Dispositivo desconectado", Toast.LENGTH_SHORT).show();}
                    catch (IOException e){
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();}}
                }
                finish();
            }
        });
    }

    // Método para registrar resultados de actividades
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == MainActivity.REQUEST_ENABLE_BT) {
                        Log.d(TAG, "ACTIVIDAD REGISTRADA");
                        //Toast.makeText(getBaseContext(), "ACTIVIDAD REGISTRADA", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // Método para buscar dispositivos vinculados
    public void DispositivosVinculados() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            showToast("Bluetooth no disponible en este dispositivo.");
            finish();
            return;
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            someActivityResultLauncher.launch(enableBtIntent);
        }

        // Listener para seleccionar un dispositivo del Spinner
        IdDisEncontrados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DispositivoSeleccionado = getBluetoothDeviceByName(mNameDevices.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                DispositivoSeleccionado = null;
            }
        });

        // Obtiene la lista de dispositivos emparejados y los muestra en el Spinner
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mNameDevices.add(device.getName());
            }
            deviceAdapter.notifyDataSetChanged();
        } else {
            showToast("No hay dispositivos Bluetooth emparejados.");
        }
    }

    // Solicita permisos de ubicación
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
    }

    // Solicita permisos de conexión Bluetooth
    private void requestBluetoothConnectPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso concedido, ahora puedes utilizar funciones de Bluetooth que requieran BLUETOOTH_CONNECT");
            } else {
                Log.d(TAG, "Permiso denegado, debes manejar este caso según tus necesidades");
            }
        }
    }

    // Obtiene un dispositivo Bluetooth por su nombre
    private BluetoothDevice getBluetoothDeviceByName(String name) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, " ----->>>>> ActivityCompat.checkSelfPermission");
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }

    // Método para conectar un dispositivo Bluetooth
    private void ConectarDispBT() {
        if (DispositivoSeleccionado == null) {
            showToast("Selecciona un dispositivo Bluetooth.");
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            btSocket = DispositivoSeleccionado.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
            btSocket.connect();
            MyConexionBT = new ConnectedThread(btSocket);
            MyConexionBT.start();
            showToast("Conexión exitosa.");
            MyConexionBT.write('o'); // Enviar un comando inicial para comprobar conexión serial
        } catch (IOException e) {
            showToast("Error al conectar con el dispositivo.");
        }
    }

    // Clase interna para manejar la comunicación Bluetooth
    private class ConnectedThread extends Thread {
        private final OutputStream mmOutStream;
        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                showToast("Error al crear el flujo de datos.");
            }

            mmOutStream = tmpOut;
        }
        public void write(char input) {
            //byte msgBuffer = (byte)input;
            try {
                mmOutStream.write((byte)input);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    // Método para mostrar mensajes en pantalla (Toast)
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 04 -----------------------------------------------------------------
}