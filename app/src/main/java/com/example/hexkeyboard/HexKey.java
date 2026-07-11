package com.example.hexkeyboard;

public class HexKey {

    public enum Type { LETTER, SHIFT, BACKSPACE, SPACE, ENTER, SYMBOLS, EMOJI, RESET }

    public final Type type;
    public final String label;
    public float cx, cy;
    public float radius;
    public boolean accent;
    public boolean sequential;

    public HexKey(Type type, String label) {
        this.type = type;
        this.label = label;
    }

    public HexKey(Type type, String label, boolean accent) {
        this.type = type;
        this.label = label;
        this.accent = accent;
    }

    public boolean contains(float x, float y) {
        float dx = x - cx;
        float dy = y - cy;
        return (dx * dx + dy * dy) <= (radius * radius);
    }
}
