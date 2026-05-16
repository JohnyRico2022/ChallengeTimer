package ru.nikita.challengetimer.screen.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.nikita.challengetimer.R;
import ru.nikita.challengetimer.common.MarathonManager;
import ru.nikita.challengetimer.databinding.ActivityMainBinding;
import ru.nikita.challengetimer.screen.fragment.InfoFragment;
import ru.nikita.challengetimer.screen.fragment.NotesFragment;
import ru.nikita.challengetimer.screen.fragment.RewardsFragment;
import ru.nikita.challengetimer.screen.fragment.TimerFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment timerFragment, rewardsFragment, notesFragment, infoFragment;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🚪 Если нет активного марафона -> на экран выбора
        if (!MarathonManager.hasActive(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        timerFragment = new TimerFragment();
        rewardsFragment = new RewardsFragment();
        notesFragment = new NotesFragment();
        infoFragment = new InfoFragment();

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment target;

            if (item.getItemId() == R.id.nav_timer)
                target = timerFragment;
            else if (item.getItemId() == R.id.nav_rewards)
                target = rewardsFragment;
            else if (item.getItemId() == R.id.nav_notes)
                target = notesFragment;
            else
                target = infoFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, target)
                    .commit();
            return true;
        });

        binding.sosFab.setOnClickListener(View -> startActivity(new Intent(this, SosActivity.class)));

        /// Показываем таймер по умолчанию
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_timer);
        }
    }
}