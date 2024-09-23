package com.screentox.rewards;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.window.SplashScreen; // Import necessário para SplashScreen
import androidx.appcompat.app.AppCompatActivity;

import com.screentox.rewards.sharedpreferences.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicialize o SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31 ou superior
            // Access the splash screen
            SplashScreen splashScreen = getSplashScreen();
            // Redirecionamento baseado na sessão
            redirectUser();
        } else { // API inferior a 31
            // Atraso da splash screen e redirecionamento
            new android.os.Handler().postDelayed(this::redirectUser, SPLASH_TIME_OUT);
        }
    }

    private void redirectUser() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(SplashActivity.this, MainMenuActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
