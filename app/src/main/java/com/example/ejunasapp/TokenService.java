package com.example.ejunasapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;

public class TokenService extends Service {
    private String TAG = "TokenService";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                WebAPI.getToken(Tools.RestURL+"auth/login/refresh");
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            scheduleNextUpdate();

        }
        private void scheduleNextUpdate()
        {
            Intent intent = new Intent(TokenService.this, TokenService.class);
            PendingIntent pendingIntent =
                    PendingIntent.getService(TokenService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // The update frequency should often be user configurable.  This is not.

            long currentTimeMillis = System.currentTimeMillis();
            long nextUpdateTimeMillis = (long)(currentTimeMillis + 14.6 * DateUtils.MINUTE_IN_MILLIS);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

    }
}
