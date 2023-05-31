package com.example.singlealarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

public class AlarmFragment extends Fragment {

    private TimePicker timePicker;
    private Button setAlarmButton;
    private TextView alarmTimeTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        // Initialize UI components
        timePicker = view.findViewById(R.id.time_picker);
        setAlarmButton = view.findViewById(R.id.set_alarm_button);
        alarmTimeTextView = view.findViewById(R.id.alarm_time_text_view);

        // Set click listener for set alarm button
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarmTime();
            }
        });

        return view;
    }

    private void setAlarmTime() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String alarmTime = String.format("%02d:%02d", hour, minute);
        alarmTimeTextView.setText(alarmTime);

        // Pass alarm time to MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.startAlarmService(alarmTime);

        // Start alarm service
    }

}