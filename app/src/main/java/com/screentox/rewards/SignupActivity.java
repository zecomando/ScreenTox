package com.screentox.rewards;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.screentox.rewards.api.ApiService;
import com.screentox.rewards.api.RetrofitClient;
import com.screentox.rewards.model.User;
import com.screentox.rewards.model.UserSignUpResponse;
import com.screentox.rewards.sharedpreferences.SessionManager;

import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private ApiService apiService;
    private SessionManager sessionManager; // Adicionado
    private EditText nameInput, usernameInput, emailInput, passwordInput, invitedByInput;
    private Button signupButton;
    private String deviceInfo, timezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        sessionManager = new SessionManager(this); // Inicializado

        // Initializing the input fields
        nameInput = findViewById(R.id.nameInput);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);

        // Obter ANDROID_ID e fuso horário
        deviceInfo = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        timezone = TimeZone.getDefault().getID();

        // Setting click listener on the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {
        // Gathering the input data
        String name = nameInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Check if any field is empty
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Additional validation for email format can be added here
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Definir pontos de balance e status como valores padrão
        float balancePoints = 0;
        int status = 1;

        // Creating the user object
        User newUser = new User();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setBalancePoints(Float.parseFloat(String.valueOf(balancePoints)));
        newUser.setDeviceInfo(deviceInfo);  // Usando ANDROID_ID como identificador
        newUser.setStatus(status);
        newUser.setTimezone(timezone);

        // Sending the user data to the server
        Call<UserSignUpResponse> call = apiService.createUser(newUser);
        call.enqueue(new Callback<UserSignUpResponse>() {
            @Override
            public void onResponse(Call<UserSignUpResponse> call, Response<UserSignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserSignUpResponse userSignUpResponse = response.body();
                    int userId = userSignUpResponse.getUserId();

                    // Debugar a resposta no log
                    Log.d("SignupActivity", "User created successfully: " + userSignUpResponse.toString());

                    // Atualiza o estado de login e salva o ID do usuário na sessão
                    sessionManager.setLogin(true);
                    sessionManager.setUserID(userId);

                    // Exibe uma mensagem de sucesso
                    Toast.makeText(SignupActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();

                    // Redireciona para a MainMenuActivity
                    Intent intent = new Intent(SignupActivity.this, MainMenuActivity.class);
                    startActivity(intent);

                    // Opcional: fecha a activity atual
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSignUpResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
