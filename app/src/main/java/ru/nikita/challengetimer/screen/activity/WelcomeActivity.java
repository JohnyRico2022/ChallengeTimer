package ru.nikita.challengetimer.screen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikita.challengetimer.common.MarathonManager;
import ru.nikita.challengetimer.databinding.ActivityWelcomeBinding;
import ru.nikita.challengetimer.databinding.ItemMarathonCardBinding;
import ru.nikita.challengetimer.screen.MarathonSelectDialog;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private final int[] DAYS = {1, 3, 5, 7, 10, 14};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Если марафон уже активен -> сразу в MainActivity
        if (MarathonManager.hasActive(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding.rvMarathons.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvMarathons.setAdapter(new MarathonAdapter());
    }

    private void showStartDialog(int days) {
        MarathonSelectDialog dialog = MarathonSelectDialog.newInstance(days);
        dialog.setListener(new MarathonSelectDialog.MarathonDialogListener() {
            @Override public void onMarathonStarted(int selectedDays) {
                MarathonManager.startMarathon(WelcomeActivity.this, selectedDays);
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish(); // Закрываем экран выбора
            }
        });
        dialog.show(getSupportFragmentManager(), "MarathonDialog");
    }

    private class MarathonAdapter extends RecyclerView.Adapter<MarathonAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new VH(ItemMarathonCardBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            int days = DAYS[pos];
            boolean completed = MarathonManager.isCompleted(WelcomeActivity.this, days);
            h.binding.tvDays.setText(String.valueOf(days));
            h.binding.tvStatus.setText(completed ? "✅ Пройден" : "Начать");
            h.binding.cardRoot.setSelected(completed);
            h.itemView.setOnClickListener(v -> showStartDialog(days));
          /*  h.itemView.setOnClickListener(v -> {
                if (completed) return; // Уже пройден
                new AlertDialog.Builder(WelcomeActivity.this)
                        .setTitle("Начать марафон?")
                        .setMessage("Ты уверен, что хочешь начать челлендж на " + days + " дней?")
                        .setPositiveButton("Да", (d, w) -> {
                            MarathonManager.startMarathon(WelcomeActivity.this, days);
                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                            finish();
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            });*/
        }

        @Override
        public int getItemCount() {
            return DAYS.length;
        }

        class VH extends RecyclerView.ViewHolder {
            ItemMarathonCardBinding binding;

            VH(ItemMarathonCardBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}