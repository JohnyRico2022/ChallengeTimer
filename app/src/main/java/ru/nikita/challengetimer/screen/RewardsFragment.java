package ru.nikita.challengetimer.screen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.adapters.RewardAdapter;


public class RewardsFragment extends Fragment {

    private RecyclerView rv;
    private RewardAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable = new Runnable() {
        @Override public void run() {
            adapter.notifyDataSetChanged();
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = view.findViewById(R.id.rvRewards);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RewardAdapter();
        rv.setAdapter(adapter);
    }

    @Override public void onResume() {
        super.onResume();
        handler.post(refreshRunnable);
    }
    @Override public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }
}