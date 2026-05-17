package ru.nikita.challengetimer.ui.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import ru.nikita.challengetimer.databinding.DialogAdortMarathonBinding;

public class AbortMarathonDialog extends DialogFragment {
    private AbortDialogListener listener;

    public interface AbortDialogListener {
        void onRestartMarathon();
        void onResetMarathon();
    }

    public void setListener(AbortDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Убираем стандартный фон диалога
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ширина 85% экрана
        Dialog d = getDialog();
        if (d != null) {
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.85f);
            d.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        DialogAdortMarathonBinding binding = DialogAdortMarathonBinding.inflate(inflater, container, false);

        binding.btnRestart.setOnClickListener(v -> {
            if (listener != null) listener.onRestartMarathon();
            dismiss();
        });
        binding.btnReset.setOnClickListener(v -> {
            if (listener != null) listener.onResetMarathon();
            dismiss();
        });
        binding.btnCancel.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }
}