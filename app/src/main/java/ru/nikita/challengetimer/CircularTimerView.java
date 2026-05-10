package ru.nikita.challengetimer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularTimerView extends View {

    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();

    private final float strokeWidth = 24f;
    private final float spacing = 12f;
    private final float[] progress = new float[4];
    private final float[] radii = new float[4];

    private long startDateMillis;
    private int currentTargetIndex = 0;

    public CircularTimerView(Context context) {
        this(context, null);
    }

    public CircularTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadStartDate();
    }

    private void init() {
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(strokeWidth);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(0xFF222222);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float cx = w / 2f;
        float cy = h / 2f;
        float maxRadius = Math.min(w, h) / 2f - strokeWidth - 8f;
        float step = strokeWidth + spacing;

        // 0-сек(внутр) -> 3-дни(внешн)
        radii[3] = maxRadius;
        radii[2] = maxRadius - step;
        radii[1] = maxRadius - 2 * step;
        radii[0] = maxRadius - 3 * step;
    }

    public void tick() {
        long now = System.currentTimeMillis();
        long elapsed = Math.max(0, now - startDateMillis);

        // 🔢 Компоненты прошедшего времени
        long seconds = (elapsed / 1000) % 60;
        long minutes = (elapsed / (1000 * 60)) % 60;
        long hours = (elapsed / (1000 * 60 * 60)) % 24;
        long days = elapsed / (24L * 60 * 60 * 1000);

        // 📈 Прогресс внутренних колец (0.0 -> 1.0)
        progress[0] = seconds / 60f; // секунды текущей минуты
        progress[1] = minutes / 60f; // минуты текущего часа
        progress[2] = hours / 24f;   // часы текущего дня (от старта!)

        // 🎯 Динамический поиск ближайшей цели
        int newIdx = 0;
        for (int i = 0; i < ChallengeTarget.ALL.length; i++) {
            if (days < ChallengeTarget.ALL[i].days) {
                newIdx = i;
                break;
            }
            newIdx = i;
        }

        // 🔄 Переключение цели
        if (newIdx != currentTargetIndex) {
            currentTargetIndex = newIdx;
        }

        int targetDays = ChallengeTarget.ALL[currentTargetIndex].days;
        progress[3] = Math.min(1f, days / (float) targetDays);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        // 1. Фоновые дорожки
        ringPaint.setColor(0x1A000000);
        for (float r : radii) {
            oval.set(cx - r, cy - r, cx + r, cy + r);
            canvas.drawArc(oval, -90, 360, false, ringPaint);
        }

        // 2. Цветные дуги
        int[] colors = {0xFFFF5252, 0xFFFF9800, 0xFF4CAF50, 0xFF2196F3};
        for (int i = 0; i < 4; i++) {
            ringPaint.setColor(colors[i]);
            oval.set(cx - radii[i], cy - radii[i], cx + radii[i], cy + radii[i]);
            canvas.drawArc(oval, -90, progress[i] * 360f, false, ringPaint);
        }

        // 3. Текст цели в центре
        String goalText = ChallengeTarget.ALL[currentTargetIndex].days + " дн.";
        float textSize = getWidth() * 0.10f;
        textPaint.setTextSize(textSize);
        float baseline = cy - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(goalText, cx, baseline + 50, textPaint);

        canvas.drawText("Цель:", cx, baseline - 50, textPaint);


    }

    public long getStartDateMillis() {
        return startDateMillis;
    }

    private static final String PREFS_NAME = "challenge_prefs";
    private static final String KEY_START_DATE = "start_date";

    private void saveStartDate() {
        getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putLong(KEY_START_DATE, startDateMillis).apply();
    }

    // Вызывать из Activity/Fragment для смены даты
    public void resetStartDate(Context context, long newMillis) {
        this.startDateMillis = newMillis;
        this.currentTargetIndex = 0; // Сбрасываем цели к началу
        saveStartDate();
        tick(); // Перерисовка
    }

    private void loadStartDate() {
        startDateMillis = ChallengeStart.getHardcodedStart();
    }
}