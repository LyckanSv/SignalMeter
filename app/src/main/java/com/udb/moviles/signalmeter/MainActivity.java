package com.udb.moviles.signalmeter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class MainActivity extends AppCompatActivity {


    /**
     * Para iniciar se utilizarán principalmente 3 variables
     */
    int grades = 0;
    int posicion = 0;
    double intensidades[] = new double[8];

    TextView posiciones;
    ImageView routerImg;
    Button manualButton;
    Button mainButton;
    Button autoButton;
    Display display;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = (ConstraintLayout) findViewById(R.id.layout);
        routerImg = (ImageView) findViewById(R.id.routerImg);
        posiciones = (TextView) findViewById(R.id.posicionesTex);
        mainButton = (Button) findViewById(R.id.mainButton);
        manualButton = (Button) findViewById(R.id.buttonManual);
        autoButton = (Button) findViewById(R.id.buttonAuto);


    }

    public void activar(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Seleccione");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Seleccione el modo para capturar datos");
        alertDialog.setPositiveButton("Manual", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                prepareLayouts(10);
            }
        });
        alertDialog.setNegativeButton("Automatico", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                prepareLayouts(20);
            }
        });
        alertDialog.show();

    }


    /**
     * Para proceder a obtener los valores siguientes en el modo manual se
     * habilita un nuevo botón, responde de la siguiente manera al hacer click:
     * se tiene condicion if, donde se evalúa la cantidad de grados recorridos y si esta es
     * igual o mayor a 315° se procede a hacer una última rotación en la imagen y luego a abrir la actividad que
     * desplegara el grafico, si los grados son menores a 315° se procede a sumar 1 en la variable posición para poder
     * agregar un valor más dentro del arreglo intensidades.
     **/
    public void manualCapture(View view) {
        rotate45();
        posiciones.setText("Posicion " + grades + " grados");
        intensidades[posicion] = dbm();

        if (grades >= 315) {
            manualButton.setEnabled(false);
            rotate45();
            Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(2200);
                        Intent intent = new Intent(MainActivity.this, MeterActivity.class);
                        intent.putExtra("intensidades", intensidades);
                        startActivity(intent);
                        MainActivity.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        } else {
            posicion += 1;
        }


    }


    /**
     * Método personalizado llamado rotate45();
     * que tal como su nombre indica rota una imagen en la interfaz 45° en
     * dirección de las agujas del reloj
     */
    public void rotate45() {
        grades += 45;
        routerImg.animate().rotation(grades).setDuration(500);

    }


    public void rotate720() {
        routerImg.animate().rotation(1440).setDuration(1000).setStartDelay(1000);

    }

    public void changeDelayImg() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(1200);
                    routerImg.setImageResource(R.drawable.modem2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /***dbm(); es un método que regresara un entero que representa el nivel de intensidad
     * del 0 al 100 de la señal de inalámbrica en determinado momento  */
    public int dbm() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                manualButton.setEnabled(true);

                /*Al utilizar el método calculateSignalLevel(), necesitamos ingresar 2 parámetros siendo el
                  primero El RSSI (Received signal strength indication) y el segundo los niveles que deseamos obtener,
                  normalmente se definen 5 pero en nuestro caso para graficar solicitaremos 100 niveles).*
                  */
                int strengthInPercentage = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
                Toast.makeText(this, "Valor capturado: " + String.valueOf(strengthInPercentage), Toast.LENGTH_SHORT).show();
                return strengthInPercentage;

            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    public void autoCap(View view) {
        autoCapture();
    }


    /**
     * El modo automático de la aplicación funciona de manera similar al modo manual:
     * la captura de datos se hace con las mismas variables, sin embargo en este caso se
     * ha creado un método personalizado llamado autoCapture();
     * Este método contiene un elemento nuevo llamado CountDownTimer(),
     * que nos permitirá repetir una tarea cada cierto intervalo de tiempo, para que esto sea posible es necesario ingresar
     * 2 parámetros de tipo long, el primero será la cantidad  total en milisegundos que se ejecutara la tarea,
     * y el segundo parámetro será el intervalo en milisegundos que se tomara para repetir la tarea.
     * En nuestro caso ingresaremos el primer parámetro como 45000 (45 segundos) y el segundo parámetro será 5,
     * tomaremos en cuenta que la primer repetición iniciara a los 5 segundos por lo que en total tendremos
     * 8 repeticiones para cubrir los 360° y generar el grafico.
     * Utilizando el método onTick() almacenaremos el valor obtenido en ese momento dentro del arreglo intensidades
     * y luego al finalizar, se ejecutara el método onFinish() que ejecutara el código para abrir una actividad nueva que generara el grafico.
     */
    private void autoCapture() {
        autoButton.setEnabled(false);
        new CountDownTimer(45000, 5000) {
            @Override
            public void onTick(long l) {
                try {

                    rotate45();
                    posiciones.setText("Camine alrededor del router, cada 5 segundos se hara una medicion para graficarla");
                    intensidades[posicion] = dbm();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                posicion += 1;
            }

            @Override
            public void onFinish() {
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Intent intent = new Intent(MainActivity.this, MeterActivity.class);
                            intent.putExtra("intensidades", intensidades);
                            startActivity(intent);
                            MainActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }.start();

    }


    /**
     * Al iniciar la aplicación y en cada momento que esta ventana sea visible,
     * será necesario verificar si tenemos acceso al servicio de conexión inalámbrica de Android,
     * para ello se ha creado un método llamado checkNetwork(), que contiene los elementos: ConnectivityManager con el cual solicitamos
     * acceso al servicio de conectividad y luego con NetworkInfo obtenemos la informacion relativa a la red,
     * en específico a la red inalámbrica.
     */
    private void checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        /*Luego se realiza una comprobación para verificar si la conexión inalámbrica se encuentra habilitada y
           en caso de no ser así se le solicita al usuario por medio de un cuadro de dialogo que la habilite **/
        if (mWifi.isConnected()) {
            mainButton.setEnabled(true);
        } else {

            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogoConfirmacion dialogo = new DialogoConfirmacion();
            dialogo.show(fragmentManager, "Wifi");

            Snackbar.make(constraintLayout, "No tiene acesso a una red wifi", Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /**
     * Si se detecta que el WiFi está habilitado se procede a animar la interfaz por medio del metodo personalizado prepareLayouts()
     * que muestra una ventana de dialogo en la que le solicita al usuario que seleccione el tipo de medición (automática o manual)
     * y a partir de esto mostrar los botones para iniciar la captura de datos, además que se realiza la primer captura de datos
     * por medio de: intensidades[0] = dbm();
     **/
    private void prepareLayouts(int selection) {

        YoYo.with(Techniques.FadeOutDown)
                .duration(700)
                .repeat(1)
                .playOn(findViewById(R.id.mainButton));

        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .playOn(findViewById(R.id.posicionesTex));

        posiciones.setVisibility(View.VISIBLE);
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        AnimationSet as = new AnimationSet(true);
        as.setFillAfter(true);

        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, height / 7);
        ta.setDuration(1000);
        ta.setFillAfter(true);
        as.addAnimation(ta);

        ScaleAnimation ta2 = new ScaleAnimation(1, 0.80f, 1, 0.80f, routerImg.getHeight() / 2, routerImg.getWidth() / 2);
        ta2.setDuration(1000);
        ta2.setStartOffset(1000);
        ta2.setFillAfter(true);
        as.addAnimation(ta2);

        routerImg.setAnimation(as);
        routerImg.startAnimation(as);
        mainButton.setEnabled(false);
        mainButton.setVisibility(View.GONE);

        switch (selection) {
            case 10:
                manualButton.setEnabled(true);
                manualButton.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp)
                        .duration(1000)
                        .playOn(findViewById(R.id.buttonManual));
                break;
            case 20:
                autoButton.setEnabled(true);
                autoButton.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp)
                        .duration(1000)
                        .playOn(findViewById(R.id.buttonAuto));
                break;
            default:
                break;
        }

        rotate720();
        changeDelayImg();

        intensidades[posicion] = dbm();
        posicion += 1;
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkNetwork();
    }


}
