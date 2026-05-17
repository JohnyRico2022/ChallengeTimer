package ru.nikita.challengetimer.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.DayOfWeek;

import ru.nikita.challengetimer.data.MarathonManager;
import ru.nikita.challengetimer.data.database.AppDatabase;
import ru.nikita.challengetimer.data.database.Note;
import ru.nikita.challengetimer.databinding.DialogAddNoteBinding;
import ru.nikita.challengetimer.common.PartOfDay;

public class AddNoteDialog extends DialogFragment {

    public static String TAG = "MyLog";

    public interface OnNoteSavedListener {
        void onNoteSaved();
    }

    private OnNoteSavedListener listener;

    public void setOnNoteSavedListener(OnNoteSavedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogAddNoteBinding binding = DialogAddNoteBinding.inflate(LayoutInflater.from(requireContext()));
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
        dialog.setCanceledOnTouchOutside(false);

        setupAutoInfo(binding);

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        binding.btnSave.setOnClickListener(v -> {
            String desc = binding.etDesc.getText().toString().trim();

            // Стало (округление + защита от 0):
            int state = Math.round(binding.rbState1.getRating());
            int wish = Math.round(binding.rbWish1.getRating());

            // Опционально: если хочешь требовать минимум 1 звезду:
            if (state == 0) state = 1;
            if (wish == 0) wish = 1;

            int currentMarathon = MarathonManager.getActiveDays(requireContext());

            // 🔌 Сохранение в Room (фоновый поток)
            ExecutorService executor = Executors.newSingleThreadExecutor();
            int finalState = state;
            int finalWish = wish;
            executor.execute(() -> {
                Calendar cal = Calendar.getInstance();
                Note note = new Note("", finalState, finalWish, desc,
                        cal.getTimeInMillis(),
                        getDayOfWeek(cal),
                        getPartOfDay(cal),
                        currentMarathon
                );

                AppDatabase.getInstance(requireContext()).noteDao().insert(note);

                requireActivity().runOnUiThread(() -> {
                    if (listener != null)
                        listener.onNoteSaved();
                    dialog.dismiss();
                });
                executor.shutdown();
            });
        });

        return dialog;
    }

    private void setupAutoInfo(@NonNull DialogAddNoteBinding binding) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String info = String.format(Locale.getDefault(), "📅 %s  🕒 %s  🌤 %s",
                sdfDate.format(cal.getTime()),
                sdfTime.format(cal.getTime()),
                getPartOfDayString(cal)
        );
        binding.tvAutoInfo.setText(info);
    }

    private DayOfWeek getDayOfWeek(@NonNull Calendar cal) {
        /// Calendar.SUNDAY=1 -> java.time.SUNDAY=7. Корректируем под ISO.
        int calDay = cal.get(Calendar.DAY_OF_WEEK);
        int isoDay = ((calDay + 5) % 7) + 1;
        return DayOfWeek.of(isoDay);
    }

    private PartOfDay getPartOfDay(@NonNull Calendar cal) {
        int h = cal.get(Calendar.HOUR_OF_DAY);
        if (h >= 5 && h < 12) return PartOfDay.MORNING;
        if (h >= 12 && h < 18) return PartOfDay.DAY;
        if (h >= 18 && h < 23) return PartOfDay.EVENING;
        return PartOfDay.NIGHT;
    }

    @NonNull
    private String getPartOfDayString(Calendar cal) {
        PartOfDay p = getPartOfDay(cal);
        switch (p) {
            case MORNING:
                return "Утро";
            case DAY:
                return "День";
            case EVENING:
                return "Вечер";
            case NIGHT:
                return "Ночь";
        }
        return "";
    }
}