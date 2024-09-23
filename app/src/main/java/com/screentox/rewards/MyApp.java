package com.screentox.rewards;

import android.app.Application;

import com.screentox.rewards.utils.AppLovinManager;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar o AppLovinManager
        AppLovinManager.getInstance(this);
    }
}
