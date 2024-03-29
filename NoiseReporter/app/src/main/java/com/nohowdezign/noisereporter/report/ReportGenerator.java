package com.nohowdezign.noisereporter.report;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.nohowdezign.noisereporter.activities.NoiseMap;
import com.nohowdezign.noisereporter.audio.SoundMeter;
import com.nohowdezign.noisereporter.network.SendData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {
    private SoundMeter soundMeter;
    private List<Double> decibelMeasures = new ArrayList<Double>();
    private List<Double> averageDecibelMeasures = new ArrayList<Double>();
    private Activity noiseMap;

    public ReportGenerator(Activity map) {
        this.noiseMap = map;
    }

    public void init() {
        soundMeter = new SoundMeter();
        if(!soundMeter.isRunning()) {
            soundMeter.start();
        }
    }

    public void generateReport(View view) {
        storeDecibels();
        calculateAverages();
        sendData(view);
    }

    private void storeDecibels() {
        // Thread to get the dB
        final Handler decibelHandler = new Handler();
        decibelHandler.post(new Runnable() {
            @Override
            public void run() {
                decibelMeasures.add(soundMeter.getDb());
                decibelHandler.postDelayed(this, 5000);
            }
        });
    }

    private void calculateAverages() {
        // Thread to average the dB every 30 seconds
        final Handler averageHandler = new Handler();
        averageHandler.post(new Runnable() {
            @Override
            public void run() {
                double average = 0;
                int count = 0;
                for(Double decibel : decibelMeasures) {
                    average += decibel;
                    count += 1;
                }
                average = (average / count);
                averageDecibelMeasures.add(average);
                decibelMeasures.removeAll(decibelMeasures); // Wipe out the old measures to conserve memory
                averageHandler.postDelayed(this, 30000);
            }
        });
    }

    private void sendData(final View view) {
        // Thread to send the data to the server
        final Handler dataHandler = new Handler();
        dataHandler.post(new Runnable() {
            boolean firstRun = true;

            @Override
            public void run() {
                System.out.println("Sending shit");
                SendData dataSender = new SendData();
                if (averageDecibelMeasures.size() > 0) {
                    for (Double decibel : averageDecibelMeasures) {
                        try {
                            dataSender.send(noiseMap.getApplicationContext(), getLocation().getLatitude(), getLocation().getLongitude(), decibel);
                        } catch (JSONException|NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    averageDecibelMeasures.clear(); // Clear up averages so we don't have duplicate data entries
                }
                // Check to stop
                if(!firstRun) {
                    Snackbar snackbar = Snackbar
                            .make(view, "Report submitted successfully.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    soundMeter.stop();
                    dataHandler.removeCallbacks(this);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(view, "Running (this will take a minute)...", Snackbar.LENGTH_LONG);
                    firstRun = false;
                    dataHandler.postDelayed(this, 60000);
                }
            }
        });
    }

    private Location getLocation() {
        LocationManager lm = (LocationManager) noiseMap.getSystemService(Context.LOCATION_SERVICE);try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return location;
        } catch (final SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getDecibels() {
        return soundMeter.getDb();
    }

    public SoundMeter getSoundMeter() {
        return this.soundMeter;
    }
}
