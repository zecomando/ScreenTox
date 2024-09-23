package com.screentox.rewards.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.screentox.rewards.service.UnlockService;
import com.screentox.rewards.utils.AppLovinManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "Dispositivo reiniciado", Toast.LENGTH_SHORT).show();

            // Iniciar o UnlockService
            Intent serviceIntent = new Intent(context, UnlockService.class);
            if (!isServiceRunning(context, UnlockService.class)) {
                context.startService(serviceIntent);
            }


        }
    }

    private boolean isServiceRunning(Context context, Class<?> serviceClass) {
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
