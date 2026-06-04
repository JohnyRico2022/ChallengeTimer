package ru.nikita.challengetimer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.data.MarathonManager;
import ru.nikita.challengetimer.databinding.ActivityWelcomeBinding;
import ru.nikita.challengetimer.databinding.ItemMarathonCardBinding;
import ru.nikita.challengetimer.ui.dialogs.MarathonSelectDialog;
import ru.nikita.challengetimer.utils.Utils;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// Если марафон уже активен -> сразу в MainActivity
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
        dialog.setListener(selectedDays -> {
            MarathonManager.startMarathon(WelcomeActivity.this, selectedDays);
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
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
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            int days = MarathonManager.getChallenges()[pos];
            boolean completed = MarathonManager.isCompleted(WelcomeActivity.this, days);
            String daysFormat = Utils.formatDays(days);
            String status = completed
                    ? holder.binding.getRoot().getContext().getString(R.string.challenge_status_completed)
                    : "";

            holder.binding.daysCount.setText(String.valueOf(days));
            holder.binding.daysFormat.setText(daysFormat);
            holder.binding.status.setText(status);
            holder.binding.cardRoot.setSelected(completed);
            holder.itemView.setOnClickListener(v -> showStartDialog(days));
        }

        @Override
        public int getItemCount() {
            return MarathonManager.getChallenges().length;
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