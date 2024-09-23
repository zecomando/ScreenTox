package com.screentox.rewards.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.screentox.rewards.R;
import com.screentox.rewards.receiver.UnlockReceiver;

public class UnlockService extends Service {

    private static final String TAG = "UnlockService";
    private static boolean isServiceRunning = false;  // Variável de controle para o estado do serviço
    private UnlockReceiver unlockReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Serviço iniciado");

        if (!isServiceRunning) {
            isServiceRunning = true;
            startForegroundServiceWithNotification();

            // Verificar se o UnlockReceiver já está registrado
            if (unlockReceiver == null) {
                unlockReceiver = new UnlockReceiver();
                IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
                registerReceiver(unlockReceiver, filter);
                Log.d(TAG, "UnlockReceiver registrado");
            }
        } else {
            Log.d(TAG, "Serviço já está em execução");
        }
    }


    private void startForegroundServiceWithNotification() {
        String channelId = "UnlockServiceChannel";
        String channelName = "Unlock Service Channel";

        // Para versões do Android Oreo (API 26) ou superiores, criar um canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        // Construir a notificação para o serviço em primeiro plano
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Serviço de Rastreamento")
                .setContentText("Monitorando desbloqueios")
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Certifique-se de que o ícone esteja disponível
                .build();

        // Iniciar o serviço em primeiro plano com a notificação
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unlockReceiver != null) {
            unregisterReceiver(unlockReceiver);
        }
        isServiceRunning = false;  // Resetar a variável de controle quando o serviço for destruído
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
