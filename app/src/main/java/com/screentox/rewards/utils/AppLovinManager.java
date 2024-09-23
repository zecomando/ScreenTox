package com.screentox.rewards.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.screentox.rewards.service.UnlockService;

public class AppLovinManager {

    private static AppLovinManager instance;
    private AppLovinSdk sdk;

    // Construtor privado para o padrão Singleton
    private AppLovinManager(Context context) {
        // Iniciar o serviço UnlockService
        Intent serviceIntent = new Intent(context, UnlockService.class);
        context.startService(serviceIntent);

        // Obter instância do SDK e configurar
        sdk = AppLovinSdk.getInstance(context);
        AppLovinSdkSettings settings = sdk.getSettings();

        // Configurar as URLs de política de privacidade e termos de serviço
        settings.getTermsAndPrivacyPolicyFlowSettings().setEnabled(true);
        settings.getTermsAndPrivacyPolicyFlowSettings().setPrivacyPolicyUri(Uri.parse("https://your_company_name.com/privacy_policy"));
        settings.getTermsAndPrivacyPolicyFlowSettings().setTermsOfServiceUri(Uri.parse("https://your_company_name.com/terms_of_service"));

        // O SDK será inicializado automaticamente
    }

    // Método para obter a instância Singleton da classe
    public static AppLovinManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppLovinManager(context.getApplicationContext());
        }
        return instance;
    }

    // Método para obter o SDK do AppLovin
    public AppLovinSdk getSdk() {
        return sdk;
    }
}
