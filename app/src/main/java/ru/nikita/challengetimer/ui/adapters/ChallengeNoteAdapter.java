package ru.nikita.challengetimer.ui.adapters;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.nikita.challengetimer.data.MarathonManager;
import ru.nikita.challengetimer.data.database.Note;
import ru.nikita.challengetimer.databinding.ItemNoteBinding;
import ru.nikita.challengetimer.common.PartOfDay;

public class ChallengeNoteAdapter extends RecyclerView.Adapter<ChallengeNoteAdapter.NoteVH> {

    private List<Note> notes = new ArrayList<>();
    private int expandedPosition = -1;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());

    public void setNotes(List<Note> newNotes) {
        this.notes = newNotes != null ? newNotes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteVH holder, int position) {
        Note note = notes.get(position);

        boolean isExpanded = (position == expandedPosition);

        // 🔑 Явно задаём ВСЕ поля при КАЖДОМ биндинге (защита от рециклинга)
        holder.binding.tvDate.setText(sdf.format(note.date));
        holder.binding.tvDayNum.setText("День " + calculateDayNumber(holder.binding.getRoot().getContext()));
        holder.binding.ivExpand.setRotation(isExpanded ? 180 : 0);

        // 🔑 Критично: явно задаём видимость контента
        holder.binding.expandedContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // 🔑 Сбрасываем стили карточки
        if (isExpanded) {
            holder.binding.noteCard.setCardBackgroundColor(0xFFE3F2FD);
            holder.binding.noteCard.setStrokeColor(0xFF2196F3);

            // Заполняем данные только если раскрыто (оптимизация)
            holder.binding.tvDescription.setText(note.description);
            holder.binding.tvDayOfWeek.setText(note.day.getDisplayName(TextStyle.FULL, Locale.getDefault()));
            holder.binding.tvPartOfDay.setText(getPartOfDayString(note.partOfDay));
            holder.binding.rbState.setRating(note.state);
            holder.binding.rbWish.setRating(note.wish);
        } else {
            holder.binding.noteCard.setCardBackgroundColor(0xFFFFFFFF);
            holder.binding.noteCard.setStrokeColor(0xFFEEEEEE);
        }

        holder.binding.noteCard.setChecked(isExpanded);

        if (note.marathonDays > 0) {
            holder.binding.tvMarathon.setText("Марафон: " + note.marathonDays + " дн.");
            holder.binding.tvMarathon.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvMarathon.setVisibility(View.GONE);
        }

        // 🔑 Обработчик клика
        holder.itemView.setOnClickListener(v -> {
            int oldPos = expandedPosition;
            expandedPosition = (oldPos == position) ? -1 : position;

            TransitionManager.beginDelayedTransition(
                    (ViewGroup) holder.binding.noteRoot.getParent(),
                    new AutoTransition().setDuration(250)
            );

            if (oldPos != -1 && oldPos < notes.size()) notifyItemChanged(oldPos);
            notifyItemChanged(position);
        });
    }

    public static String getPartOfDayString(@NonNull PartOfDay part) {
        switch (part) {
            case MORNING:
                return "Утро";
            case DAY:
                return "День";
            case EVENING:
                return "Вечер";
            case NIGHT:
                return "Ночь";
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    /// Вспомогательный метод для расчёта дня
    private int calculateDayNumber(Context context) {

        long start = MarathonManager.getStartTime(context);
        long now = System.currentTimeMillis();
        long elapsed = Math.max(0, now - start);
        long d = elapsed / (24 * 60 * 60 * 1000);
        return (int) d + 1;
    }

    static class NoteVH extends RecyclerView.ViewHolder {
        final ItemNoteBinding binding;

        NoteVH(@NonNull ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}