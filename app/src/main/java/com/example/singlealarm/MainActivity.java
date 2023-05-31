package com.example.singlealarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TimePickerFragment.OnTimeSetListener {

    private Button startButton, stopButton;
    private Fragment alarmFragment, timePickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        // Initialize fragments
        alarmFragment = new AlarmFragment();
        timePickerFragment = new TimePickerFragment();

        // Add alarm fragment to activity
        addFragment(alarmFragment);

        // Set click listeners for buttons
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the alarm time from the AlarmFragment to the startAlarmService method
                AlarmFragment alarmFragment = (AlarmFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                String alarmTime = ((TextView) alarmFragment.getView().findViewById(R.id.alarm_time_text_view)).getText().toString();
                startAlarmService(alarmTime);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmService();
            }
        });
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void startAlarmService(String alarmTime) {
        // Create an intent to start the AlarmService
        Intent intent = new Intent(this, AlarmService.class);
        intent.putExtra("alarm_time", alarmTime);
        startService(intent);
    }

    private void stopAlarmService() {
        // Create an intent to stop the AlarmService
        Intent intent = new Intent(this, AlarmService.class);
        stopService(intent);
    }


    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        // Pass selected time back to MainActivity and start alarm service
        String alarmTime = String.format("%02d:%02d", hourOfDay, minute);
        startAlarmService(alarmTime);
    }
}
