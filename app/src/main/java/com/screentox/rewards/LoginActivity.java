package com.screentox.rewards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.screentox.rewards.api.ApiRepository;
import com.screentox.rewards.model.LoginResponse;
import com.screentox.rewards.model.User;
import com.screentox.rewards.sharedpreferences.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private SessionManager sessionManager;
    private ApiRepository apiRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar os campos
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        sessionManager = new SessionManager(this);
        apiRepository = new ApiRepository();

        // Configurar o evento de clique no botão de login
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Configurar o link para a SignupActivity
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        User loginUser = new User();
        loginUser.setEmail(email);
        loginUser.setPassword(password);

        apiRepository.loginUser(loginUser, new ApiRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                sessionManager.setUserID(loginResponse.getUserId());
                sessionManager.setLogin(true);

                Log.d("LoginActivity", "User ID armazenado: " + loginResponse.getUserId());

                // Redireciona para a próxima atividade
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
