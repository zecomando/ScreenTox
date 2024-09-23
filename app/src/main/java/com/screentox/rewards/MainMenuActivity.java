package com.screentox.rewards;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.applovin.sdk.AppLovinSdkSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.screentox.rewards.api.ApiRepository;
import com.screentox.rewards.api.ApiService;
import com.screentox.rewards.api.RetrofitClient;
import com.screentox.rewards.model.User;
import com.screentox.rewards.sharedpreferences.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class MainMenuActivity extends AppCompatActivity {

    private TextView unlocksView, appUsageView, totalUsageView, pointsTextView;
    private BottomNavigationView bottomNavigationView;
    private static final int REQUEST_PICK_APPWIDGET = 1001;
    private static final int REQUEST_OVERLAY_PERMISSION = 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar views
        pointsTextView  = findViewById(R.id.unlocksView);
        appUsageView = findViewById(R.id.appUsageView);
        totalUsageView = findViewById(R.id.totalUsageView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Exemplo de como definir o saldo de pontos
        int points = 100; // Obtenha o saldo de pontos real do seu sistema
        pointsTextView.setText("Points: " + points);

        // Gerenciar sessão de usuário
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserID();
            if (userId > 0) {
                Log.d("MainMenuActivity", "Usuário logado com ID: " + userId);
                fetchUserPoints(userId); // método para buscar dados do usuário
            } else {
                Log.d("MainMenuActivity", "Sessão inválida ou sem usuário logado");
                // Redirecionar para a tela de login
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // Usuário não está logado, redirecionar para a tela de login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Permanece na MainMenuActivity
                    return true;
                } else if (itemId == R.id.nav_user) {
                    // Navegar para UserActivity
                    Intent userIntent = new Intent(MainMenuActivity.this, UserActivity.class);
                    startActivity(userIntent);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    // Navegar para SettingsActivity (ou outra activity)
                    // Intent settingsIntent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                    // startActivity(settingsIntent);
                    return true;
                }

                return false;
            }
        });

        // Botão para ativar o widget
        CardView activateWidgetButton = findViewById(R.id.activate_widget_card);
        activateWidgetButton.setOnClickListener(v -> {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            ComponentName thisWidget = new ComponentName(this, Widget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            if (appWidgetIds.length > 0) {
                // Se o widget já estiver adicionado, atualize-o
                for (int appWidgetId : appWidgetIds) {
                    Widget.updateAppWidget(this, appWidgetManager, appWidgetId, 10); // Substitua pelo número real
                }
                Toast.makeText(this, "Widget ativado!", Toast.LENGTH_SHORT).show();
            } else {
                // Inicie a atividade de seleção de widgets
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetManager.getAppWidgetIds(thisWidget));
                startActivityForResult(intent, REQUEST_PICK_APPWIDGET);
            }
        });

        // Botão para solicitar permissão de overlay e exibir a OverlayScreenActivity
        CardView requestOverlayButton = findViewById(R.id.request_overlay_card);
        requestOverlayButton.setOnClickListener(v -> checkOverlayPermission());


        // Configuração inicial do AppLovinSdk
        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder("GubiyyJT8oue1u9ars7Owkr0Lw5R82D7rp-htC_Z1WbJktxNzBbhMKFtOBfZxd4O8UlTXac9HIlP4NOI8fmiDQ", this)
                .setMediationProvider(AppLovinMediationProvider.MAX)
                .build();


        // Configurações do SDK
        final AppLovinSdkSettings settings = AppLovinSdk.getInstance(this).getSettings();
        settings.setUserIdentifier("«user-ID»");
        settings.setExtraParameter("uid2_token", "«token-value»");
        settings.getTermsAndPrivacyPolicyFlowSettings().setEnabled(true);
        settings.getTermsAndPrivacyPolicyFlowSettings().setPrivacyPolicyUri(Uri.parse("https://your_company_name.com/privacy_policy"));
        settings.getTermsAndPrivacyPolicyFlowSettings().setTermsOfServiceUri(Uri.parse("https://your_company_name.com/terms_of_service"));

        // Inicialização do SDK
        AppLovinSdk.getInstance(this).initialize(initConfig, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration sdkConfig) {
                // Começar a carregar os anúncios
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_APPWIDGET && resultCode == RESULT_OK) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Configure o widget conforme necessário
                Widget.updateAppWidget(this, AppWidgetManager.getInstance(this), appWidgetId, 10); // Substitua pelo número real
                Toast.makeText(this, "Widget adicionado!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayScreen();
            } else {
                Toast.makeText(this, "Permissão de overlay não concedida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para verificar e solicitar a permissão de overlay
    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            } else {
                startOverlayScreen();
            }
        } else {
            startOverlayScreen();
        }
    }

    // Iniciar a OverlayScreenActivity
    private void startOverlayScreen() {
        Intent intent = new Intent(this, OverlayScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    private void fetchUserPoints(int userId) {
        ApiRepository apiRepository = new ApiRepository();
        apiRepository.fetchUserPoints(userId, new ApiRepository.PointsCallback() {
            @Override
            public void onSuccess(float points) { // Mude de int para float
                pointsTextView.setText("Points: " + points);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainMenuActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
