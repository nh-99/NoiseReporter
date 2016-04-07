package com.nohowdezign.noisereporter.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nohowdezign.noisereporter.R;
import com.nohowdezign.noisereporter.audio.SoundMeter;
import com.nohowdezign.noisereporter.network.SendData;
import com.nohowdezign.noisereporter.report.ReportGenerator;

public class CreateReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ReportGenerator reportGenerator = new ReportGenerator(this);
        reportGenerator.init();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportGenerator.generateReport(view);
            }
        });

        final Handler decibelHandler = new Handler();
        decibelHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView decibelCounter = (TextView) findViewById(R.id.decibelCounter);
                decibelCounter.setText("Decibels: " + reportGenerator.getDecibels());
                decibelHandler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
