package ru.nikita.challengetimer.ui.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ru.nikita.challengetimer.databinding.DialogMaraphonConfirmBinding;
import ru.nikita.challengetimer.utils.Utils;

public class MarathonSelectDialog extends DialogFragment {
    private static final String ARG_DAYS = "arg_days";
    private MarathonDialogListener listener;

    public interface MarathonDialogListener {
        void onMarathonStarted(int days);
    }

    public void setListener(MarathonDialogListener listener) {
        this.listener = listener;
    }

    public static MarathonSelectDialog newInstance(int days) {
        MarathonSelectDialog dialog = new MarathonSelectDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_DAYS, days);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Убираем стандартный прямоугольный фон диалога
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Растягиваем диалог на комфортную ширину
        Dialog d = getDialog();
        if (d != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85f);
            d.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        DialogMaraphonConfirmBinding binding = DialogMaraphonConfirmBinding.inflate(inflater, container, false);
        int daysCount = getArguments() != null ? getArguments().getInt(ARG_DAYS) : 1;
        Log.d("MyLog", "daysCount:  " + daysCount + "  ");

        String daysStr = Utils.formatDays(daysCount);
        Log.d("MyLog", "daysStr:  " + daysStr + "  ");

        String text = "Если ты готов посвятить " + daysStr + " челленджу - сосредоточься, выдохни и погнали 💪";
        Log.d("MyLog", "text:  " + text + "  ");


        binding.tvMessage.setText(text);
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnStart.setOnClickListener(v -> {
            if (listener != null) listener.onMarathonStarted(daysCount);
            dismiss();
        });

        return binding.getRoot();
    }
}