package ru.nikita.challengetimer.screen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.nikita.challengetimer.R;

public class MainActivity extends AppCompatActivity {
    private Fragment timerFragment, rewardsFragment, notesFragment, infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        /// Показываем таймер по умолчанию
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_timer);
        }
    }
}