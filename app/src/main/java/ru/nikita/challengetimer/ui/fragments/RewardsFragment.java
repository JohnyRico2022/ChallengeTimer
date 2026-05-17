package ru.nikita.challengetimer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ru.nikita.challengetimer.ui.adapters.RewardAdapter;
import ru.nikita.challengetimer.data.MarathonManager;
import ru.nikita.challengetimer.databinding.FragmentRewardsBinding;

public class RewardsFragment extends Fragment {
    private FragmentRewardsBinding binding;
    private RewardAdapter adapter;
    private final int[] ALL_MARATHONS = {1, 3, 5, 7, 10, 14};

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvRewards.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RewardAdapter();
        binding.rvRewards.setAdapter(adapter);

        updateProgress();
    }

    private void updateProgress() {
        int completed = 0;
        for (int days : ALL_MARATHONS) {
            if (MarathonManager.isCompleted(requireContext(), days)) completed++;
        }
        binding.tvProgress.setText(completed + " из " + ALL_MARATHONS.length + " пройдено");
        binding.tvPercent.setText((completed * 100 / ALL_MARATHONS.length) + "%");
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        updateProgress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}