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
    TextView posiciones;
    ImageView routerImg;
    int grades = 0;
    int posicion = 0;
    double intensidades[] = new double[8];
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

    public void manualCapture(View view) {
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
                manualButton.setEnabled(true);
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

    public void autoCap(View view) {
        autoCapture();
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

    private void checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

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
        Toast.makeText(this, String.valueOf(dbm()), Toast.LENGTH_SHORT).show();
        posicion += 1;
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkNetwork();
    }


}
