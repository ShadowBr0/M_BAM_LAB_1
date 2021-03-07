package com.example.firstapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class firstService extends Service {
    private boolean state = false;
    public firstService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        state = true;
        String userName = intent.getStringExtra("userName");
        Thread thread = new Thread(() -> {
            try {
                int counter = 1;
                while (state==true){
                    Log.d("CounterValue", Integer.toString(counter));
                    Thread.sleep(1000);
                    counter++;
                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("Name", userName);
                broadcastIntent.putExtra("Ticks", counter);
                broadcastIntent.setAction("notification.counter");
                sendBroadcast(broadcastIntent);
            }catch(Exception e){
                Log.d("Exception", e.toString());
            }
        });
        thread.start();
        return Service.START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException();
    }
    @Override
    public void onDestroy() {
        state = false;
    }
}