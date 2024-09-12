package com.pleasanttours.myapplication;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView unlocksView, appUsageView, totalUsageView;
    private static final String TAG = "UnlockTracker";
    private UnlockReceiver unlockReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Iniciar o UnlockService
        Intent serviceIntent = new Intent(this, UnlockService.class);
        startService(serviceIntent);
        unlocksView = findViewById(R.id.unlocksView);
        appUsageView = findViewById(R.id.appUsageView);
        totalUsageView = findViewById(R.id.totalUsageView);

        // Exemplo de dados fictícios para envio
        int unlocks = 10;
        long totalUsage = 30000;
        String appUsage = "com.whatsapp: 1000 seg";

        Log.d("MainActivity", "Dados para envio - Desbloqueios: " + unlocks + ", Tempo total de uso: " + totalUsage + ", Uso do app: " + appUsage);

        // Enviar dados usando Retrofit
        sendUsageData(unlocks, totalUsage, appUsage);

        // Obter dados usando Retrofit
        fetchUsageData();
    }

    private void sendUsageData(int unlocks, long totalUsage, String appUsage) {
        UsageData usageData = new UsageData(unlocks, totalUsage, appUsage);

        Log.d("SendUsageData", "Criando instância do serviço Retrofit");
        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("SendUsageData", "Chamando API para envio de dados");

        Call<ResponseBody> call = service.sendUsageData(usageData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("SendUsageData", "Resposta recebida do servidor. Código: " + response.code());

                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("SendUsageData", "Corpo da resposta: " + responseBody);
                        // Aqui você pode usar Gson para converter a resposta JSON em um objeto Java, se necessário
                        // Exemplo: ResponseData responseData = new Gson().fromJson(responseBody, ResponseData.class);
                        Toast.makeText(MainActivity.this, "Dados enviados com sucesso", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("SendUsageData", "Erro ao ler o corpo da resposta: " + e.getMessage(), e);
                        Toast.makeText(MainActivity.this, "Erro ao processar a resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("SendUsageData", "Erro na resposta do servidor: Código " + response.code() + ", Mensagem: " + response.message());
                    Toast.makeText(MainActivity.this, "Erro na resposta do servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SendUsageData", "Erro ao enviar os dados: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Erro ao enviar os dados: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUsageData() {
        Log.d("FetchUsageData", "Criando instância do serviço Retrofit");
        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Log.d("FetchUsageData", "Chamando API para obter dados");

        Call<List<UsageData>> call = service.getUsageData();

        call.enqueue(new Callback<List<UsageData>>() {
            @Override
            public void onResponse(Call<List<UsageData>> call, Response<List<UsageData>> response) {
                Log.d("FetchUsageData", "Resposta recebida do servidor. Código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<UsageData> usageDataList = response.body();
                    Log.d("FetchUsageData", "Dados recebidos: " + new Gson().toJson(usageDataList));

                    // Processar e exibir os dados obtidos
                    // Exemplo de como exibir os dados, você pode ajustar conforme necessário
                    StringBuilder unlocksText = new StringBuilder();
                    StringBuilder appUsageText = new StringBuilder();
                    long totalUsage = 0;

                    for (UsageData data : usageDataList) {
                        unlocksText.append("Desbloqueios: ").append(data.getUnlocks()).append("\n");
                        appUsageText.append(data.getAppUsage()).append("\n");
                        totalUsage += data.getTotalUsage();
                    }

                    unlocksView.setText(unlocksText.toString());
                    appUsageView.setText(appUsageText.toString());
                    totalUsageView.setText("Tempo Total de Uso: " + totalUsage + " seg");
                } else {
                    Log.e("FetchUsageData", "Erro ao obter dados: Código " + response.code() + ", Mensagem: " + response.message());
                    Toast.makeText(MainActivity.this, "Erro ao obter dados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsageData>> call, Throwable t) {
                Log.e("FetchUsageData", "Erro ao obter dados: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Erro ao obter dados: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // if (unlockReceiver != null) {
       //     unregisterReceiver(unlockReceiver);
     //   }
    }
}
