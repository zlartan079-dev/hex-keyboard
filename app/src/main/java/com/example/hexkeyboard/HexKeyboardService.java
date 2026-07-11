package com.example.hexkeyboard;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class HexKeyboardService extends InputMethodService
        implements HexKeyboardView.OnKeyListener {

    private HexKeyboardView keyboardView;

    @Override
    public View onCreateInputView() {
        keyboardView = new HexKeyboardView(this, null);
        keyboardView.setOnKeyListener(this);
        int heightPx = (int) (260 * getResources().getDisplayMetrics().density);
        keyboardView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, heightPx));
        return keyboardView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
    }

    @Override
    public void onKey(HexKey key) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (key.type) {
            case LETTER:
                String text = keyboardView.isCaps()
                        ? key.label.toUpperCase()
                        : key.label.toLowerCase();
                ic.commitText(text, 1);
                break;
            case SPACE:
                ic.commitText(" ", 1);
                break;
            case BACKSPACE:
                ic.deleteSurroundingText(1, 0);
                break;
            case ENTER:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            case EMOJI:
                ic.commitText("\uD83D\uDE00", 1);
                break;
            default:
                break;
        }
    }
        }
