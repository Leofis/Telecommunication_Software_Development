package com.leofis.network.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.leofis.network.database.DatabaseAdapter;

public class SilentHunter extends Service {

    private HoundDog puppy = new HoundDog(true);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.i("Service", "Service is Restarting");
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        puppy.setAdapter(adapter);
        if (intent == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            puppy.setUsername(preferences.getString("Username_Key", "default"));
            puppy.setPassword(preferences.getString("Password_Key", "default"));
        } else {
            puppy.setUsername(intent.getStringExtra("Username_Key"));
            puppy.setPassword(intent.getStringExtra("Password_Key"));
        }
        Thread task = new Thread(puppy);
        task.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        puppy.setStillRun(false);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
