package com.screentox.rewards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.screentox.rewards.api.ApiService;
import com.screentox.rewards.api.RetrofitClient;
import com.screentox.rewards.model.User;
import com.screentox.rewards.sharedpreferences.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class UserActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, balancePointsTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Inicializar views
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        balancePointsTextView = findViewById(R.id.balancePointsTextView);
        logoutButton = findViewById(R.id.logoutButton);

        // Obter dados do usuário da sessão
        SessionManager sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserID();

        // Buscar os dados do usuário
        fetchUserDetails(userId);

        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();

            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // A chamada a finish() não é mais necessária com as flags acima, mas pode ser adicionada para garantir que a atividade atual seja encerrada.
            finish();
        });

    }

    private void fetchUserDetails(int userId) {
        Log.d("UserActivity", "Criando instância do serviço Retrofit. UserID: " + userId);
        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Log.d("UserActivity", "Chamando API para obter dados do usuário. Endpoint: /obter_dados_utilizador.php?id=" + userId);
        Call<User> call = service.getUserDetails(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("UserActivity", "Resposta recebida do servidor. Código: " + response.code());

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        Log.d("UserActivity", "Dados recebidos: " + user.toString());

                        // Exibir os dados do usuário
                        nameTextView.setText("Nome: " + user.getName());
                        emailTextView.setText("Email: " + user.getEmail());
                        balancePointsTextView.setText("Pontos: " + user.getBalancePoints());
                    } else {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "N/A";
                        Log.e("UserActivity", "Erro ao obter dados: Código " + response.code() + ", Mensagem: " + response.message() + ", Corpo do erro: " + errorBody);
                        Toast.makeText(UserActivity.this, "Erro ao obter dados do usuário", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("UserActivity", "Erro ao ler o corpo da resposta: " + e.getMessage(), e);
                    Toast.makeText(UserActivity.this, "Erro ao processar a resposta do servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserActivity", "Falha na chamada à API. Detalhes: " + t.getMessage(), t);
                Toast.makeText(UserActivity.this, "Erro ao obter dados do usuário: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
