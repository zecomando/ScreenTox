package com.screentox.rewards.api;

import com.screentox.rewards.model.LoginResponse;
import com.screentox.rewards.model.Poll;
import com.screentox.rewards.model.UnlockDevice;
import com.screentox.rewards.model.UnlockScreen;
import com.screentox.rewards.model.User;
import com.screentox.rewards.model.UserSignUpResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @GET("obter_dados_utilizador.php")
    Call<User> getUserDetails(@Query("id") int userId);

    @GET("obter_utilizadores.php")
    Call<List<User>> getUsers();

    @POST("criar_utilizador.php")
    Call<UserSignUpResponse> createUser(@Body User user);

    @PUT("atualizar_utilizador.php")
    Call<Void> updateUser(@Body User user);

    @DELETE("eliminar_utilizador.php")
    Call<Void> deleteUser(@Body User user);

    @GET("obter_pontos_utilizador.php")
    Call<User> getUser(@Query("id") int userId);

    @POST("adicionar_desbloqueio.php")
    Call<ResponseBody> sendUnlockData(@Body UnlockDevice unlockDevice);

    @POST("adicionar_desbloqueio_ecran.php")
    Call<ResponseBody> sendUnlockScreenData(@Body UnlockScreen unlockScreen);

    @POST("login_utilizador.php")
    Call<LoginResponse> loginUser(@Body User user);

    @GET("obter_sondagens.php")
    Call<Poll> getRandomPoll();

}


