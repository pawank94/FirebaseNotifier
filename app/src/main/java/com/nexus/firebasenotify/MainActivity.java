package com.nexus.firebasenotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nexus.firebasenotify.service.FirebaseService;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button getToken = (Button) findViewById(R.id.button);
        Button multiNotificationEnabled = (Button) findViewById(R.id.multiNotificationEnabled);
        TextView token = (TextView) findViewById(R.id.token);
        getToken.setOnClickListener(v -> {
            final String[] tokenString = new String[1];
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                tokenString[0] = task.getResult();

                token.setText(tokenString[0]);

                // Log and toast
                Toast.makeText(MainActivity.this, "Firebase Token received", Toast.LENGTH_SHORT).show();
            });
        });
        multiNotificationEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = settings.edit();
                Boolean currentFlag = settings.getBoolean(FirebaseService.MULTI_NOTIFICATION_ENABLED, false);
                editor.putBoolean(FirebaseService.MULTI_NOTIFICATION_ENABLED, !currentFlag);
                editor.apply();
                Toast.makeText(MainActivity.this, "MultiNotification set to " + !currentFlag, Toast.LENGTH_SHORT).show();
            }
        });
    }
}