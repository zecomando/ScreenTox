package com.screentox.rewards;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.screentox.rewards.api.ApiService;
import com.screentox.rewards.api.RetrofitClient;
import com.screentox.rewards.model.Poll;
import com.screentox.rewards.model.UnlockScreen;
import com.screentox.rewards.sharedpreferences.SessionManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OverlayScreenActivity extends Activity implements MaxAdViewAdListener {

    private static final String TAG = "OverlayScreenActivity";
    private SeekBar slider;
    private ImageView arrowLeft, arrowRight;
    private MaxAdView adViewMrec, adViewBanner;
    private Poll currentPoll;
    private boolean hasSentUnlockData = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay_screen);

        // Configurar anúncios
        adViewMrec = findViewById(R.id.ad_view_mrec);
        adViewMrec.setListener(this);
        adViewMrec.loadAd();

        adViewBanner = findViewById(R.id.ad_view_banner);
        adViewBanner.setListener(this);
        adViewBanner.loadAd();

        slider = findViewById(R.id.slider);
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);

        fetchRandomPoll(); // Busca um poll aleatório

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 90 && !hasSentUnlockData) {
                    sendUnlockScreenData("Unlock Screen", "Ad");
                    hasSentUnlockData = true;
                    finish();
                }
                // Atualizar a visibilidade das setas
                arrowLeft.setVisibility(progress < 10 ? View.VISIBLE : View.INVISIBLE);
                arrowRight.setVisibility(progress > 90 ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Slider tracking started.");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() <= 90) {
                    seekBar.setProgress(0);
                    hasSentUnlockData = false;
                }
                Log.d(TAG, "Slider tracking stopped.");
            }
        });
    }

    private void fetchRandomPoll() {
        ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Poll> call = service.getRandomPoll();

        call.enqueue(new Callback<Poll>() {
            @Override
            public void onResponse(Call<Poll> call, Response<Poll> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPoll = response.body();
                    displayPoll(currentPoll);
                } else {
                    Log.e(TAG, "Erro ao obter poll: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Poll> call, Throwable t) {
                Log.e(TAG, "Erro ao buscar poll: " + t.getMessage());
            }
        });
    }

    private void displayPoll(Poll poll) {
        TextView pollTitle = findViewById(R.id.poll_title);
        TextView pollDescription = findViewById(R.id.poll_description);

        pollTitle.setText(poll.getTitle());
        pollDescription.setText(poll.getDescription());

        // Aqui você pode adicionar lógica para exibir as opções do poll, se necessário
    }

    private void sendUnlockScreenData(String action, String type) {
        Log.d(TAG, "Tentando enviar dados de ação: " + action + ", tipo: " + type);
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserID();
            if (userId > 0) {
                UnlockScreen unlockScreen = new UnlockScreen(userId, type, action);
                ApiService service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                Call<ResponseBody> call = service.sendUnlockScreenData(unlockScreen);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Dados enviados com sucesso: ação - " + action + ", tipo - " + type);
                        } else {
                            Log.e(TAG, "Erro no envio de dados: código " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Erro ao enviar dados: " + t.getMessage());
                    }
                });
            } else {
                Log.d(TAG, "Usuário não está logado.");
            }
        }
    }

    @Override
    public void onAdLoaded(MaxAd maxAd) {
        Log.d(TAG, "Ad loaded: " + maxAd.getAdUnitId());
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {
        Log.d(TAG, "Ad displayed: " + maxAd.getAdUnitId());
    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {
        Log.d(TAG, "Ad hidden: " + maxAd.getAdUnitId());
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Log.d(TAG, "Failed to load ad: " + adUnitId + " with error: " + error.getMessage());
    }

    @Override
    public void onAdDisplayFailed(MaxAd maxAd, MaxError error) {
        Log.d(TAG, "Failed to display ad: " + maxAd.getAdUnitId() + " with error: " + error.getMessage());
    }

    @Override
    public void onAdClicked(MaxAd maxAd) {
        Log.d(TAG, "Ad clicked: " + maxAd.getAdUnitId());
    }

    @Override
    public void onAdExpanded(MaxAd maxAd) {
        Log.d(TAG, "Ad expanded: " + maxAd.getAdUnitId());
    }

    @Override
    public void onAdCollapsed(MaxAd maxAd) {
        Log.d(TAG, "Ad collapsed: " + maxAd.getAdUnitId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        sendUnlockScreenData("No Action", "Ad");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Activity stopped");
    }
}
