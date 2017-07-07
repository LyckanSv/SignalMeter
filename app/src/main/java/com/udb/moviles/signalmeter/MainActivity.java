package com.udb.moviles.signalmeter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
    TextView posiciones;
    ImageView routerImg;
    int grades = 0;
    int posicion = 0;
    double intensidades[] = new double[8];
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        routerImg = (ImageView) findViewById(R.id.routerImg);
        posiciones = (TextView) findViewById(R.id.posicionesTex);
        button2 = (Button) findViewById(R.id.button3);
    }

    public void activar(View view) {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            YoYo.with(Techniques.FadeOutDown)
                    .duration(700)
                    .repeat(1)
                    .playOn(findViewById(R.id.button));

            YoYo.with(Techniques.FadeInUp)
                    .duration(700)
                    .playOn(findViewById(R.id.posicionesTex));


            Button boton = (Button) findViewById(R.id.button);

            Display display = getWindowManager().getDefaultDisplay();
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
            boton.setEnabled(false);


            button2.setEnabled(true);
            button2.setVisibility(View.VISIBLE);
            posiciones.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.FadeInUp)
                    .duration(1000)
                    .playOn(findViewById(R.id.button3));

            rotate720();
            changeDelayImg();

            intensidades[posicion] = dbm();
            Toast.makeText(this, String.valueOf(dbm()), Toast.LENGTH_SHORT).show();
            posicion += 1;

        } else {

            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogoConfirmacion dialogo = new DialogoConfirmacion();
            dialogo.show(fragmentManager, "Wifi");

            Snackbar.make(view, "No tiene acesso a una red wifi", Snackbar.LENGTH_LONG)
                    .show();
        }


    }

    public void nextPosition(View view) {
        button2.setEnabled(false);
        rotate45();
        posiciones.setText("Posicion " + grades + " grados");
        intensidades[posicion] = dbm();

        //TODO metodo para guardar valores

        if (grades >= 315) {
            rotate45();
            Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(2200);
                        Intent intent = new Intent(MainActivity.this, MeterActivity.class);
                        intent.putExtra("intensidades", intensidades);
                        startActivity(intent);
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


    public int dbm() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                button2.setEnabled(true);
                int strengthInPercentage = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
                Toast.makeText(this, String.valueOf(strengthInPercentage), Toast.LENGTH_SHORT).show();
                return strengthInPercentage;

            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

    private void autoCapture() {
        new CountDownTimer(45000, 5000) {
            @Override
            public void onTick(long l) {
                try {

                    rotate45();
                    posiciones.setText("Posicion " + grades + " grados");
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
                            sleep(2200);
                            Intent intent = new Intent(MainActivity.this, MeterActivity.class);
                            intent.putExtra("intensidades", intensidades);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }.start();

    }

}
