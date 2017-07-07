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


        ArrayList<Integer> datos = getIntent().getIntegerArrayListExtra("intensidades");

        ArrayList<RadarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < datos.size(); i++) {
            entries.add(new RadarEntry((float) datos.get(i)));
            labels.add(String.valueOf(grade));
            grade += 45;
        }

        RadarDataSet dataset_comp1 = new RadarDataSet(entries, "Area de cobertura");

        dataset_comp1.setColor(Color.MAGENTA);
        dataset_comp1.setDrawFilled(true);


        RadarData data = new RadarData(dataset_comp1);
        data.setLabels(labels);
        Description description = new Description();
        description.setText("Espectro de WiFi");
        radarChart.setDescription(description);
        radarChart.setData(data);
        radarChart.invalidate();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MeterActivity.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();

    }
}
