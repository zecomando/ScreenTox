package com.screentox.rewards.api;

import android.util.Log;
import com.google.gson.Gson;
import com.screentox.rewards.model.LoginResponse;
import com.screentox.rewards.model.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository {

    private static final String TAG = "ApiRepository";

    public void fetchUserPoints(int userId, final PointsCallback callback) {
        Log.d(TAG, "Chamando API para obter dados do usuário. Endpoint: /getUser?id=" + userId);
        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<User> call = service.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    float balancePoints = user.getBalancePoints();
                    callback.onSuccess(balancePoints);
                    Log.d(TAG, "Dados recebidos: " + new Gson().toJson(user));
                } else {
                    handleErrorResponse(response);
                    callback.onError("Erro ao obter saldo de pontos");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Falha na chamada à API. Detalhes: " + t.getMessage(), t);
                callback.onError("Erro ao obter saldo de pontos: " + t.getMessage());
            }
        });
    }

    public void loginUser(User user, final LoginCallback callback) {
        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<LoginResponse> call = service.loginUser(user);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Credenciais inválidas");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Erro ao fazer login: " + t.getMessage());
            }
        });
    }

    private void handleErrorResponse(Response<User> response) {
        String errorBody = null;
        try {
            errorBody = response.errorBody() != null ? response.errorBody().string() : "N/A";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.e(TAG, "Erro ao obter dados: Código " + response.code() + ", Mensagem: " + response.message() + ", Corpo do erro: " + errorBody);
    }

    public interface PointsCallback {
        void onSuccess(float points);
        void onError(String error);
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse loginResponse);
        void onError(String error);
    }
}
