package com.example.hexkeyboard;

/** A single hexagonal key: its position, size, label and behavior type. */
public class HexKey {

    public enum Type { LETTER, SHIFT, BACKSPACE, SPACE, ENTER, SYMBOLS, EMOJI }

    public final Type type;
    public final String label;   // what's drawn / committed for LETTER keys
    public float cx, cy;         // center, set by the view during layout
    public float radius;         // circumradius, set by the view during layout
    public boolean accent;       // true = highlighted (e.g. enter key)

    public HexKey(Type type, String label) {
        this.type = type;
        this.label = label;
    }

    public HexKey(Type type, String label, boolean accent) {
        this.type = type;
        this.label = label;
        this.accent = accent;
    }

    /** Distance-based hit test against the key's center. */
    public boolean contains(float x, float y) {
        float dx = x - cx;
        float dy = y - cy;
        return (dx * dx + dy * dy) <= (radius * radius);
    }
}
