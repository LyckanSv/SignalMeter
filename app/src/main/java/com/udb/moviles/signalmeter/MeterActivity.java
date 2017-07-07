package com.udb.moviles.signalmeter;


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


        double datos[] = getIntent().getDoubleArrayExtra("intensidades");

        ArrayList<RadarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < datos.length; i++) {
            entries.add(new RadarEntry((float) datos[i], i));
            labels.add(String.valueOf(grade));
            grade += 45;
        }

        RadarDataSet dataset_comp1 = new RadarDataSet(entries, "Area de cobertura");

        dataset_comp1.setColor(Color.MAGENTA);
        dataset_comp1.setDrawFilled(true);


        RadarData data = new RadarData(dataset_comp1);
        Description description = new Description();
        description.setText("Espectro de WiFi");
        radarChart.setDescription(description);
        radarChart.setData(data);
        radarChart.invalidate();

    }


}
