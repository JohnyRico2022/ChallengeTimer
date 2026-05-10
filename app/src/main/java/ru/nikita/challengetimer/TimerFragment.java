package ru.nikita.challengetimer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;


public class TimerFragment extends Fragment {


    private CircularTimerView timerView;
    private TextView tvPassedValue;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable tickRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timerView = view.findViewById(R.id.timerView);
        tvPassedValue = view.findViewById(R.id.tvPassedValue);

        tickRunnable = new Runnable() {
            @Override
            public void run() {
                timerView.tick();
                updateElapsedText();
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(tickRunnable, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(tickRunnable);
    }

    private void updateElapsedText() {
        long start = ChallengeStart.getHardcodedStart();
        long now = System.currentTimeMillis();
        long elapsed = Math.max(0, now - start);


        long d = elapsed / (24 * 60 * 60 * 1000);
        long h = (elapsed % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long m = (elapsed % (60 * 60 * 1000)) / (60 * 1000);
        long s = (elapsed % (60 * 1000)) / 1000;
        tvPassedValue.setText(String.format(Locale.getDefault(), "%d дн. %02d:%02d:%02d", d, h, m, s));
    }

}