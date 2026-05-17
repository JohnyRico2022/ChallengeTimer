package ru.nikita.challengetimer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ru.nikita.challengetimer.data.MarathonManager;
import ru.nikita.challengetimer.ui.customView.CircularTimerView;
import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.ui.dialogs.AbortMarathonDialog;
import ru.nikita.challengetimer.ui.activities.WelcomeActivity;

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

        Button abortMarathon = view.findViewById(R.id.button_abort);
        abortMarathon.setOnClickListener(view1 -> showAbortDialog());
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
        long start = MarathonManager.getStartTime(this.requireContext());
        long now = System.currentTimeMillis();
        long elapsed = Math.max(0, now - start);

        long d = elapsed / (24 * 60 * 60 * 1000);
        long h = (elapsed % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long m = (elapsed % (60 * 60 * 1000)) / (60 * 1000);
        long s = (elapsed % (60 * 1000)) / 1000;

        String dateText = ((int) d == 0)
                ? String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
                : String.format(Locale.getDefault(), "%d дн. %02d:%02d:%02d", d, h, m, s);

        tvPassedValue.setText(dateText);
    }

    private void showAbortDialog() {
        AbortMarathonDialog dialog = new AbortMarathonDialog();
        dialog.setListener(new AbortMarathonDialog.AbortDialogListener() {
            @Override
            public void onRestartMarathon() {
                int days = MarathonManager.getActiveDays(requireContext());
                MarathonManager.startMarathon(requireContext(), days);
                /// Обновляем таймер без пересоздания фрагмента
                timerView.resetStartDate(requireContext(), System.currentTimeMillis());
                Toast.makeText(requireContext(), "🔄 Марафон начат заново", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResetMarathon() {
                int days = MarathonManager.getActiveDays(requireContext());
                MarathonManager.resetMarathonCompletely(requireContext(), days);
                Toast.makeText(requireContext(), "🗑 Марафон сброшен", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), WelcomeActivity.class));
                requireActivity().finish();
            }
        });
        dialog.show(getChildFragmentManager(), "AbortDialog");
    }
}