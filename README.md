# **Documentación del Proyecto: OttoRobot**

## **Introducción**

Este proyecto es una aplicación Android que permite la conexión y comunicación con un dispositivo Bluetooth, como un robot controlado mediante señales. La aplicación incluye funcionalidades para buscar dispositivos Bluetooth emparejados, establecer una conexión y enviar comandos para controlar diferentes acciones del dispositivo, como "cantar", "bailar" o realizar la evasión de obstáculos.
Descarga de la Aplicación: [OttoRobot.apk](https://github.com/EduSantander/OttoRobot/blob/master/app/OttoRobot.apk)

## **Dependencias y permisos**

### **Dependencias**

El proyecto utiliza varias bibliotecas y componentes clave de Android, incluyendo:

- **BluetoothAdapter:** para gestionar el Bluetooth en el dispositivo.
- **BluetoothSocket:** para establecer la conexión con un dispositivo Bluetooth.
- **ActivityResultLauncher:** para manejar los resultados de la actividad que solicita habilitar el Bluetooth.

### **Permisos requeridos**

Para el correcto funcionamiento de la aplicación, se necesitan los siguientes permisos:

- **Manifest.permission.BLUETOOTH_CONNECT:** para la conexión y el control de Bluetooth.
- **Manifest.permission.ACCESS_FINE_LOCATION:** requerido para buscar dispositivos Bluetooth en ciertas versiones de Android.
Estos permisos se solicitan en tiempo de ejecución dentro de la actividad principal (MainActivity), utilizando la clase ActivityCompat.

## **Componentes del Proyecto**

### **1. MainActivity**

Es la actividad principal de la aplicación. Aquí se gestiona la interfaz de usuario y las funciones Bluetooth. Se define la lógica para la interacción con botones y la conexión a dispositivos Bluetooth.

#### **Atributos principales**

- **BluetoothAdapter mBtAdapter:** instancia del adaptador Bluetooth del dispositivo.
- **BluetoothSocket btSocket:** para crear una conexión de socket con el dispositivo seleccionado.
- **ConnectedThread MyConexionBT:** un hilo encargado de enviar datos a través del socket Bluetooth.
- **ArrayList<String> mNameDevices:** lista que almacena los nombres de dispositivos Bluetooth emparejados.
- **ArrayAdapter<String> deviceAdapter:** adaptador para manejar la visualización de los dispositivos en un Spinner.
- **Buttons:** botones para buscar dispositivos, conectar, realizar acciones (cantar, bailar) y desconectar.

#### **Métodos principales**

- **onCreate(Bundle savedInstanceState):** Inicializa la actividad, solicita permisos para Bluetooth y localización, y configura los botones y el Spinner para mostrar los dispositivos encontrados.

- **DispositivosVinculados():** Obtiene los dispositivos Bluetooth emparejados y los muestra en un Spinner para que el usuario seleccione uno. Si el adaptador Bluetooth está deshabilitado, solicita al usuario que lo active.

- **ConectarDispBT():** Intenta establecer una conexión Bluetooth con el dispositivo seleccionado. Si tiene éxito, inicia un hilo para enviar señales a través del socket Bluetooth.

### **2. Hilo de Conexión: ConnectedThread**

Un hilo que se encarga de gestionar la comunicación con el dispositivo Bluetooth. Su método write() permite enviar comandos (caracteres) al dispositivo.

### **3. Manejo de Permisos**

El manejo de permisos es esencial para acceder al Bluetooth y a la ubicación, necesarios para la búsqueda y conexión con dispositivos. Los métodos requestBluetoothConnectPermission() y requestLocationPermission() solicitan estos permisos al usuario.

## **Eventos de Botones**

Cada botón tiene una función específica que permite realizar acciones en el dispositivo Bluetooth, utilizando los métodos write() del hilo de conexión:

- **IdBtnBuscar:** busca los dispositivos Bluetooth emparejados.
- **IdBtnConectar:** conecta al dispositivo seleccionado.
- **IdBtnCantar:** envía el comando 'a' para que el dispositivo realice una acción (por ejemplo, cantar).
- **IdBtnBaile:** envía el comando 'b' para que el dispositivo realice una acción (por ejemplo, bailar).
- **IdBtnContAt:** envía el comando 'c' para que el dispositivo realice otra acción (por ejemplo, evasión de obstáculos).
- **IdBtnDesconectar:** cierra la conexión Bluetooth y finaliza la actividad.

## **Flujo de Trabajo**

- **Búsqueda de Dispositivos:** El usuario presiona el botón "Buscar", que llama al método DispositivosVinculados(). Se muestran los dispositivos emparejados en el Spinner.
- **Conexión a un Dispositivo:** Al seleccionar un dispositivo y presionar "Conectar", el método ConectarDispBT() se encarga de crear un socket y conectar al dispositivo.
- **Envío de Comandos:** Dependiendo del botón que se presione ("Cantar", "Bailar", "Evasión de Obstáculos"), se envía un comando correspondiente ('a', 'b', 'c') al dispositivo conectado utilizando el método write() del hilo de conexión.
- **Desconexión:** El usuario puede desconectar el dispositivo presionando el botón "Desconectar", que cierra el socket Bluetooth y finaliza la actividad.

## Integración con el Proyecto Arduino Otto Robot

Esta aplicación funciona en conjunto con el proyecto [Arduino Otto Robot](https://github.com/EduSantander/ArduinoOttoRobot.git) que se debe cargar en un Arduino Nano. A continuación, se detalla cómo interactúan ambos componentes:

1. **Descripción del Proyecto Arduino Otto Robot**:
   - El proyecto Arduino Otto Robot es un robot interactivo que puede ser controlado a través de Bluetooth. Implementa movimientos y melodías, permitiendo una experiencia divertida y educativa.

2. **Requisitos**:
   - Se requiere un Arduino Nano con el código del proyecto Arduino Otto Robot cargado.
   - La aplicación debe estar configurada para comunicarse a través de Bluetooth.

3. **Funcionalidades**:
   - La aplicación permite enviar comandos al robot, tales como iniciar melodías, realizar movimientos específicos (como girar o retroceder) y ejecutar secuencias de baile.
   - Utiliza el puerto serie para establecer la comunicación con el Arduino Nano, facilitando el control en tiempo real.

4. **Instrucciones de Carga**:
   - Clona el repositorio del [proyecto Arduino Otto Robot](https://github.com/EduSantander/ArduinoOttoRobot.git).
   - Abre el proyecto en el IDE de Arduino y carga el código en tu Arduino Nano.
   - Asegúrate de que la configuración de los pines y las conexiones sea la correcta.

5. **Ejemplo de Uso**:
   - Al iniciar la aplicación, selecciona el dispositivo Bluetooth correspondiente al Arduino Nano.
   - Envía comandos a través de la interfaz de la aplicación para interactuar con el robot Otto.

Para más detalles sobre la implementación del robot Otto, consulta la documentación en el repositorio del proyecto Arduino Otto Robot.

## **Consideraciones Finales** 

Este proyecto está diseñado para permitir la interacción con dispositivos Bluetooth mediante comandos enviados desde la interfaz gráfica. Es importante manejar los permisos de forma adecuada y prever casos en los que la conexión falle, proporcionando mensajes de error claros al usuario.

## **Créditos**
Este proyecto fue realizado por estudiantes universitarios pertenecientes al capítulo de Robotics and Automation Society (RAS), IEEE de la Escuela Superior Politécnica del Litoral (ESPOL) durante el primer período ordinario de 2024.
