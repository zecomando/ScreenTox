package com.screentox.rewards.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userID"; // Adicionar uma chave para o userID
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Método para salvar o estado de login
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Método para salvar o userID
    public void setUserID(int userID) {
        editor.putInt(KEY_USER_ID, userID);
        editor.apply();
    }

    // Método para obter o userID
    public int getUserID() {
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1); // Retorna -1 se o userID não estiver presente
        // Se o userId for 0 ou -1, considera-se que não há sessão ativa
        return (userId == 0 || userId == -1) ? -1 : userId;
    }


    // Método para verificar se o usuário está logado
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Método para limpar a sessão
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
