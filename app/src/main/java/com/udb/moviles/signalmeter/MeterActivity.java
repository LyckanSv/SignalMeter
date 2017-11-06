package com.udb.moviles.signalmeter;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;

import java.util.ArrayList;

public class MeterActivity extends AppCompatActivity {
    RadarChart radarChart;
    int grade = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);

        radarChart = (RadarChart) findViewById(R.id.chart);

        /*Esta actividad consiste en una vista de una gráfica circular,
        que al iniciarse recibirá el arreglo de doublé llamado intensidades de la actividad que captura los datos, *
          */
        double datos[] = getIntent().getDoubleArrayExtra("intensidades");


        /*
          Luego para trabajar el grafico será necesario crear un arreglo de tipo RadarEntry para poder poblar
          el grafico, procederemos además a castear los datos recibidos y agregarlos a este arreglo por medio de un recorrido.**/
        ArrayList<RadarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < datos.length; i++) {
            entries.add(new RadarEntry((float) datos[i]));
            labels.add(String.valueOf(grade));
            grade += 45;
        }

        /*
         * El próximo paso será crear un objeto del tipo RadarDataSet para insertar el arreglo de
         * entries (Dibujar las líneas) en el grafico y además se configuraran ciertas propiedades de este dataset.
         */
        RadarDataSet dataset_comp1 = new RadarDataSet(entries, "Area de cobertura");

        dataset_comp1.setColor(Color.MAGENTA);
        dataset_comp1.setDrawFilled(true);

        /*
        * Luego se procede a crear el objeto RadarData que recibira un dataset creado previamente.
        * Y se crea ademas un objeto del tipo Description para agregar un texto descriptivo al grafico */
        RadarData data = new RadarData(dataset_comp1);
        data.setLabels(labels);
        Description description = new Description();
        description.setText("Espectro de WiFi");

        /*Como paso final agregamos estas propiedades al grafico
        * */
        radarChart.setDescription(description);
        radarChart.setData(data);
        radarChart.invalidate();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MeterActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();

    }
}
