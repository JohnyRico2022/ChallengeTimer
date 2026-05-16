package ru.nikita.challengetimer.adapters;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.common.ChallengeStart;
import ru.nikita.challengetimer.common.MarathonManager;
import ru.nikita.challengetimer.databinding.ItemRewardsBinding;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {
    private final int[] ALL_MARATHONS = {1, 3, 5, 7, 10, 14};
    private final String[] SUBTITLES = {
            "Первый шаг", "Неделя силы", "Полумесяц", "Две недели",
            "Декада привычки", "Путь мастера"
    };

    @NonNull @Override
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
        holder.binding.tvGoal.setText(ChallengeStart.formatDays(days));
        holder.binding.tvSubtitle.setText(SUBTITLES[position]);
        holder.binding.cardRoot.setSelected(done);

        // 🔹 Иконка статуса: галочка или замок
        holder.binding.ivStatus.setImageResource(
                done ? R.drawable.ic_check_circle : R.drawable.ic_lock_outline);
        holder.binding.ivStatus.setColorFilter(
                done ? Color.parseColor("#4CAF50") : Color.parseColor("#E0E0E0"));

        // 🔹 Бейдж "Новое" (если прошло недавно)
        boolean isNew = done && (System.currentTimeMillis() - getLastCompletionTime(holder.binding.getRoot().getContext(), days) < 24 * 60 * 60 * 1000);
        holder.binding.tvBadge.setVisibility(isNew ? View.VISIBLE : View.GONE);

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
    }

    @Override public int getItemCount() { return ALL_MARATHONS.length; }

    // Простая заглушка для времени прохождения (в реальном проекте — из БД)
    private long getLastCompletionTime(android.content.Context ctx, int days) {
        return MarathonManager.getStartTime(ctx); // Упрощённо
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemRewardsBinding binding;
        boolean isAnimated = false;

        ViewHolder(ItemRewardsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}