package com.nohowdezign.noisereporter.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nohowdezign.noisereporter.R;
import com.nohowdezign.noisereporter.activities.NoiseMap;
import com.nohowdezign.noisereporter.audio.SoundMeter;
import com.nohowdezign.noisereporter.report.ReportGenerator;

public class ReportNoise extends Fragment {
    private SoundMeter soundMeter;
    private OnFragmentInteractionListener mListener;
    private NoiseMap noiseMap;
    private ReportGenerator reportGenerator;

    public ReportNoise() {}

    public static ReportNoise newInstance(String param1, String param2) {
        ReportNoise fragment = new ReportNoise();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ReportGenerator reportGenerator = new ReportGenerator(getActivity());
        this.reportGenerator = reportGenerator;
        reportGenerator.init();
        soundMeter = reportGenerator.getSoundMeter();

        final Handler decibelHandler = new Handler();
        decibelHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView decibelCounter = (TextView) getActivity().findViewById(R.id.decibelCounter);
                decibelCounter.setText("Decibels: " + reportGenerator.getDecibels());
                decibelHandler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(soundMeter != null) {
            soundMeter.stop();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_report_noise, container, false);

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportGenerator.generateReport(view);
            }
        });

        return layout;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
