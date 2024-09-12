package com.pleasanttours.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Ação após o reboot
            Toast.makeText(context, "Dispositivo reiniciado", Toast.LENGTH_SHORT).show();

            // Iniciar o serviço de monitoramento ou qualquer outro serviço necessário
            Intent serviceIntent = new Intent(context, UnlockService.class); // Substitua "YourService" pelo serviço que deseja iniciar
            context.startService(serviceIntent);
        }
    }
}
