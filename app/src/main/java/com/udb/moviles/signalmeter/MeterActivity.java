package com.udb.moviles.signalmeter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;

import java.util.ArrayList;
import java.util.List;

public class MeterActivity extends AppCompatActivity {
    RadarChart radarChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);

        radarChart = (RadarChart) findViewById(R.id.chart);

        double datos[] = getIntent().getDoubleArrayExtra("intensidades");

        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry((float)datos[0], 0));
        entries.add(new RadarEntry((float)datos[1], 1));
        entries.add(new RadarEntry((float)datos[2], 2));
        entries.add(new RadarEntry((float)datos[3], 3));
        entries.add(new RadarEntry((float)datos[4], 4));
        entries.add(new RadarEntry((float)datos[5], 5));
        entries.add(new RadarEntry((float)datos[6], 6));
        entries.add(new RadarEntry((float)datos[7], 7));


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
