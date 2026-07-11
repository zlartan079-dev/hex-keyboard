package com.example.hexkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Simple launcher screen that helps the user enable the keyboard
 * and switch to it. No keyboard logic lives here.
 */
public class SetupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText(getString(R.string.app_name));
        title.setTextSize(24);
        title.setPadding(0, 0, 0, pad);
        root.addView(title);

        TextView message = new TextView(this);
        message.setText(getString(R.string.setup_message));
        message.setTextSize(16);
        message.setPadding(0, 0, 0, pad);
        root.addView(message);

        Button enableButton = new Button(this);
        enableButton.setText(getString(R.string.enable_button));
        enableButton.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        root.addView(enableButton);

        Button switchButton = new Button(this);
        switchButton.setText(getString(R.string.switch_button));
        switchButton.setOnClickListener(v -> {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showInputMethodPicker();
            }
        });
        root.addView(switchButton);

        setContentView(root);
    }
}
