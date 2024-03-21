package com.example.practice_6;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String CHANNEL_ID = "1";
    private static final int NOTIFICATION_ID = 1;
    final int PERMISSION_REQUEST_CODE = 1;
    private static NotificationManager manager;
    android.content.Context main = this;

    public void requestPermissions() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.POST_NOTIFICATIONS
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionService() {
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(myIntent, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE &&
                grantResults.length == 1) {
            if ( grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );
    }

    private void showNotification() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cat)
                .setContentTitle("Уведомление от котика")
                .setContentText("Хочу кушать!");
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for notification");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
        }

        Button button_notification = findViewById(R.id.b_notification);
        button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification();
            }
        });

        Button button_service = findViewById(R.id.b_service);
        button_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Settings.canDrawOverlays(main)) {
                    requestPermissionService();
                }

                while (!Settings.canDrawOverlays(main)) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                EditText editText = findViewById(R.id.e_text);
                String text = editText.getText().toString();
                Intent serviceIntent = new Intent(main, BannerService.class);
                serviceIntent.putExtra("value", text);
                startService(serviceIntent);
            }
        });

    }
}