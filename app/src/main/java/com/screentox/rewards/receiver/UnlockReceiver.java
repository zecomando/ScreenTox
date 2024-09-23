package com.screentox.rewards.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.screentox.rewards.OverlayScreenActivity;
import com.screentox.rewards.api.ApiService;
import com.screentox.rewards.api.RetrofitClient;
import com.screentox.rewards.model.UnlockDevice;
import com.screentox.rewards.service.UnlockService;
import com.screentox.rewards.sharedpreferences.SessionManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnlockReceiver extends BroadcastReceiver {

    private static final String TAG = "UnlockReceiver";

    private static long lastUnlockTime = 0;
    private static final long DEBOUNCE_INTERVAL = 5000; // 5 segundos

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUnlockTime > DEBOUNCE_INTERVAL) {
                lastUnlockTime = currentTime;

                Log.d(TAG, "Dispositivo desbloqueado - onReceive() chamado.");

                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if (keyguardManager != null && !keyguardManager.isKeyguardLocked()) {
                    Log.d(TAG, "Dispositivo está desbloqueado.");

                    Intent serviceIntent = new Intent(context, UnlockService.class);
                    context.startService(serviceIntent);

                    Log.d(TAG, "Dispositivo desbloqueado");
                    Intent overlayIntent = new Intent(context, OverlayScreenActivity.class);
                    overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(overlayIntent);

                    SessionManager sessionManager = new SessionManager(context);
                    if (sessionManager.isLoggedIn()) {
                        int userId = sessionManager.getUserID();
                        if (userId > 0) {
                            Log.d("MainMenuActivity", "Usuário logado com ID: " + userId);
                            sendUnlockData(context, userId);
                        }
                    } else {
                        Log.d(TAG, "Usuário não está logado.");
                    }
                } else {
                    Log.d(TAG, "Dispositivo ainda está bloqueado.");
                }
            }
        }
    }

    private void sendUnlockData(Context context, int userId) {
        UnlockDevice unlockDevice = new UnlockDevice(userId);

        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = service.sendUnlockData(unlockDevice);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Dados de desbloqueio enviados com sucesso.");
                } else {
                    Log.e(TAG, "Erro no envio de dados de desbloqueio: código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Erro ao enviar dados de desbloqueio: " + t.getMessage());
            }
        });
    }
}
