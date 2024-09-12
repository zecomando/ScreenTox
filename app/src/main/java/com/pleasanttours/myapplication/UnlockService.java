package com.pleasanttours.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.pleasanttours.myapplication.UnlockReceiver;

public class UnlockService extends Service {

    private static int unlockCount = 0;  // Variável para armazenar a contagem de desbloqueios
    private UnlockReceiver unlockReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UnlockService", "Serviço iniciado");

        // Iniciar o serviço em primeiro plano com uma notificação
        startForegroundServiceWithNotification();

        unlockReceiver = new UnlockReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, filter);
        Log.d("UnlockService", "UnlockReceiver registrado");
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

    // Método para aumentar a contagem de desbloqueios
    public static void incrementUnlockCount() {
        unlockCount++;
    }

    // Método para obter a contagem de desbloqueios
    public static int getUnlockCount() {
        return unlockCount;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unlockReceiver != null) {
            unregisterReceiver(unlockReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
