package ru.nikita.challengetimer.ui.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.databinding.ActivitySosBinding;

public class RelaxActivity extends AppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private MediaPlayer mediaPlayer;
    private int currentPhraseIndex = 0;
    private Runnable phraseRunnable;

    private ActivitySosBinding binding;

    private static final String[] PHRASES = {
            "Дыши спокойно и глубоко. Ты в безопасности.",
            "Это чувство временно. Оно пройдёт.",
            "Ты сильнее, чем думаешь.",
            "Сосредоточься на том, что можешь контролировать.",
            "Вокруг тишина и спокойствие."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonOk.setOnClickListener(v -> finish());
        binding.buttonRepeat.setOnClickListener(v -> startSession());

        startSession();
    }

    private void startSession() {
        currentPhraseIndex = 0;
        binding.llButtons.setVisibility(View.GONE);
        binding.llButtons.setAlpha(0f);
        binding.message.setVisibility(View.VISIBLE);
        binding.background.setVisibility(View.VISIBLE);
        binding.message.setAlpha(1f);
        binding.background.setAlpha(1f);
        binding.message.setText(PHRASES[0]);

        // Звук
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.forest_sound);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.2f, 0.2f);
        }
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

        // Таймер фраз
        phraseRunnable = new Runnable() {
            @Override
            public void run() {
                currentPhraseIndex++;
                if (currentPhraseIndex < PHRASES.length) {
                    updatePhrase();
                    handler.postDelayed(this, 8_000);
                } else
                    showButtons();
            }
        };

        handler.postDelayed(phraseRunnable, 6_000);
    }

    private void updatePhrase() {
        binding.message.animate().alpha(0f).setDuration(800).withEndAction(() -> {
            binding.message.setText(PHRASES[currentPhraseIndex]);
            binding.message.animate().alpha(1f).setDuration(800).start();
        }).start();
    }

    private void showButtons() {
        handler.removeCallbacksAndMessages(null);
        binding.message.animate().alpha(0f).setDuration(800).withEndAction(() -> binding.message.setVisibility(View.GONE)).start();
        binding.background.animate().alpha(0f).setDuration(800).withEndAction(() -> binding.message.setVisibility(View.GONE)).start();

        binding.llButtons.setVisibility(View.VISIBLE);
        binding.llButtons.animate().alpha(1f).setDuration(800).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}