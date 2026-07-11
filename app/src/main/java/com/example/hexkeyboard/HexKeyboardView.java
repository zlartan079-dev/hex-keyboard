package com.example.hexkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a honeycomb-style keyboard where letters are arranged in plain
 * alphabetical order (A-Z), row by row, with alternating rows offset
 * horizontally to create the interlocking hex pattern.
 */
public class HexKeyboardView extends View {

    public interface OnKeyListener {
        void onKey(HexKey key);
    }

    // --- Layout definition -------------------------------------------------
    // Each row is a list of tokens. Tokens beginning with '#' are special keys.
    private static final String[][] LETTER_ROWS = {
            {"A", "B", "C", "D", "E", "F"},
            {"G", "H", "I", "J", "K", "L", "M"},
            {"#SHIFT", "N", "O", "P", "#BACK"},
            {"Q", "R", "S", "T", "U", "V"},
            {"W", "X", "Y", "Z"},
            {"#EMOJI", "#SYMBOLS", "#SPACE", "#ENTER"}
    };

    private static final String[][] SYMBOL_ROWS = {
            {"1", "2", "3", "4", "5", "6"},
            {"7", "8", "9", "0", "@", "#"},
            {"(", ")", "-", "_", "+", "#BACK"},
            {"*", "\"", "'", ":", ";", "/"},
            {"!", "?", ",", "."},
            {"#EMOJI", "#ABC", "#SPACE", "#ENTER"}
    };

    private List<HexKey> keys = new ArrayList<>();
    private boolean caps = false;
    private boolean symbolsMode = false;

    private OnKeyListener listener;
    private HexKey pressedKey;

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint = new Paint();

    public HexKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2f);
        strokePaint.setColor(0xFFDADCE0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(0xFF202124);
        bgPaint.setColor(0xFFF1F3F4);
    }

    public void setOnKeyListener(OnKeyListener l) {
        this.listener = l;
    }

    public boolean isCaps() {
        return caps;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutKeys(w, h);
    }

    private void layoutKeys(int w, int h) {
        keys.clear();
        String[][] rows = symbolsMode ? SYMBOL_ROWS : LETTER_ROWS;

        int rowCount = rows.length;
        float rowHeight = (float) h / rowCount;
        // circumradius sized so hexagon height (2*radius) roughly matches row height
        float radius = rowHeight / 1.5f;
        float hexWidth = (float) Math.sqrt(3) * radius;

        for (int r = 0; r < rowCount; r++) {
            String[] tokens = rows[r];
            float cy = rowHeight * r + rowHeight / 2f;
            int count = tokens.length;

            // last (function) row spans the full width evenly regardless of count
            boolean functionRow = (r == rowCount - 1);
            float startX;
            float spacing;
            if (functionRow) {
                spacing = (float) w / count;
                startX = spacing / 2f;
            } else {
                spacing = hexWidth;
                float totalWidth = spacing * count;
                startX = (w - totalWidth) / 2f + spacing / 2f;
                // offset alternating rows to create the honeycomb interlock
                if (r % 2 == 1) {
                    startX += spacing / 2f;
                }
            }

            for (int i = 0; i < count; i++) {
                String token = tokens[i];
                HexKey key = tokenToKey(token);
                key.cx = startX + spacing * i;
                key.cy = cy;
                key.radius = functionRow ? Math.min(spacing, rowHeight) / 1.7f : radius * 0.95f;
                keys.add(key);
            }
        }
        invalidate();
    }

    private HexKey tokenToKey(String token) {
        switch (token) {
            case "#SHIFT":
                return new HexKey(HexKey.Type.SHIFT, "\u21E7");
            case "#BACK":
                return new HexKey(HexKey.Type.BACKSPACE, "\u232B");
            case "#SPACE":
                return new HexKey(HexKey.Type.SPACE, "");
            case "#ENTER":
                return new HexKey(HexKey.Type.ENTER, "\u23CE", true);
            case "#SYMBOLS":
                return new HexKey(HexKey.Type.SYMBOLS, "123");
            case "#ABC":
                return new HexKey(HexKey.Type.SYMBOLS, "ABC");
            case "#EMOJI":
                return new HexKey(HexKey.Type.EMOJI, "\u263A");
            default:
                return new HexKey(HexKey.Type.LETTER, token);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
        for (HexKey key : keys) {
            drawHexKey(canvas, key);
        }
    }

    private void drawHexKey(Canvas canvas, HexKey key) {
        boolean pressed = key == pressedKey;
        boolean special = key.type != HexKey.Type.LETTER;

        if (key.accent) {
            fillPaint.setColor(pressed ? 0xFF1565C0 : 0xFF2979FF);
        } else if (pressed) {
            fillPaint.setColor(0xFFE8EAED);
        } else if (special) {
            fillPaint.setColor(0xFFE3E5E8);
        } else {
            fillPaint.setColor(0xFFFFFFFF);
        }

        Path path = hexagonPath(key.cx, key.cy, key.radius);
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, strokePaint);

        String label = key.type == HexKey.Type.LETTER && caps
                ? key.label.toUpperCase()
                : (key.type == HexKey.Type.LETTER ? key.label.toLowerCase() : key.label);

        if (key.type == HexKey.Type.SPACE) {
            label = "space";
        }

        textPaint.setColor(key.accent ? Color.WHITE : 0xFF202124);
        textPaint.setTextSize(key.radius * (key.type == HexKey.Type.LETTER ? 0.85f : 0.55f));
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = key.cy - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(label, key.cx, textY, textPaint);
    }

    /** Pointy-top hexagon centered at (cx, cy) with the given circumradius. */
    private Path hexagonPath(float cx, float cy, float radius) {
        Path path = new Path();
        for (int i = 0; i < 6; i++) {
            double angleDeg = -90 + 60 * i;
            double angleRad = Math.toRadians(angleDeg);
            float x = (float) (cx + radius * Math.cos(angleRad));
            float y = (float) (cy + radius * Math.sin(angleRad));
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.close();
        return path;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pressedKey = findKeyAt(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                HexKey moved = findKeyAt(x, y);
                if (moved != pressedKey) {
                    pressedKey = moved;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                HexKey released = findKeyAt(x, y);
                pressedKey = null;
                invalidate();
                if (released != null) {
                    handleKey(released);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                pressedKey = null;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private HexKey findKeyAt(float x, float y) {
        HexKey best = null;
        float bestDist = Float.MAX_VALUE;
        for (HexKey key : keys) {
            float dx = x - key.cx;
            float dy = y - key.cy;
            float dist = dx * dx + dy * dy;
            if (dist <= key.radius * key.radius && dist < bestDist) {
                best = key;
                bestDist = dist;
            }
        }
        return best;
    }

    private void handleKey(HexKey key) {
        if (key.type == HexKey.Type.SHIFT) {
            caps = !caps;
            invalidate();
            return;
        }
        if (key.type == HexKey.Type.SYMBOLS) {
            symbolsMode = !symbolsMode;
            layoutKeys(getWidth(), getHeight());
            return;
        }
        if (listener != null) {
            listener.onKey(key);
        }
        // auto-drop caps after one letter, like most keyboards
        if (key.type == HexKey.Type.LETTER && caps) {
            caps = false;
            invalidate();
        }
    }
}
