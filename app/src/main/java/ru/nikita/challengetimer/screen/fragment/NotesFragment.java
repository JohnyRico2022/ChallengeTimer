package ru.nikita.challengetimer.screen.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.Executors;

import ru.nikita.challengetimer.database.AppDatabase;
import ru.nikita.challengetimer.database.Note;
import ru.nikita.challengetimer.databinding.FragmentNotesBinding;
import ru.nikita.challengetimer.note.AddNoteDialog;
import ru.nikita.challengetimer.note.ChallengeNoteAdapter;

public class NotesFragment extends Fragment {

    FragmentNotesBinding binding;
    private boolean isLoading = false;
    ChallengeNoteAdapter adapter;

    public NotesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(getLayoutInflater(), container, false);

        binding.addNote.setOnClickListener(View -> openAddNoteDialog());

        // В твоем фрагменте/активности
        //RecyclerView rv = findViewById(R.id.rvNotes);
        binding.noteRecycler.setLayoutManager(new LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.noteRecycler.setClipToPadding(false);
        binding.noteRecycler.setPadding(16, 8, 16, 8); // отступы по краям

        adapter = new ChallengeNoteAdapter();
        binding.noteRecycler.setAdapter(adapter);

        return binding.getRoot();
    }

    private void openAddNoteDialog() {
        AddNoteDialog dialog = new AddNoteDialog();
        dialog.setOnNoteSavedListener(() -> loadNotesFromDb()); // ← Перезагрузка после сохранения
        dialog.show(getChildFragmentManager(), "AddNoteDialog");
    }

    // 2. Загрузка из Room
    private void loadNotesFromDb() {
        if (isLoading) return; // ← Защита от повторного вызова
        isLoading = true;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Note> dbNotes = AppDatabase.getInstance(requireContext())
                        .noteDao()
                        .getAllNotes();

                requireActivity().runOnUiThread(() -> {
                    adapter.setNotes(dbNotes);
                    isLoading = false; // ← Сброс флага ТОЛЬКО в UI-потоке
                });
            } catch (Exception e) {
                isLoading = false;
                e.printStackTrace();
            }
        });
    }


    // Вызов при старте
    @Override
    public void onResume() {
        super.onResume();
        loadNotesFromDb();
    }


}