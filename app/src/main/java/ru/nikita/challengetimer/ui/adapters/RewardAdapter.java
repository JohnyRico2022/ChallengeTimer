package ru.nikita.challengetimer.ui.adapters;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.data.MarathonManager;
import ru.nikita.challengetimer.databinding.ItemRewardsBinding;
import ru.nikita.challengetimer.utils.Utils;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {
    private final int[] ALL_MARATHONS = {1, 3, 5, 7, 10, 14};
    private final String[] SUBTITLES = {
            "Первый шаг", "Неделя силы", "Полумесяц", "Две недели",
            "Декада привычки", "Путь мастера"
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRewardsBinding binding = ItemRewardsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int days = ALL_MARATHONS[position];
        boolean done = MarathonManager.isCompleted(holder.binding.getRoot().getContext(), days);

        // 🔹 Базовые данные
        holder.binding.tvGoal.setText(Utils.formatDays(days));
        holder.binding.tvSubtitle.setText(SUBTITLES[position]);
        holder.binding.cardRoot.setSelected(done);

        // 🔹 Иконка статуса: галочка или замок
        holder.binding.ivStatus.setImageResource(
                done ? R.drawable.ic_check_circle : R.drawable.ic_lock_outline);
        holder.binding.ivStatus.setColorFilter(
                done ? Color.parseColor("#4CAF50") : Color.parseColor("#E0E0E0"));

        // 🔹 Анимация появления при прокрутке
        if (!holder.isAnimated) {
            holder.binding.cardRoot.setAlpha(0f);
            holder.binding.cardRoot.setTranslationY(20f);
            ObjectAnimator.ofFloat(holder.binding.cardRoot, "alpha", 0f, 1f)
                    .setDuration(300)
                    .start();
            ObjectAnimator.ofFloat(holder.binding.cardRoot, "translationY", 20f, 0f)
                    .setDuration(300)
                    .start();
            holder.isAnimated = true;
        }

        // 🔹 Клик (опционально: показать детали)
        holder.binding.cardRoot.setOnClickListener(v -> {
            // Можно добавить диалог с деталями марафона
        });

        /// Показываем попытку, если марафон пройден
        if (done) {
            int attempts = MarathonManager.getAttemptCount(holder.binding.getRoot().getContext(), days);
            String attemptSuffix = attempts > 1 ? " (с #" + attempts + " попытки)" : " (с первой)";
            holder.binding.tvSubtitle.setText(holder.binding.tvSubtitle.getText() + attemptSuffix);
        }
    }

    @Override
    public int getItemCount() {
        return ALL_MARATHONS.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemRewardsBinding binding;
        boolean isAnimated = false;

        ViewHolder(@NonNull ItemRewardsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}