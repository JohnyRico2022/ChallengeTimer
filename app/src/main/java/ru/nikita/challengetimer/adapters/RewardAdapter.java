package ru.nikita.challengetimer.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikita.challengetimer.common.ChallengeStart;
import ru.nikita.challengetimer.common.ChallengeTarget;
import ru.nikita.challengetimer.R;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {
    private final ChallengeTarget[] goals = ChallengeTarget.ALL;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rewards, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChallengeTarget goal = goals[position];
        long daysElapsed = ChallengeStart.getElapsedDays();
        boolean isReached = daysElapsed >= goal.days;

        holder.tvGoal.setText(ChallengeStart.formatDays(goal.days));
        holder.tvGoal.setTextColor(isReached ? Color.BLACK : 0xFFBDBDBD); // Серый или черный
        holder.ivStar.setColorFilter(isReached ? 0xFFFFC107 : 0xFFE0E0E0); // Желтая или светло-серая звезда
    }

    @Override public int getItemCount() { return goals.length; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStar;
        TextView tvGoal;
        ViewHolder(View v) { super(v); ivStar = v.findViewById(R.id.ivStar); tvGoal = v.findViewById(R.id.tvGoal); }
    }
}