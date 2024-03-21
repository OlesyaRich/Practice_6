package com.example.practice_6;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class BannerService extends Service {
    private WindowManager windowManager;
    private View bannerView;
    android.content.Context service = this;
    String text;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getStringExtra("value");
        showBanner();
        return super.onStartCommand(intent, flags, startId);
    }

    /*@Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        bannerView = LayoutInflater.from(this).inflate(R.layout.layout_banner, null);
        Button button_close = bannerView.findViewById(R.id.b_close);

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                windowManager.removeView(bannerView);
                stopSelf();
                onDestroy();
            }
        });
    }*/


    private void showBanner() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        bannerView = inflater.inflate(R.layout.layout_banner, null);
        if (text.length() > 0) {
            TextView textView = bannerView.findViewById(R.id.banner_text);
            textView.setText(text);
        }
        bannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(service, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.startActivity(intent);
            }
        });

        windowManager.addView(bannerView, params);
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.height = 300;
        params.width = 600;
        windowManager.updateViewLayout(bannerView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null && bannerView != null) {
            windowManager.removeView(bannerView);
        }
    }
}
