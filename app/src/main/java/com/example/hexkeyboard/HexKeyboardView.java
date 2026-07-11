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

public class HexKeyboardView extends View {

    public interface OnKeyListener {
        void onKey(HexKey key);
    }

    private static final String[] COUNTRIES = {
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola",
            "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria",
            "Azerbaijan",
            "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus",
            "Belgium", "Belize", "Benin", "Bhutan", "Bolivia",
            "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria",
            "Burkina Faso", "Burundi",
            "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic (CAR)",
            "Chad", "Chile", "China", "Colombia", "Comoros",
            "Congo, Democratic Republic of the", "Congo, Republic of the", "Costa Rica", "Cote d'Ivoire", "Croatia",
            "Cuba", "Cyprus", "Czechia (Czech Republic)",
            "Denmark", "Djibouti", "Dominica", "Dominican Republic",
            "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
            "Estonia", "Eswatini", "Ethiopia",
            "Fiji", "Finland", "France",
            "Gabon", "Gambia", "Georgia", "Germany", "Ghana",
            "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau",
            "Guyana",
            "Haiti", "Honduras", "Hungary",
            "Iceland", "India", "Indonesia", "Iran", "Iraq",
            "Ireland", "Israel", "Italy",
            "Jamaica", "Japan", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait",
            "Kyrgyzstan",
            "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia",
            "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
            "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali",
            "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico",
            "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro",
            "Morocco", "Mozambique", "Myanmar (Burma)",
            "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand",
            "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia",
            "Norway",
            "Oman",
            "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea",
            "Paraguay", "Peru", "Philippines", "Poland", "Portugal",
            "Qatar",
            "Romania", "Russia", "Rwanda",
            "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino",
            "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles",
            "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
            "Somalia", "South Africa", "South Korea", "South Sudan", "Spain",
            "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland",
            "Syria",
            "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste",
            "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
            "Turkmenistan", "Tuvalu",
            "Uganda", "Ukraine", "United Arab Emirates (UAE)", "United Kingdom (UK)", "United States of America (USA)",
            "Uruguay", "Uzbekistan",
            "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
            "Yemen",
            "Zambia", "Zimbabwe"
    };

    private static final String[] LETTER_FUNCTION_ROW =
            {"#SHIFT", "#BACK", "#SYMBOLS", "#SPACE", "#ENTER", "#RESET"};

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
    private int countryIndex = 0;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        float density = getResources().getDisplayMetrics().density;
        int desiredHeight = (int) (260 * density);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutKeys(w, h);
    }

    private void layoutKeys(int w, int h) {
        keys.clear();
        if (symbolsMode) {
            layoutGrid(SYMBOL_ROWS, w, h);
        } else {
            layoutSequential(w, h);
        }
        invalidate();
    }

    private void layoutSequential(int w, int h) {
        float bigAreaHeight = h * 0.65f;
        float radius = Math.min(w * 0.46f, bigAreaHeight * 0.46f);

        HexKey big = new HexKey(HexKey.Type.LETTER, COUNTRIES[countryIndex]);
        big.sequential = true;
        big.cx = w / 2f;
        big.cy = bigAreaHeight / 2f;
        big.radius = radius;
        keys.add(big);

        float rowY = bigAreaHeight + (h - bigAreaHeight) / 2f;
        float spacing = (float) w / LETTER_FUNCTION_ROW.length;
        float startX = spacing / 2f;
        float funcRadius = Math.min(spacing, h - bigAreaHeight) / 1.7f;

        for (int i = 0; i < LETTER_FUNCTION_ROW.length; i++) {
            HexKey key = tokenToKey(LETTER_FUNCTION_ROW[i]);
            key.cx = startX + spacing * i;
            key.cy = rowY;
            key.radius = funcRadius;
            keys.add(key);
        }
    }

    private void layoutGrid(String[][] rows, int w, int h) {
        int rowCount = rows.length;
        float rowHeight = (float) h / rowCount;
        float radius = rowHeight / 1.5f;
        float hexWidth = (float) Math.sqrt(3) * radius;

        for (int r = 0; r < rowCount; r++) {
            String[] tokens = rows[r];
            float cy = rowHeight * r + rowHeight / 2f;
            int count = tokens.length;

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
                if (r % 2 == 1) {
                    startX += spacing / 2f;
                }
            }

            for (int i = 0; i < count; i++) {
                HexKey key = tokenToKey(tokens[i]);
                key.cx = startX + spacing * i;
                key.cy = cy;
                key.radius = functionRow ? Math.min(spacing, rowHeight) / 1.7f : radius * 0.95f;
                keys.add(key);
            }
        }
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
            case "#RESET":
                return new HexKey(HexKey.Type.RESET, "\u21BA");
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

        textPaint.setColor(key.accent ? Color.WHITE : 0xFF202124);

        if (key.sequential) {
            drawFittedLabel(canvas, key.label, key.cx, key.cy, key.radius);
            return;
        }

        String label = (key.type == HexKey.Type.LETTER && caps)
                ? key.label.toUpperCase()
                : (key.type == HexKey.Type.LETTER ? key.label.toLowerCase() : key.label);
        if (key.type == HexKey.Type.SPACE) {
            label = "space";
        }
        textPaint.setTextSize(key.radius * (key.type == HexKey.Type.LETTER ? 0.85f : 0.55f));
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = key.cy - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(label, key.cx, textY, textPaint);
    }

    private void drawFittedLabel(Canvas canvas, String text, float cx, float cy, float radius) {
        float maxWidth = radius * 1.55f;
        float maxHeight = radius * 1.3f;
        float size = radius * 0.42f;
        float minSize = 14f;
        List<String> lines;

        while (true) {
            textPaint.setTextSize(size);
            lines = wrapText(text, maxWidth);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float lineHeight = fm.descent - fm.ascent;
            float totalHeight = lineHeight * lines.size();
            if (totalHeight <= maxHeight || size <= minSize) break;
            size -= 2f;
        }

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float lineHeight = fm.descent - fm.ascent;
        float totalHeight = lineHeight * lines.size();
        float startY = cy - totalHeight / 2f - fm.ascent;
        for (int i = 0; i < lines.size(); i++) {
            canvas.drawText(lines.get(i), cx, startY + i * lineHeight, textPaint);
        }
    }

    private List<String> wrapText(String text, float maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (line.length() == 0 || textPaint.measureText(candidate) <= maxWidth) {
                line = new StringBuilder(candidate);
            } else {
                lines.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

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
        if (key.type == HexKey.Type.RESET) {
            countryIndex = 0;
            layoutKeys(getWidth(), getHeight());
            return;
        }

        if (listener != null) {
            listener.onKey(key);
        }

        if (key.type == HexKey.Type.LETTER && key.sequential) {
            if (countryIndex < COUNTRIES.length - 1) {
                countryIndex++;
                layoutKeys(getWidth(), getHeight());
            }
        }
    }
            }
