package com.screentox.rewards.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class UnlockJobService extends JobService {

    private static final String TAG = "UnlockJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        // Código para executar a tarefa agendada
        Log.d(TAG, "Job iniciado");

        // Retornar false se a tarefa for rápida, ou true se estiver em execução em uma thread separada
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job parado");
        return true; // Retornar true para reagendar o trabalho se ele for interrompido
    }
}
