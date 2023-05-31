package com.example.singlealarm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getSimpleName();
    private static final long INTERVAL_TIME = 10000; // check time every 10 seconds
    private static final int MUSIC_DURATION = 10000; // music plays for 10 seconds

    private MediaPlayer mMediaPlayer;
    private Timer mTimer;
    private Handler mHandler;
    private boolean mIsPlaying = false;
    private String mAlarmTime;
    private boolean mStopRequested = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received broadcast");
            stopSelf();
            stopService();
        }
    };

    private final BroadcastReceiver mBatteryLowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received battery low broadcast");
            stopSelf();
            stopService();
        }
    };

    private final BroadcastReceiver mPowerConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received power connected broadcast");
            stopSelf();
            stopService();
        }
    };

    private final BroadcastReceiver mBatteryOkayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received battery okay broadcast");
            stopService();
            stopSelf();
            stopService();
        }
    };

    private final BroadcastReceiver mStopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received stop service broadcast");
            stopSelf();
            stopService();
        }
    };

    private final BroadcastReceiver mIncomingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                Log.i(TAG, "Received incoming call");
                stopSelf();
                stopService();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        registerReceiver(mStopServiceReceiver, new IntentFilter("stop_service"));
        registerReceiver(mBatteryLowReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(mPowerConnectedReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        registerReceiver(mBatteryOkayReceiver, new IntentFilter(Intent.ACTION_BATTERY_OKAY));
        registerReceiver(mIncomingCallReceiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mAlarmTime = intent.getStringExtra("alarm_time");
        }

        startService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mStopServiceReceiver);
        unregisterReceiver(mBatteryLowReceiver);
        unregisterReceiver(mPowerConnectedReceiver);
        unregisterReceiver(mBatteryOkayReceiver);
        unregisterReceiver(mIncomingCallReceiver);
        stopService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startService() {
        Log.i(TAG, "Service started");

        // Register the broadcast receiver
        // This will stop the service if a broadcast is received
        registerReceiver(mReceiver, getBroadcastIntentFilter());


        // Start the timer
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkTime();
            }
        }, INTERVAL_TIME, INTERVAL_TIME);
    }

    public static IntentFilter getBroadcastIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("STOP_ALARM_SERVICE");
        return filter;
    }


    private void stopService() {
        Log.i(TAG, "Service stopped");
        mHandler.post(() -> {
            Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
        });

        // Unregister the broadcast receiver

        if (mReceiver != null && isReceiverRegistered(mReceiver)) {
            unregisterReceiver(mReceiver);
        }
        //unregisterReceiver(mReceiver);

        // Stop the timer
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        // Stop the music if it's currently playing
        stopMusic();

        // Stop the service
        stopSelf();
    }

    private void stopMusic() {

        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Stop the service when the music stops playing
                    stopSelf();
                }
            });
            mMediaPlayer.stop();
            //mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mIsPlaying = false;
        }
    }

    private void checkTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        if (mAlarmTime.equals(currentTime) && !mIsPlaying) {
            mHandler.post(() -> {
                playMusic();
            });
        }
    }

    private void playMusic() {
        Log.i(TAG, "Playing music");

        mIsPlaying = true;
        mMediaPlayer = MediaPlayer.create(this, R.raw.alarm_music);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        // Stop the music after 10 seconds
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopMusic();
                stopService();
            }
        }, MUSIC_DURATION);

        mHandler.post(() -> {
            Toast.makeText(getApplicationContext(), "Alarm ringing", Toast.LENGTH_SHORT).show();
        });
    }
    private boolean isReceiverRegistered(BroadcastReceiver receiver) {
        IntentFilter filter = getBroadcastIntentFilter();
        return getApplicationContext().registerReceiver(receiver, filter) != null;
    }

}


