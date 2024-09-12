package com.pleasanttours.myapplication;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnlockReceiver extends BroadcastReceiver {

    private static final String TAG = "UnlockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            Log.d(TAG, "Dispositivo desbloqueado - onReceive() chamado.");

            // Usar KeyguardManager para verificar se o dispositivo está realmente desbloqueado
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null && !keyguardManager.isKeyguardLocked()) {
                Log.d(TAG, "Dispositivo está desbloqueado.");

                // Incrementar a contagem de desbloqueios
                UnlockService.incrementUnlockCount();

                // Iniciar o UnlockService para registrar o desbloqueio
                Intent serviceIntent = new Intent(context, UnlockService.class);
                context.startService(serviceIntent);

                // Obter a contagem de desbloqueios do UnlockService
                int unlockCount = UnlockService.getUnlockCount();

                // Imprimir no log a contagem de desbloqueios atualizada
                Log.d(TAG, "Dispositivo desbloqueado. Contagem atual: " + unlockCount);

                // Dados fictícios para exemplo
                long totalUsage = 30000; // Exemplo de tempo total de uso
                String appUsage = "com.whatsapp: 1000 seg"; // Exemplo de uso de aplicativo

                // Enviar dados usando Retrofit
                sendUsageData(unlockCount, totalUsage, appUsage);
            }
            else {
                Log.d(TAG, "Dispositivo ainda está bloqueado.");
            }
        }
    }

    private void sendUsageData(int unlocks, long totalUsage, String appUsage) {
        UsageData usageData = new UsageData(unlocks, totalUsage, appUsage);

        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = service.sendUsageData(usageData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Dados enviados com sucesso.");
                } else {
                    Log.e(TAG, "Erro no envio de dados: código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Erro ao enviar dados: " + t.getMessage());
            }
        });
    }
}
